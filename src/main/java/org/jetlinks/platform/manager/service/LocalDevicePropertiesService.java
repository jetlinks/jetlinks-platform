package org.jetlinks.platform.manager.service;

import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.jetlinks.platform.manager.entity.DevicePropertiesEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LocalDevicePropertiesService extends GenericReactiveCrudService<DevicePropertiesEntity, String> {


    public Flux<DevicePropertiesEntity> getProperties(String deviceId) {
        return createQuery().where(DevicePropertiesEntity::getDeviceId, deviceId).fetch();
    }

    public Mono<DevicePropertiesEntity> getProperty(String deviceId, String property) {
        return createQuery()
                .where(DevicePropertiesEntity::getDeviceId, deviceId)
                .and(DevicePropertiesEntity::getProperty, property)
                .fetchOne()
                .switchIfEmpty(Mono.just(new DevicePropertiesEntity()));
    }
}
