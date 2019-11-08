package org.jetlinks.platform.manager.service;

import org.hswebframework.web.cache.ReactiveCache;
import org.hswebframework.web.crud.service.EnableCacheReactiveCrudService;
import org.hswebframework.web.crud.service.GenericReactiveCacheSupportCrudService;
import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.jetlinks.platform.manager.entity.MqttClientEntity;
import org.springframework.stereotype.Service;

@Service
public class MqttClientService extends GenericReactiveCacheSupportCrudService<MqttClientEntity,String> {


    @Override
    public String getCacheName() {
        return "mqtt-client";
    }
}
