package org.jetlinks.platform.configuration;

import org.hswebframework.web.authorization.token.UserTokenManager;
import org.jetlinks.core.ProtocolSupports;
import org.jetlinks.core.device.registry.DeviceMessageHandler;
import org.jetlinks.core.device.registry.DeviceRegistry;
import org.jetlinks.core.message.interceptor.DeviceMessageSenderInterceptor;
import org.jetlinks.core.support.JetLinksProtocolSupport;
import org.jetlinks.platform.events.DeviceConnectedEvent;
import org.jetlinks.platform.events.DeviceDisconnectedEvent;
import org.jetlinks.gateway.monitor.GatewayServerMonitor;
import org.jetlinks.gateway.monitor.LettuceGatewayServerMonitor;
import org.jetlinks.gateway.session.DefaultDeviceSessionManager;
import org.jetlinks.lettuce.LettucePlus;
import org.jetlinks.lettuce.spring.cache.LettuceCacheManager;
import org.jetlinks.lettuce.spring.hsweb.LettuceUserTokenManager;
import org.jetlinks.registry.redis.lettuce.LettuceDeviceMessageHandler;
import org.jetlinks.registry.redis.lettuce.LettuceDeviceRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableConfigurationProperties(JetLinksProperties.class)
public class JetLinksConfiguration {


    @Bean
    public DeviceMessageHandler deviceMessageHandler(LettucePlus lettucePlus) {
        return new LettuceDeviceMessageHandler(lettucePlus);
    }


    @Bean
    public LettuceDeviceRegistry deviceRegistry(LettucePlus lettucePlus, DeviceMessageHandler handler, ProtocolSupports protocolSupports) {
        return new LettuceDeviceRegistry(lettucePlus, handler, protocolSupports);
    }

    @Bean
    public BeanPostProcessor deviceMessageSenderAutoRegister(LettuceDeviceRegistry registry) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
                return o;
            }

            @Override
            public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
                if (o instanceof DeviceMessageSenderInterceptor) {
                    registry.addInterceptor(((DeviceMessageSenderInterceptor) o));
                }
                return o;
            }
        };
    }

    @Bean
    public CacheManager cacheManager(LettucePlus plus) {

        CacheManager cacheManager = new LettuceCacheManager(plus);

        return new TransactionAwareCacheManagerProxy(cacheManager);
    }

    @Bean
    @ConditionalOnProperty(prefix = "jetlinks.redis.user-token", name = "enable", havingValue = "true")
    @ConfigurationProperties(prefix = "jetlinks.authorize")
    public UserTokenManager userTokenManager(LettucePlus plus) {
        return new LettuceUserTokenManager("jetlinks", plus);
    }

    @Bean(initMethod = "startup", destroyMethod = "shutdown")
    public LettuceGatewayServerMonitor gatewayServerMonitor(JetLinksProperties properties, LettucePlus lettucePlus) {

        return new LettuceGatewayServerMonitor(properties.getServerId(), lettucePlus);
    }

    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public DefaultDeviceSessionManager deviceSessionManager(DeviceMessageHandler handler,
                                                            JetLinksProperties properties,
                                                            DeviceRegistry deviceRegistry,
                                                            GatewayServerMonitor monitor,
                                                            ScheduledExecutorService executorService,
                                                            ProtocolSupports protocolSupports,
                                                            ApplicationEventPublisher eventPublisher) {
        DefaultDeviceSessionManager sessionManager = new DefaultDeviceSessionManager();
        sessionManager.setDeviceRegistry(deviceRegistry);
        sessionManager.setDeviceMessageHandler(handler);
        sessionManager.setExecutorService(executorService);
        sessionManager.setProtocolSupports(protocolSupports);
        sessionManager.setGatewayServerMonitor(monitor);

        Optional.ofNullable(properties.getTransportLimit()).ifPresent(conf->conf.forEach(sessionManager::setTransportLimit));

        sessionManager.setOnDeviceRegister(session -> eventPublisher.publishEvent(new DeviceConnectedEvent(session)));
        sessionManager.setOnDeviceUnRegister(session -> eventPublisher.publishEvent(new DeviceDisconnectedEvent(session)));

        return sessionManager;
    }

    @Bean
    public JetLinksProtocolSupport jetLinksProtocolSupport(){
        return new JetLinksProtocolSupport();
    }

}
