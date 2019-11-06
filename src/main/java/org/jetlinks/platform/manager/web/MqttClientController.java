package org.jetlinks.platform.manager.web;

import org.hswebframework.ezorm.rdb.mapping.defaults.SaveResult;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.jetlinks.platform.manager.entity.MqttClientEntity;
import org.jetlinks.platform.manager.service.LocalDeviceProductService;
import org.jetlinks.platform.manager.service.MqttClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/mqtt-client")
@Resource(id = "mqtt-client", name = "mqtt客户端")
public class MqttClientController implements ReactiveServiceCrudController<MqttClientEntity, String> {

    @Autowired
    private MqttClientService mqttClientService;

    @Override
    public MqttClientService getService() {
        return mqttClientService;
    }


    @PostMapping
    public Mono<MqttClientEntity> add(@RequestBody Mono<MqttClientEntity> payload) {
        return payload.flatMap(entity -> {
            if (StringUtils.isEmpty(entity.getClientId())){
                entity.setClientId(IDGenerator.MD5.generate());
            }
            return mqttClientService
                    .insert(Mono.just(entity))
                    .thenReturn(entity);
        });

    }
}
