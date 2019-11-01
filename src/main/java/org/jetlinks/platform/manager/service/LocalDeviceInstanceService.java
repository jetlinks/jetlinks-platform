package org.jetlinks.platform.manager.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.crud.generator.Generators;
import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.hswebframework.web.exception.BusinessException;
import org.hswebframework.web.exception.NotFoundException;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.core.device.DeviceConfigKey;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.device.ProductInfo;
import org.jetlinks.core.utils.FluxUtils;
import org.jetlinks.platform.events.DeviceConnectedEvent;
import org.jetlinks.platform.events.DeviceDisconnectedEvent;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.jetlinks.platform.manager.enums.DeviceState;
import org.jetlinks.platform.manager.web.response.DeviceInfo;
import org.jetlinks.platform.manager.web.response.DeviceRunInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LocalDeviceInstanceService extends GenericReactiveCrudService<DeviceInstanceEntity, String> {

    @Autowired
    private DeviceRegistry registry;

    @Autowired
    private LocalDeviceProductService deviceProductService;

    @Autowired
    private LocalDevicePropertiesService propertiesService;

    /**
     * 重置设备安全配置
     *
     * @param deviceId
     * @return
     */
    public Mono<Object> resetSecurityProperties(String deviceId) {
        Map<String, Object> security = new HashMap<>();
        security.put("deviceKey", IDGenerator.MD5.generate());
        security.put("deviceSecret", IDGenerator.MD5.generate());
        security.put("omos", "true");
        return createUpdate()
                .set(DeviceInstanceEntity::getSecurity, security)
                .where(DeviceInstanceEntity::getId, deviceId)
                .execute()
                .flatMap(integer -> {
                    if (integer == 1) {
                        return Mono.just(security);
                    } else {
                        return Mono.error(() -> new SQLException("重置安全参数错误"));
                    }
                });
    }

    public Mono<Integer> deploy(String id) {
        return findById(Mono.just(id))
                .flatMap(instance -> deviceProductService
                        .findById(Mono.just(instance.getProductId()))
                        .flatMap(productEntity -> registry.registry(
                                org.jetlinks.core.device.DeviceInfo.builder()
                                        .id(instance.getId())
                                        .productId(instance.getProductId())
                                        .build())
                                .flatMap(deviceOperator -> deviceOperator.putState((byte) -1)
                                        .flatMap(bool -> {
                                            //设备配置为一机一密时设置安全配置到设备实例
                                            if (productEntity.getSecurity() != null && "true".equals(productEntity.getSecurity().get("omos"))) {
                                                return deviceOperator.setConfig("security", productEntity.getSecurity())
                                                        .map(confBool -> confBool && bool);
                                            } else {
                                                return Mono.just(bool);
                                            }
                                        }))
                                .doOnNext(re -> {
                                    if (!re) {
                                        throw new BusinessException("设置设备实例状态错误");
                                    }
                                }))
                )
                .doOnError(err -> log.error("设备实例发布错误:{}", err))
                .switchIfEmpty(Mono.error(NotFoundException::new))
                .then(deviceDeployUpdate(id));
    }

    private Mono<Integer> deviceDeployUpdate(String deviceId) {
        return createUpdate()
                .set(DeviceInstanceEntity::getRegistryTime, System.currentTimeMillis())
                .set(DeviceInstanceEntity::getState, DeviceState.offline)
                .where(DeviceInstanceEntity::getId, deviceId)
                .execute();
    }

    private volatile FluxSink<String> deviceIdSink;

    public Mono<DeviceRunInfo> getDeviceRunInfo(String deviceId) {
        return registry.getDevice(deviceId)
                .flatMap(deviceOperator -> Mono.zip(
                        deviceOperator.getOnlineTime().switchIfEmpty(Mono.just(0L)),//
                        deviceOperator.getOfflineTime().switchIfEmpty(Mono.just(0L)),//
                        deviceOperator.getState().map(DeviceState::of),//
                        deviceOperator.getConfig(DeviceConfigKey.metadata).switchIfEmpty(Mono.just(""))
                        ).map(tuple4 -> DeviceRunInfo.of(
                        tuple4.getT1(),
                        tuple4.getT2(),
                        tuple4.getT3(),
                        tuple4.getT4()
                        ))
                );
    }


    @PostConstruct
    public void init() {
        syncState(Flux.create(fluxSink -> deviceIdSink = fluxSink), false)
                .doOnError(err -> {
                    log.error(err.getMessage(), err);
                })
                .subscribe((i) -> {
                    log.info("同步设备状态成功:{}", i);
                });
    }

    @EventListener
    public void handleDeviceConnectEvent(DeviceDisconnectedEvent event) {
        if (deviceIdSink != null) {
            deviceIdSink.next(event.getSession().getDeviceId());
        } else {
            syncState(Flux.just(event.getSession().getDeviceId()), false)
                    .doOnError(err -> log.error(err.getMessage(), err))
                    .subscribe((i) -> log.info("同步设备状态成功"));
        }
    }

    @EventListener
    public void handleDeviceConnectEvent(DeviceConnectedEvent event) {
        if (deviceIdSink != null) {
            deviceIdSink.next(event.getSession().getDeviceId());
        } else {
            syncState(Flux.just(event.getSession().getDeviceId()), false)
                    .doOnError(err -> log.error(err.getMessage(), err))
                    .subscribe((i) -> log.info("同步设备状态成功"));
        }
    }

    @EventListener
    public void handleDeviceDisConnectEvent(DeviceDisconnectedEvent event) {
        if (deviceIdSink != null) {
            deviceIdSink.next(event.getSession().getDeviceId());
        } else {
            syncState(Flux.just(event.getSession().getDeviceId()), false)
                    .doOnError(err -> log.error(err.getMessage(), err))
                    .subscribe((i) -> log.info("同步设备状态成功"));
        }
    }

    public Mono<DeviceInfo> getDeviceInfoById(String id) {
        return findById(Mono.just(id))
                .zipWhen(instance -> deviceProductService
                        .findById(Mono.just(instance.getProductId())), DeviceInfo::of)
                .switchIfEmpty(Mono.error(NotFoundException::new));
//        return findById(Mono.just(id))
//                .zipWhen(instance -> deviceProductService
//                                .findById(Mono.just(instance.getProductId()))
//                                .zipWith(propertiesService.createQuery()
//                                        .where(DevicePropertiesEntity::getDeviceId, id)
//                                        .fetch()
//                                        .collectList()),
//                        ((deviceInstanceEntity, tuple) -> DeviceInfo.of(deviceInstanceEntity, tuple.getT1(), tuple.getT2())))
//                .switchIfEmpty(Mono.error(NotFoundException::new));
    }

    /**
     * 同步设备状态
     *
     * @param deviceId 设备id集合
     * @param force    是否强制同步,将会检查设备的真实状态
     * @return 同步成功数量
     */
    public Flux<Integer> syncState(Flux<String> deviceId, boolean force) {

        return FluxUtils.bufferRate(deviceId.
                        flatMap(registry::getDevice)
                        .publishOn(Schedulers.parallel())
                        .flatMap(operation -> {
                            if (force) {
                                return operation.checkState().zipWith(Mono.just(operation.getDeviceId()));
                            }
                            return operation.getState().zipWith(Mono.just(operation.getDeviceId()));
                        })
                , 800, Duration.ofSeconds(5))
                .map(list -> list.stream()
                        .collect(Collectors.groupingBy(Tuple2::getT1, Collectors.mapping(Tuple2::getT2, Collectors.toSet()))))
                .map(Map::entrySet)
                .flatMap(Flux::fromIterable)
                .flatMap(e -> getRepository().createUpdate()
                        .set(DeviceInstanceEntity::getState, org.jetlinks.platform.manager.enums.DeviceState.of(e.getKey()))
                        .where()
                        .in(DeviceInstanceEntity::getId, e.getValue())
                        .execute());

    }

//    public void updateRegistry(DeviceInstanceEntity entity) {
//        Runnable runnable = () -> {
//            DeviceProductEntity productEntity = productService.selectByPk(entity.getProductId());
//
//            logger.info("update device instance[{}:{}] registry info", entity.getId(), entity.getName());
//            DeviceInfo productInfo = new DeviceInfo();
//            productInfo.setId(entity.getId());
//            if (null != productEntity) {
//                productInfo.setProductId(productEntity.getId());
//                productInfo.setProductName(productEntity.getName());
//                productInfo.setProtocol(productEntity.getMessageProtocol());
//            }
//
//            productInfo.setName(entity.getName());
//            productInfo.setCreatorId(entity.getCreatorId());
//            productInfo.setCreatorName(entity.getCreatorName());
//            productInfo.setProjectId(entity.getProductId());
//            productInfo.setProjectName(entity.getProductName());
//
//            DeviceOperation operation = registry.getDevice(entity.getId());
//            operation.update(productInfo);
//
//            Optional.ofNullable(entity.getDeriveMetadata())
//                    .ifPresent(operation::updateMetadata);
//
//            if (operation.getState() == DeviceState.unknown) {
//                operation.putState(DeviceState.noActive);
//            }
//            //自定义配置
//
//            ofNullable(entity.getSecurity())
//                    .ifPresent(operation::putAll);
//        };
//        if (TransactionSynchronizationManager.isSynchronizationActive()) {
//            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
//                @Override
//                public void afterCommit() {
//                    runnable.run();
//                }
//            });
//        } else {
//            runnable.run();
//        }
//    }
}
