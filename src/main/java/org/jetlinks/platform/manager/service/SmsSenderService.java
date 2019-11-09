package org.jetlinks.platform.manager.service;

import org.hswebframework.ezorm.rdb.mapping.defaults.SaveResult;
import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.jetlinks.platform.events.SaveSmsSenderSuccessEvent;
import org.jetlinks.platform.manager.entity.SmsSenderEntity;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
@Service
public class SmsSenderService extends GenericReactiveCrudService<SmsSenderEntity, String> {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<SaveResult> save(Publisher<SmsSenderEntity> entityPublisher) {

        return Flux.from(entityPublisher)
                .doOnNext(e-> eventPublisher.publishEvent(new SaveSmsSenderSuccessEvent(e.getId())))
                .as(super::save);
    }

    @Override
    public Mono<Integer> updateById(String id, Mono<SmsSenderEntity> entityPublisher) {
        return super.updateById(id,entityPublisher)
                .doOnSuccess((r)->{
                    eventPublisher.publishEvent(new SaveSmsSenderSuccessEvent(id));
                });
    }

    @Override
    public Mono<Integer> deleteById(Publisher<String> idPublisher) {
        return Flux.from(idPublisher)
                .doOnNext(id -> eventPublisher.publishEvent(new SaveSmsSenderSuccessEvent(id)))
                .as(super::deleteById);
    }
}
