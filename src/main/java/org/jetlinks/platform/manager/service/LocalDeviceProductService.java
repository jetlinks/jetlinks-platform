package org.jetlinks.platform.manager.service;

import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LocalDeviceProductService extends GenericReactiveCrudService<DeviceProductEntity, String> {



    @Autowired
    private DeviceRegistry registry;



}
