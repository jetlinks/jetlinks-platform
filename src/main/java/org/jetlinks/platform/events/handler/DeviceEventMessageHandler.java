package org.jetlinks.platform.events.handler;

import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.event.GenericsPayloadApplicationEvent;
import org.hswebframework.web.exception.NotFoundException;
import org.jetlinks.core.Value;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.message.*;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.property.ReadPropertyMessageReply;
import org.jetlinks.core.message.property.WritePropertyMessageReply;
import org.jetlinks.core.metadata.DataType;
import org.jetlinks.core.metadata.DeviceMetadata;
import org.jetlinks.core.metadata.EventMetadata;
import org.jetlinks.core.metadata.PropertyMetadata;
import org.jetlinks.core.metadata.types.DateTimeType;
import org.jetlinks.core.metadata.types.NumberType;
import org.jetlinks.core.metadata.types.UnknownType;
import org.jetlinks.platform.events.DeviceMessageEvent;
import org.jetlinks.platform.events.GaugePropertyEvent;
import org.jetlinks.platform.manager.elasticsearch.index.DeviceEventIndexProvider;
import org.jetlinks.platform.manager.elasticsearch.index.ElasticIndexProvider;
import org.jetlinks.platform.manager.elasticsearch.save.SaveService;
import org.jetlinks.platform.manager.entity.DevicePropertiesEntity;
import org.jetlinks.platform.manager.enums.DeviceLogType;
import org.jetlinks.platform.manager.logger.DeviceOperationLog;
import org.jetlinks.platform.manager.service.LocalDevicePropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Component
@Slf4j
public class DeviceEventMessageHandler {

    @Autowired
    private LocalDevicePropertiesService propertiesService;

    @Autowired
    private SaveService saveService;

    @Autowired
    private DeviceRegistry registry;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @EventListener
    public void dispatchDeviceMessage(Message message) {
        eventPublisher.publishEvent(new GenericsPayloadApplicationEvent<>(this, new DeviceMessageEvent<>(message), message.getClass()));
    }

    @EventListener
    public void handleChildrenDeviceMessage(DeviceMessageEvent<ChildDeviceMessageReply> event) {
        ChildDeviceMessageReply reply = event.getMessage();
        Message message = reply.getChildDeviceMessage();
        if (message instanceof EventMessage) {
            handleDeviceEvent(((EventMessage) message));
        } else if (message instanceof DeviceOnlineMessage || message instanceof DeviceOfflineMessage) {
            String deviceId = ((CommonDeviceMessage) message).getDeviceId();
            //子设备上线

        }
        // TODO: 2019-11-01 更多消息类型处理

    }

    @EventListener
    public void handleEvent(DeviceMessageEvent<EventMessage> event) {
        handleDeviceEvent(event.getMessage());
    }

    public void handleDeviceEvent(EventMessage message) {
        //属性上报
        boolean isReportProperty = message.getHeader("report-property")
                .filter(Boolean.TRUE::equals)
                .map(Boolean.class::cast)
                .orElse(false);
        if (isReportProperty) {
            syncDeviceProperty(message.getDeviceId(), ((Map) message.getData()), new Date(message.getTimestamp()));
        } else {
            syncEvent(message.getDeviceId(), message);
        }
        DeviceOperationLog deviceOperationType = DeviceOperationLog.builder()
                .deviceId(message.getDeviceId())
                .createTime(new Date(message.getTimestamp()))
                .content(message.getData())
                .type(isReportProperty ? DeviceLogType.reportProperty : DeviceLogType.event)
                .build();
        eventPublisher.publishEvent(deviceOperationType);
    }

    @EventListener
    public void handleWriteProperty(DeviceMessageEvent<WritePropertyMessageReply> event) {
        WritePropertyMessageReply message = event.getMessage();
        if (message.isSuccess()) {
            syncDeviceProperty(message.getDeviceId(), message.getProperties(), new Date(message.getTimestamp()));
        }
        DeviceOperationLog deviceOperationType = DeviceOperationLog.builder()
                .deviceId(message.getDeviceId())
                .createTime(new Date(message.getTimestamp()))
                .content(message.getProperties())
                .type(DeviceLogType.writeProperty)
                .build();
        eventPublisher.publishEvent(deviceOperationType);

    }

    @EventListener
    public void handleReadProperty(DeviceMessageEvent<ReadPropertyMessageReply> event) {

        ReadPropertyMessageReply message = event.getMessage();
        if (message.isSuccess()) {
            syncDeviceProperty(message.getDeviceId(), message.getProperties(), new Date(message.getTimestamp()));
        }
        DeviceOperationLog deviceOperationType = DeviceOperationLog.builder()
                .deviceId(message.getDeviceId())
                .createTime(new Date(message.getTimestamp()))
                .content(message.getProperties())
                .type(DeviceLogType.readProperty)
                .build();
        eventPublisher.publishEvent(deviceOperationType);

    }


