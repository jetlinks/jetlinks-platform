package org.jetlinks.platform.manager.notify;

import org.hswebframework.ezorm.rdb.mapping.ReactiveRepository;
import org.jetlinks.platform.manager.entity.SmsSenderEntity;
import org.jetlinks.rule.engine.executor.node.notify.SmsSender;
import org.jetlinks.rule.engine.executor.node.notify.SmsSenderManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ManagedSmsSenderManager implements SmsSenderManager, BeanPostProcessor {

    private Map<String, SmsProvider> providers = new ConcurrentHashMap<>();

    private Map<String, SmsSender> cache = new ConcurrentHashMap<>();

    @Autowired
    private ReactiveRepository<SmsSenderEntity, String> reactiveRepository;

    public void register(SmsProvider provider) {
        providers.put(provider.getProvider(), provider);
    }

    private Mono<SmsSender> createSender(SmsSenderEntity entity) {
        return Mono.justOrEmpty(providers.get(entity.getProvider()))
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("不支持的短信服务商:" + entity.getProvider())))
                .flatMap(smsProvider -> smsProvider.createSender(entity.getConfiguration()))
                .doOnNext(smsSender -> cache.put(entity.getId(), smsSender));
    }

    @Override
    public Mono<SmsSender> getSender(String id) {
        return Mono.justOrEmpty(cache.get(id))
                .switchIfEmpty(Mono.defer(() -> reactiveRepository.findById(Mono.just(id)).flatMap(this::createSender)))
                .switchIfEmpty(Mono.error(() -> new UnsupportedOperationException("不支持的短信发件人:" + id)));
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SmsProvider) {
            register((SmsProvider) bean);
        }
        return bean;
    }
}
