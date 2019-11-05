package org.jetlinks.platform.gateway;

import io.vertx.core.Verticle;
import io.vertx.mqtt.MqttServerOptions;
import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.event.GenericsPayloadApplicationEvent;
import org.jetlinks.core.ProtocolSupports;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.server.monitor.GatewayServerMonitor;
import org.jetlinks.core.server.session.DeviceSessionManager;
import org.jetlinks.gateway.vertx.mqtt.MqttServer;
import org.jetlinks.gateway.vertx.mqtt.VertxMqttGatewayServerContext;
import org.jetlinks.platform.events.DeviceMessageEvent;
import org.jetlinks.supports.server.ClientMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author zhouhao
 * @since 1.0.0
 */
@Component
public class MqttServerVerticleSupplier implements VerticleSupplier, EnvironmentAware {

    @Autowired
    private MqttServerOptions mqttServerOptions;

    @Autowired
    private DeviceRegistry deviceRegistry;

    @Autowired
    private DeviceSessionManager deviceSessionManager;

    @Autowired
    private GatewayServerMonitor gatewayServerMonitor;

    @Autowired
    private ProtocolSupports protocolSupports;

    @Autowired
    private ClientMessageHandler clientMessageHandler;

    @Autowired
    private VertxMqttGatewayServerContext context;

    @Getter
    @Setter
    private String publicServerAddress;

    @Override
    public Verticle get() {
        MqttServer mqttServer = new MqttServer();
        mqttServer.setMqttServerOptions(mqttServerOptions);
        mqttServer.setRegistry(deviceRegistry);
        mqttServer.setGatewayServerMonitor(gatewayServerMonitor);
        mqttServer.setDeviceSessionManager(deviceSessionManager);
        mqttServer.setProtocolSupports(protocolSupports);
        mqttServer.setMessageHandler(clientMessageHandler);
        mqttServer.setServerContext(context);
        return mqttServer;
    }

    @Override
    public void setEnvironment(Environment environment) {
        publicServerAddress = environment.getProperty("vertx.mqtt.public-server-address");
    }
}
