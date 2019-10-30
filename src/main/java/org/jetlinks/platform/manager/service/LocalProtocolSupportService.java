package org.jetlinks.platform.manager.service;

import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.hswebframework.web.exception.NotFoundException;
import org.jetlinks.platform.manager.entity.ProtocolSupportEntity;
import org.jetlinks.supports.protocol.management.ProtocolSupportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LocalProtocolSupportService extends GenericReactiveCrudService<ProtocolSupportEntity, String> {

    @Autowired
    private ProtocolSupportManager supportManager;

    public Mono<Boolean> deploy(String id) {
        return findById(Mono.just(id))
                .switchIfEmpty(Mono.error(NotFoundException::new))
                .map(ProtocolSupportEntity::toDeployDefinition)
                .flatMap(supportManager::save)
                .flatMap(r -> createUpdate()
                        .set(ProtocolSupportEntity::getState, 1)
                        .where(ProtocolSupportEntity::getId, id)
                        .execute())
                .map(i -> i > 0);
    }

    public Mono<Boolean> unDeploy(String id) {
        return findById(Mono.just(id))
                .switchIfEmpty(Mono.error(NotFoundException::new))
                .map(ProtocolSupportEntity::toUnDeployDefinition)
                .flatMap(supportManager::save)
                .flatMap(r -> createUpdate()
                        .set(ProtocolSupportEntity::getState, 0)
                        .where(ProtocolSupportEntity::getId, id)
                        .execute())
                .map(i -> i > 0);
    }

}
