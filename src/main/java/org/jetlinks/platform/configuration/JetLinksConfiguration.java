package org.jetlinks.platform.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.event.GenericsPayloadApplicationEvent;
import org.jetlinks.core.ProtocolSupports;
import org.jetlinks.core.cluster.ClusterManager;
import org.jetlinks.core.device.DeviceOperationBroker;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.message.codec.DefaultTransport;
import org.jetlinks.core.server.MessageHandler;
import org.jetlinks.core.server.monitor.GatewayServerMetrics;
import org.jetlinks.core.server.monitor.GatewayServerMonitor;
import org.jetlinks.core.server.session.DeviceSession;
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
import org.jetlinks.supports.protocol.ServiceLoaderProtocolSupports;
import org.jetlinks.supports.protocol.management.ClusterProtocolSupportManager;
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
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

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

    @Bean(destroyMethod = "shutdown")
    public DefaultDecodedClientMessageHandler defaultDecodedClientMessageHandler(MessageHandler handler,
                                                                                 DeviceSessionManager deviceSessionManager,
                                                                                 ApplicationEventPublisher eventPublisher) {
        DefaultDecodedClientMessageHandler clientMessageHandler = new DefaultDecodedClientMessageHandler(handler,deviceSessionManager);
        clientMessageHandler.subscribe()
                .onBackpressureBuffer(Duration.ofSeconds(30), 1024, message -> {
                    log.warn("无法处理更多消息:{}", message);
                })
                .doOnNext(msg->{
                    //转发消息到spring event
                    eventPublisher.publishEvent(new GenericsPayloadApplicationEvent<>(
                            clientMessageHandler,
                            new DeviceMessageEvent<>(msg),
                            msg.getClass()));
                })
                .metrics()
                .onErrorContinue((err,r)-> log.error(err.getMessage(),err))
                .subscribe();

        return clientMessageHandler;
    }

    @Bean(initMethod = "startup")
    public DefaultSendToDeviceMessageHandler defaultSendToDeviceMessageHandler(JetLinksProperties properties,
                                                                               DeviceSessionManager sessionManager,
                                                                               DeviceRegistry registry,
                                                                               MessageHandler messageHandler) {
        return new DefaultSendToDeviceMessageHandler(properties.getServerId(), sessionManager, messageHandler,registry);
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
                                                            DeviceRegistry registry,
                                                            ScheduledExecutorService executorService,
                                                            ApplicationEventPublisher eventPublisher) {
        DefaultDeviceSessionManager sessionManager = new DefaultDeviceSessionManager();
        sessionManager.setExecutorService(executorService);
        sessionManager.setGatewayServerMonitor(monitor);
        sessionManager.setRegistry(registry);
        Optional.ofNullable(properties.getTransportLimit()).ifPresent(sessionManager::setTransportLimits);

        sessionManager.onRegister()
                .map(DeviceSession::getDeviceId)
                .map(DeviceConnectedEvent::new)
                .doOnNext(eventPublisher::publishEvent)
                .onErrorContinue((err,r)-> log.error(err.getMessage(),err))
                .subscribe();

        sessionManager.onUnRegister()
                .map(DeviceSession::getDeviceId)
                .map(DeviceDisconnectedEvent::new)
                .doOnNext(eventPublisher::publishEvent)
                .onErrorContinue((err,r)-> log.error(err.getMessage(),err))
                .subscribe();

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


}
