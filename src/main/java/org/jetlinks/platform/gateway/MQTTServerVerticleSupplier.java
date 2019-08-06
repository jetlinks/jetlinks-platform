package org.jetlinks.platform.gateway;

import io.vertx.core.Verticle;
import io.vertx.mqtt.MqttServerOptions;
import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.service.GenericsPayloadApplicationEvent;
import org.jetlinks.core.device.registry.DeviceRegistry;
import org.jetlinks.platform.events.DeviceMessageEvent;
import org.jetlinks.gateway.monitor.GatewayServerMonitor;
import org.jetlinks.gateway.session.DeviceSessionManager;
import org.jetlinks.gateway.vertx.mqtt.MqttServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author zhouhao
 * @since 1.0.0
 */
@Component
public class MQTTServerVerticleSupplier implements VerticleSupplier, EnvironmentAware {

    @Autowired
    private MqttServerOptions mqttServerOptions;

    @Autowired
    private DeviceRegistry deviceRegistry;

    @Autowired
    private DeviceSessionManager deviceSessionManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private GatewayServerMonitor gatewayServerMonitor;

    @Getter
    @Setter
    private String publicServerAddress;

    @Override
    public Verticle get() {
        MqttServer mqttServer = new MqttServer();
        mqttServer.setMqttServerOptions(mqttServerOptions);
        mqttServer.setRegistry(deviceRegistry);
        mqttServer.setGatewayServerMonitor(gatewayServerMonitor);
        mqttServer.setPublicServerAddress(publicServerAddress);
        mqttServer.setMessageConsumer(((deviceClient, message) -> {
            //转发消息到spring event
            eventPublisher.publishEvent(new GenericsPayloadApplicationEvent<>(
                    MQTTServerVerticleSupplier.this,
                    new DeviceMessageEvent<>(deviceClient, message),
                    message.getClass()));
        }));
        mqttServer.setDeviceSessionManager(deviceSessionManager);
        return mqttServer;
    }

    @Override
    public void setEnvironment(Environment environment) {
        publicServerAddress = environment.getProperty("vertx.mqtt.public-server-address", (String) null);
    }
}
