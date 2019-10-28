package org.jetlinks.platform.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.event.GenericsPayloadApplicationEvent;
import org.jetlinks.core.ProtocolSupports;
import org.jetlinks.core.cluster.ClusterManager;
import org.jetlinks.core.defaults.CompositeProtocolSupports;
import org.jetlinks.core.device.DeviceOperationBroker;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.message.codec.DefaultTransport;
import org.jetlinks.core.server.MessageHandler;
import org.jetlinks.core.server.monitor.GatewayServerMetrics;
import org.jetlinks.core.server.monitor.GatewayServerMonitor;
import org.jetlinks.core.server.session.DeviceSessionManager;
import org.jetlinks.core.spi.ServiceContext;
import org.jetlinks.platform.events.DeviceConnectedEvent;
import org.jetlinks.platform.events.DeviceDisconnectedEvent;
import org.jetlinks.platform.events.DeviceMessageEvent;
import org.jetlinks.supports.cluster.ClusterDeviceOperationBroker;
import org.jetlinks.supports.cluster.ClusterDeviceRegistry;
import org.jetlinks.supports.cluster.redis.RedisClusterManager;
import org.jetlinks.supports.official.JetLinksAuthenticator;
import org.jetlinks.supports.official.JetLinksDeviceMetadataCodec;
import org.jetlinks.supports.official.JetLinksMQTTDeviceMessageCodec;
import org.jetlinks.supports.protocol.CompositeProtocolSupport;
import org.jetlinks.supports.protocol.ServiceLoaderProtocolSupports;
import org.jetlinks.supports.protocol.StaticProtocolSupports;
import org.jetlinks.supports.protocol.management.ClusterProtocolSupportManager;
import org.jetlinks.supports.protocol.management.ManagementProtocolSupports;
import org.jetlinks.supports.protocol.management.ProtocolSupportLoader;
import org.jetlinks.supports.protocol.management.ProtocolSupportManager;
import org.jetlinks.supports.protocol.management.jar.JarProtocolSupportLoader;
import org.jetlinks.supports.server.DefaultClientMessageHandler;
import org.jetlinks.supports.server.DefaultDecodedClientMessageHandler;
import org.jetlinks.supports.server.DefaultSendToDeviceMessageHandler;
import org.jetlinks.supports.server.monitor.MicrometerGatewayServerMetrics;
import org.jetlinks.supports.server.session.DefaultDeviceSessionManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Mono;
import reactor.extra.processor.WorkQueueProcessor;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@EnableConfigurationProperties(JetLinksProperties.class)
@Slf4j
public class JetLinksConfiguration {

//    @Bean
//    public StandaloneDeviceMessageBroker deviceMessageHandler() {
//        return new StandaloneDeviceMessageBroker(EmitterProcessor.create(false));
//    }

    @Bean
    public ClusterDeviceOperationBroker clusterDeviceOperationBroker(ClusterManager clusterManager) {
        return new ClusterDeviceOperationBroker(clusterManager);
    }

    @Bean
    public ClusterManager clusterManager(ReactiveRedisTemplate<Object, Object> template) {
        return new RedisClusterManager("default", "test", template);
    }

    @Bean
    public DeviceRegistry deviceRegistry(ProtocolSupports supports, ClusterManager manager, DeviceOperationBroker handler) {
        return new ClusterDeviceRegistry(supports, manager, handler);
    }

    @Bean
    public DefaultDecodedClientMessageHandler defaultDecodedClientMessageHandler(MessageHandler handler, ApplicationEventPublisher eventPublisher) {
        DefaultDecodedClientMessageHandler clientMessageHandler = new DefaultDecodedClientMessageHandler(handler);
        clientMessageHandler.subscribe()
                .onBackpressureBuffer(Duration.ofSeconds(30), 1024, message -> {
                    log.warn("无法处理更多消息:{}", message);
                })
                .subscribe(msg -> {
                    //转发消息到spring event
                    eventPublisher.publishEvent(new GenericsPayloadApplicationEvent<>(
                            clientMessageHandler,
                            new DeviceMessageEvent<>(msg),
                            msg.getClass()));
                });

        return clientMessageHandler;
    }

