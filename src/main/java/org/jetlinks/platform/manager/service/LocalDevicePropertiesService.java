package org.jetlinks.platform.manager.service;

import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.platform.manager.entity.DevicePropertiesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class LocalDevicePropertiesService extends GenericReactiveCrudService<DevicePropertiesEntity, String> {


    public Flux<DevicePropertiesEntity> findByDeviceId(String deviceId) {
        return createQuery().where(DevicePropertiesEntity::getDeviceId, deviceId).fetch();
    }
}
