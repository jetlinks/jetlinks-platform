package org.jetlinks.platform.manager.web;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.exception.NotFoundException;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.message.FunctionInvokeMessageSender;
import org.jetlinks.core.message.ReadPropertyMessageSender;
import org.jetlinks.core.message.WritePropertyMessageSender;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.function.FunctionInvokeMessageReply;
import org.jetlinks.core.message.property.ReadPropertyMessageReply;
import org.jetlinks.core.message.property.WritePropertyMessageReply;
import org.jetlinks.platform.events.DeviceMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@RestController
@RequestMapping("/device")
@Slf4j
public class DeviceMessageController {

    @Autowired
    private DeviceRegistry registry;

//    @Autowired
//    private LocalDeviceInstanceService localDeviceInstanceService;


    private Map<String, EmitterProcessor<Object>> eventProcessor = new ConcurrentHashMap<>();

    @EventListener
    @Authorize(ignore = true)
    public void handleDeviceEvent(DeviceMessageEvent<EventMessage> e) {

        Optional.ofNullable(eventProcessor.get(e.getMessage().getDeviceId()))
                .ifPresent(processor -> {
                    if (processor.isCancelled()) {
                        eventProcessor.remove(e.getMessage().getDeviceId());
                        return;
                    }
                    processor.onNext(e.getMessage());
                });

    }

    //获取实时事件
    @GetMapping(value = "/{deviceId}/event", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object> getEvent(@PathVariable String deviceId) {
        return eventProcessor
                .computeIfAbsent(deviceId, __ -> EmitterProcessor.create(100, true))
                .map(Function.identity())
                .doOnCancel(() -> {
                    log.debug("unsubscribe event {}", deviceId);
                });
    }



    //获取设备属性
    @GetMapping("/{deviceId}/property/{property:.+}")
    @SneakyThrows
    public Flux<?> getProperties(@PathVariable String deviceId, @PathVariable String property) {

        return registry
                .getDevice(deviceId)
                .switchIfEmpty(Mono.error(NotFoundException::new))
                .map(DeviceOperator::messageSender)
                .map(sender -> sender.readProperty(property).messageId(IDGenerator.SNOW_FLAKE_STRING.generate()))
                .flatMapMany(ReadPropertyMessageSender::send)
                .map(ReadPropertyMessageReply::getProperties);

    }

    //设置设备属性
    @PostMapping("setting/{deviceId}/property")
    @SneakyThrows
    public Flux<?> settingProperties(@PathVariable String deviceId, @RequestBody Map<String, Object> properties) {

        return registry
                .getDevice(deviceId)
                .switchIfEmpty(Mono.error(NotFoundException::new))
                .map(DeviceOperator::messageSender)
                .map(sender -> sender.writeProperty().messageId(IDGenerator.SNOW_FLAKE_STRING.generate()))
                .map(writePropertyMessageSender -> writePropertyMessageSender.write(properties))
                .flatMapMany(WritePropertyMessageSender::send)
                .map(WritePropertyMessageReply::getProperties);

    }

    //设备功能调用
    @PostMapping("invoked/{deviceId}/functionId/{functionId}")
    @SneakyThrows
    public Flux<?> invokedFunction(@PathVariable String deviceId, @PathVariable String functionId, @RequestBody Map<String, Object> properties) {

        return registry
                .getDevice(deviceId)
                .switchIfEmpty(Mono.error(NotFoundException::new))
                .map(DeviceOperator::messageSender)
                .map(sender -> sender.invokeFunction(functionId).messageId(IDGenerator.SNOW_FLAKE_STRING.generate()))
                .map(functionInvokeMessageSender -> functionInvokeMessageSender.setParameter(properties))
                .flatMapMany(FunctionInvokeMessageSender::send)
                .map(FunctionInvokeMessageReply::getOutput);

    }


    //获取设备所有属性
    @PostMapping("/{deviceId}/properties")
    @SneakyThrows
    public Flux<?> getProperties(@PathVariable String deviceId, @RequestBody Mono<List<String>> properties) {

        return properties
                .flatMapMany(list -> registry
                        .getDevice(deviceId)
                        .switchIfEmpty(Mono.error(NotFoundException::new))
                        .map(DeviceOperator::messageSender)
                        .map(sender -> sender.readProperty(list.toArray(new String[0]))
                                .messageId(IDGenerator.SNOW_FLAKE_STRING.generate()))
                        .flatMapMany(ReadPropertyMessageSender::send)
                        .map(ReadPropertyMessageReply::getProperties));

    }


}