    @Bean(initMethod = "startup")
    public DefaultSendToDeviceMessageHandler defaultSendToDeviceMessageHandler(JetLinksProperties properties,
                                                                               DeviceSessionManager sessionManager,
                                                                               MessageHandler messageHandler) {
        return new DefaultSendToDeviceMessageHandler(properties.getServerId(), sessionManager, messageHandler);
    }

    @Bean
    public DefaultClientMessageHandler defaultClientMessageHandler(DefaultDecodedClientMessageHandler handler) {
        return new DefaultClientMessageHandler(handler);
    }


    @Bean
    public GatewayServerMonitor gatewayServerMonitor(JetLinksProperties properties, MeterRegistry registry) {
        GatewayServerMetrics metrics = new MicrometerGatewayServerMetrics(properties.getServerId(), registry);

        return new GatewayServerMonitor() {
            @Override
            public String getCurrentServerId() {
                return properties.getServerId();
            }

            @Override
            public GatewayServerMetrics metrics() {
                return metrics;
            }
        };
    }

    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public DefaultDeviceSessionManager deviceSessionManager(JetLinksProperties properties,
                                                            GatewayServerMonitor monitor,
                                                            ScheduledExecutorService executorService,
                                                            ApplicationEventPublisher eventPublisher) {
        DefaultDeviceSessionManager sessionManager = new DefaultDeviceSessionManager();
        sessionManager.setExecutorService(executorService);
        sessionManager.setGatewayServerMonitor(monitor);

        Optional.ofNullable(properties.getTransportLimit()).ifPresent(sessionManager::setTransportLimits);

        sessionManager.onRegister().subscribe(session -> eventPublisher.publishEvent(new DeviceConnectedEvent(session)));
        sessionManager.onUnRegister().subscribe(session -> eventPublisher.publishEvent(new DeviceDisconnectedEvent(session)));

        return sessionManager;
    }

    @Bean(initMethod = "init")
    public ServiceLoaderProtocolSupports serviceLoaderProtocolSupports(ServiceContext serviceContext) {
        ServiceLoaderProtocolSupports supports= new ServiceLoaderProtocolSupports();
        supports.setServiceContext(serviceContext);
        return supports;
    }

    @Bean
    public ProtocolSupportManager protocolSupportManager(ClusterManager clusterManager) {
        return new ClusterProtocolSupportManager(clusterManager);
    }

    @Bean
    public JarProtocolSupportLoader jarProtocolSupportLoader(ServiceContext serviceContext) {
        JarProtocolSupportLoader loader = new JarProtocolSupportLoader();
        loader.setServiceContext(serviceContext);
        return loader;
    }


    @Bean
    public LazyInitManagementProtocolSupports managementProtocolSupports(ProtocolSupportManager supportManager,
                                                                         ProtocolSupportLoader loader,
                                                                         ClusterManager clusterManager) {
        LazyInitManagementProtocolSupports supports = new LazyInitManagementProtocolSupports();
        supports.setClusterManager(clusterManager);
        supports.setManager(supportManager);
        supports.setLoader(loader);
        return supports;
    }


    @Bean
    public CompositeProtocolSupport jetLinksProtocolSupport() {
        CompositeProtocolSupport support = new CompositeProtocolSupport();

        support.setId("jet-links");
        support.setName("JetLinks V1.0");
        support.setDescription("JetLinks Protocol Version 1.0");

        support.addAuthenticator(DefaultTransport.MQTT, new JetLinksAuthenticator());
        support.addAuthenticator(DefaultTransport.MQTTS, new JetLinksAuthenticator());
        support.setMetadataCodec(new JetLinksDeviceMetadataCodec());
        JetLinksMQTTDeviceMessageCodec codec = new JetLinksMQTTDeviceMessageCodec();

        support.addMessageCodecSupport(DefaultTransport.MQTT, () -> Mono.just(codec));
        support.addMessageCodecSupport(DefaultTransport.MQTTS, () -> Mono.just(codec));
        return support;
    }

}
