package org.jetlinks.platform.manager.service;

import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.jetlinks.platform.manager.enums.DeviceProductState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class LocalDeviceProductService extends GenericReactiveCrudService<DeviceProductEntity, String> {



    @Autowired
    private DeviceRegistry registry;


    public Flux<DeviceProductEntity> queryRegisteredDeviceProduct() {
        return createQuery()
                .where()
                .fetch()
                .filter(productEntity -> productEntity.getState() != DeviceProductState.registered.getValue());
    }
}
