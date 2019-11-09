package org.jetlinks.platform.manager.service;

import org.hswebframework.ezorm.rdb.mapping.defaults.SaveResult;
import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.jetlinks.platform.manager.entity.EmailSenderEntity;
import org.jetlinks.platform.manager.notify.email.DefaultEmailSenderManager;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EmailSenderService extends GenericReactiveCrudService<EmailSenderEntity, String> {


    @Autowired
    private DefaultEmailSenderManager senderManager;

    @Override
    public Mono<SaveResult> save(Publisher<EmailSenderEntity> entityPublisher) {
        return Flux.from(entityPublisher)
                .doOnNext(e -> senderManager.refreshCache(e.getId()))
                .as(super::save);
    }

    @Override
    public Mono<Integer> updateById(String id, Mono<EmailSenderEntity> entityPublisher) {
        return super.updateById(id, entityPublisher)
                .doOnSuccess(i-> senderManager.refreshCache(id));
    }

    // TODO: 2019/11/9 修改为事件发送
    @Override
    public Mono<Integer> deleteById(Publisher<String> idPublisher) {
        return Flux.from(idPublisher)
                .doOnNext(id -> senderManager.refreshCache(id))
                .as(super::deleteById);
    }
}
