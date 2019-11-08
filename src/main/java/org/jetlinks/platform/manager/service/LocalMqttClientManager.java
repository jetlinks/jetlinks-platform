package org.jetlinks.platform.manager.service;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClientOptions;
import org.jetlinks.platform.manager.entity.MqttClientEntity;
import org.jetlinks.rule.engine.executor.node.mqtt.vertx.VertxMqttClient;
import org.jetlinks.rule.engine.executor.node.mqtt.vertx.VertxMqttClientManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@Component
public class LocalMqttClientManager extends VertxMqttClientManager {

    @Autowired
    private Vertx vertx;

    @Override
    protected Vertx getVertx() {
        return vertx;
    }

    @Autowired
    private MqttClientService clientService;

    @Override
    public Mono<VertxMqttConfig> getConfig(String id) {
        return clientService
                .findById(Mono.just(id))
                .filter(MqttClientEntity::clientIsEnabled)
                .map(this::convert);
    }

    @Override
    public void stopClient(String id) {
        super.stopClient(id);
    }

    @PostConstruct
    public void init(){
        //每5秒进行连接保活
        Flux.interval(Duration.ofSeconds(5))
                .subscribe(l-> doClientKeepAlive());
    }

    @Override
    public Mono<VertxMqttClient> createMqttClient(VertxMqttConfig config) {
        return super.createMqttClient(config);
    }


    private VertxMqttConfig convert(MqttClientEntity entity) {

        Map<String, Object> secure = entity.getSecureConfiguration();
        Map<String, Object> ssl = entity.getSslConfiguration();

        MqttClientOptions options = new MqttClientOptions();
        options.setClientId(entity.getClientId());

        if (secure != null) {
            options.setUsername((String) secure.get("username"));
            options.setPassword((String) secure.get("password"));
            // TODO: 2019-11-07 其他自定义用户名密码方式处理，比如通过脚本来动态生成
        }
        if (ssl != null) {
            // TODO: 2019-11-06
        }

        return VertxMqttConfig
                .builder()
                .port(entity.getPort())
                .id(entity.getId())
                .host(entity.getHost())
                .port(entity.getPort())
                .options(options)
                .build();

    }
}