    private void syncEvent(String device, EventMessage message) {


        registry.getDevice(device)
                .flatMap(deviceOperator ->
                        Mono.zip(
                                deviceOperator.getConfig("productId")
                                        .map(Value::asString)
                                        .switchIfEmpty(Mono.just("")),
                                deviceOperator.getMetadata()))
                .switchIfEmpty(Mono.error(new NotFoundException("保存设备事件失败,注册中心设备：" + device + " 元数据或设备型号为空")))
                .flatMap(tuple2 -> {
                    DeviceMetadata metadata = tuple2.getT2();
                    Object value = message.getData();
                    DataType dataType = metadata
                            .getEvent(message.getEvent())
                            .map(EventMetadata::getType)
                            .orElseGet(UnknownType::new);

                    Map<String, Object> data = new HashMap<>();
                    data.put("deviceId", device);
                    data.put("productId", tuple2.getT1());
                    data.put("createTime", ValueTypeTranslator.dateFormatTranslator(new Date(message.getTimestamp())));
                    Object tempValue = ValueTypeTranslator.translator(value, dataType);
                    if (tempValue instanceof Map) {
                        data.putAll(((Map) tempValue));
                    } else {
                        data.put("value", tempValue);
                    }
                    return saveService.asyncSave(data, new DeviceEventIndexProvider(tuple2.getT1(), message.getEvent()));
                })
                .doOnError(ex -> log.error("保存设备事件失败", ex))
                .subscribe(s -> log.info("保存设备事件成功"));

    }

    private void syncDeviceProperty(String device, Map<String, Object> properties, Date time) {
        registry.getDevice(device)
                .flatMap(DeviceOperator::getMetadata)
                .flatMap(metadata -> {

                    Map<String, PropertyMetadata> propertyMetadata = metadata.getProperties().stream()
                            .collect(Collectors.toMap(PropertyMetadata::getId, Function.identity()));

                    List<DevicePropertiesEntity> entities = properties.entrySet()
                            .stream()
                            .map(entry -> {
                                DevicePropertiesEntity entity = new DevicePropertiesEntity();
                                entity.setDeviceId(device);
                                entity.setUpdateTime(time);
                                entity.setProperty(entry.getKey());
                                entity.setPropertyName(entry.getKey());
                                ofNullable(propertyMetadata.get(entry.getKey()))
                                        .ifPresent(prop -> {
                                            DataType type = prop.getValueType();
                                            entity.setPropertyName(prop.getName());
                                            if (type instanceof NumberType) {
                                                NumberType numberType = (NumberType) type;
                                                entity.setNumberValue(new BigDecimal(numberType.convert(entry.getValue()).toString()));
                                                eventPublisher.publishEvent(GaugePropertyEvent.builder()
                                                        .deviceId(device)
                                                        .propertyName(entry.getKey())
                                                        .propertyValue(entry.getValue())
                                                        .build()
                                                );
                                            } else if (type instanceof DateTimeType) {
                                                DateTimeType dateTimeType = (DateTimeType) type;
                                                entity.setNumberValue(new BigDecimal(dateTimeType.convert(entry.getValue()).toString()));
                                            } else {
                                                entity.setStringValue(String.valueOf(entry.getValue()));
                                            }
                                            entity.setValue(entry.getValue().toString());
                                            ofNullable(type.format(entry.getValue()))
                                                    .map(String::valueOf)
                                                    .ifPresent(entity::setFormatValue);
                                        });
                                return entity;
                            })
                            .peek(processor::onNext)
                            .collect(Collectors.toList());
                    return syncDeviceProperty(entities);
                })
                .doOnError(ex -> log.error("保存设备属性记录失败", ex))
                .subscribe(s -> log.info("保存设备属性记录成功"));

    }

    private EmitterProcessor<DevicePropertiesEntity> processor = EmitterProcessor.create();


    @PostConstruct
    @SuppressWarnings("all")
    public void init() {

        processor.subscribe(entity -> {
            propertiesService
                    .createUpdate()
                    .set(entity)
                    .where(entity::getDeviceId)
                    .and(entity::getProperty)
                    .execute()
                    .filter(i -> i != 0)
                    .switchIfEmpty(propertiesService.insert(Mono.just(entity)))
                    .subscribe(i -> {
                        log.debug("同步设备属性成功:{}", entity);
                    });
        });
    }

    private Mono<Boolean> syncDeviceProperty(List<DevicePropertiesEntity> list) {
        return saveService.asyncBulkSave(list, ElasticIndexProvider.createIndex("device_properties", "doc"));
    }

}
