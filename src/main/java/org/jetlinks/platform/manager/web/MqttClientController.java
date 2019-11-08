package org.jetlinks.platform.manager.web;

import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.hswebframework.web.exception.BusinessException;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.core.message.codec.MqttMessage;
import org.jetlinks.core.message.codec.SimpleMqttMessage;
import org.jetlinks.platform.manager.entity.MqttClientEntity;
import org.jetlinks.platform.manager.enums.MqttClientState;
import org.jetlinks.platform.manager.service.LocalMqttClientManager;
import org.jetlinks.platform.manager.service.MqttClientService;
import org.jetlinks.platform.manager.web.request.MqttMessageRequest;
import org.jetlinks.platform.manager.web.response.MqttMessageResponse;
import org.jetlinks.rule.engine.executor.node.mqtt.MqttClient;
import org.jetlinks.rule.engine.executor.node.mqtt.PayloadType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@RestController
@RequestMapping("/mqtt-client")
@Resource(id = "mqtt-client", name = "mqtt客户端")
public class MqttClientController implements ReactiveServiceCrudController<MqttClientEntity, String> {

    @Autowired
    private MqttClientService mqttClientService;

    @Autowired
    private LocalMqttClientManager clientManager;

    @Override
    public MqttClientService getService() {
        return mqttClientService;
    }

    //eventSource

    /**
     * 检查是否存活
     *
     * @param id
     * @return
     */
    @GetMapping("/isAlive/{id}")
    public Mono<Boolean> isAlive(@PathVariable String id) {
        return clientManager.getMqttClient(id)
                .map(MqttClient::isAlive)
                .switchIfEmpty(Mono.just(false));
    }

    /**
     * 获取最后一次错误信息 设备not alive 时调用
     *
     * @param id
     * @return
     */
    @GetMapping("/lastError/{id}")
    public Mono<Throwable> getLastError(@PathVariable String id) {
        return clientManager.getMqttClient(id)
                .map(MqttClient::getLastError);
    }

    /**
     * 停止客户端
     *
     * @param id
     */
    @PostMapping("/stop/{id}")
    public void stopClient(@PathVariable String id) {
        clientManager.stopClient(id);
    }

    /**
     * 订阅消息
     *
     * @param id
     * @param topics
     * @return
     */
    @GetMapping(value = "/subscribe/{id}/{type}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MqttMessageResponse> subscribe(@PathVariable String id, @PathVariable PayloadType type, @RequestParam String topics) {
        return clientManager
                .getMqttClient(id)
                .flatMapMany(client -> client.subscribe(Arrays.asList(topics.split("[\n]"))))
                .map(msg -> MqttMessageResponse.of(msg, type));
    }

    /**
     * 推送消息
     *
     * @param id
     * @param mqttMessage
     * @return
     */
    @PostMapping("/publish/{id}/{type}")
    public Mono<Boolean> publish(@PathVariable String id, @PathVariable PayloadType type, @RequestBody MqttMessageRequest mqttMessage) {
        return clientManager.getMqttClient(id)
                .flatMap(c -> c.publish(MqttMessageRequest.of(mqttMessage, type)));
    }

    @PostMapping("/disable/{id}")
    public Mono<Boolean> disable(@PathVariable String id) {
        clientManager.stopClient(id);
        return mqttClientService.createUpdate()
                .set(MqttClientEntity::getStatus, MqttClientState.registered.getValue())
                .where(MqttClientEntity::getId, id)
                .execute()
                .map(i -> i > 0);
    }

    @PostMapping("/start/{id}")
    public Mono<Boolean> start(@PathVariable String id) {
        return clientManager.getMqttClient(id)
                .filter(MqttClient::isAlive)
                .flatMap(s-> mqttClientService.createUpdate()
                        .set(MqttClientEntity::getStatus, MqttClientState.registered.getValue())
                        .where(MqttClientEntity::getId, id)
                        .execute()
                        .map(i -> i > 0))
                .switchIfEmpty(Mono.just(false));

    }
}
