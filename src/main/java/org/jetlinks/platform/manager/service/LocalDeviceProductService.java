package org.jetlinks.platform.manager.service;

import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.hswebframework.web.exception.BusinessException;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.device.ProductInfo;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.jetlinks.platform.manager.enums.DeviceProductState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class LocalDeviceProductService extends GenericReactiveCrudService<DeviceProductEntity, String> {


    @Autowired
    private DeviceRegistry registry;


//    public Flux<DeviceProductEntity> queryRegisteredDeviceProduct() {
////        return createQuery()
////                .where()
////                .fetch()
////                .filter(productEntity -> !productEntity.getState().equals(DeviceProductState.registered.getValue()));
////    }

    public Mono<Integer> deploy(String id) {
        return findById(Mono.just(id))
                .flatMap(product -> registry.registry(new ProductInfo(id, product.getMessageProtocol(), product.getMetadata()))
                        .flatMap(deviceProductOperator -> deviceProductOperator.setConfigs(product.getSecurity()))
                        .doOnNext(re -> {
                            if (!re) {
                                throw new BusinessException("设置设备型号安全配置错误");
                            }
                        }).flatMap(re -> createUpdate()
                                .set(DeviceProductEntity::getState, DeviceProductState.registered.getValue())
                                .where(DeviceProductEntity::getId, id)
                                .execute()));
    }

    public Mono<Integer> cancelDeploy(String id){
        return findById(Mono.just(id))
                .flatMap(product -> registry.unRegistry(id)
                        .thenReturn(true)
                        .flatMap(re -> createUpdate()
                                .set(DeviceProductEntity::getState, DeviceProductState.unregistered.getValue())
                                .where(DeviceProductEntity::getId, id)
                                .execute()));
    }

}
