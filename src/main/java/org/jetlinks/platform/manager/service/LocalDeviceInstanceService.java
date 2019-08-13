package org.jetlinks.platform.manager.service;

import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.GenericEntityService;
import org.jetlinks.core.device.DeviceInfo;
import org.jetlinks.core.device.DeviceOperation;
import org.jetlinks.core.device.DeviceState;
import org.jetlinks.core.device.registry.DeviceRegistry;
import org.jetlinks.platform.events.DeviceConnectedEvent;
import org.jetlinks.platform.manager.dao.DeviceInstanceDao;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
public class LocalDeviceInstanceService extends GenericEntityService<DeviceInstanceEntity, String> {

    @Autowired
    private DeviceInstanceDao deviceInstanceDao;

    @Autowired
    private DeviceRegistry registry;

    @Autowired
    private LocalDeviceProductService productService;

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public DeviceInstanceDao getDao() {
        return deviceInstanceDao;
    }

    @Override
    public String insert(DeviceInstanceEntity entity) {
        entity.setState(org.jetlinks.platform.manager.enums.DeviceState.notActive);

        return super.insert(entity);
    }

    public void deploy(String id) {
        updateRegistry(selectByPk(id));
    }

    private volatile FluxSink<String> deviceIdSink;

    @PostConstruct
    public void init() {
        Flux.<String>create(fluxSink -> deviceIdSink = fluxSink)
                .bufferTimeout(200, Duration.ofSeconds(2), Schedulers.elastic())
                .subscribe(list -> syncState(list, false));
    }

    @EventListener
    @Async
    public void handleDeviceConnectEvent(DeviceConnectedEvent event) {
        if (deviceIdSink != null) {
            deviceIdSink.next(event.getSession().getDeviceId());
        } else {
            syncState(Collections.singletonList(event.getSession().getDeviceId()), false);
        }
    }

    /**
     * 同步设备状态
     *
     * @param deviceId 设备id集合
     * @param force    是否强制同步,将会检查设备的真实状态
     * @return 同步成功数量
     */
    public int syncState(List<String> deviceId, boolean force) {
        int total = 0;
        try {

            Map<Byte, Set<String>> group = deviceId
                    .parallelStream()
                    .map(device -> {
                        DeviceOperation operation = registry.getDevice(device);
                        if (force) {
                            //检查真实状态
                            operation.checkState();
                        }
                        return operation;
                    })
                    .collect(Collectors.groupingByConcurrent(DeviceOperation::getState,
                            Collectors.mapping(DeviceOperation::getDeviceId, Collectors.toSet())));


            //批量更新分组结果
            for (Map.Entry<Byte, Set<String>> entry : group.entrySet()) {
                byte state = entry.getKey();
                Set<String> deviceIdList = entry.getValue();
                if (logger.isDebugEnabled()) {
                    logger.debug("同步设备状态:{} 数量: {}", state, deviceIdList.size());
                }
                if (CollectionUtils.isEmpty(deviceIdList)) {
                    continue;
                }
                total += createUpdate()
                        .set(DeviceInstanceEntity::getState, org.jetlinks.platform.manager.enums.DeviceState.of(state))
                        .where()
                        .in(DeviceInstanceEntity::getId, deviceIdList)
                        .exec();
            }
        } catch (Exception e) {
            logger.error("同步设备状态失败:\n{}", deviceId, e);
        }
        return total;
    }

    public void updateRegistry(DeviceInstanceEntity entity) {
        Runnable runnable = () -> {
            DeviceProductEntity productEntity = productService.selectByPk(entity.getProductId());

            logger.info("update device instance[{}:{}] registry info", entity.getId(), entity.getName());
            DeviceInfo productInfo = new DeviceInfo();
            productInfo.setId(entity.getId());
            if (null != productEntity) {
                productInfo.setProductId(productEntity.getId());
                productInfo.setProductName(productEntity.getName());
                productInfo.setProtocol(productEntity.getMessageProtocol());
            }

            productInfo.setName(entity.getName());
            productInfo.setCreatorId(entity.getCreatorId());
            productInfo.setCreatorName(entity.getCreatorName());
            productInfo.setProjectId(entity.getProductId());
            productInfo.setProjectName(entity.getProductName());

            DeviceOperation operation = registry.getDevice(entity.getId());
            operation.update(productInfo);

            Optional.ofNullable(entity.getDeriveMetadata())
                    .ifPresent(operation::updateMetadata);

            if (operation.getState() == DeviceState.unknown) {
                operation.putState(DeviceState.noActive);
            }
            //自定义配置

            ofNullable(entity.getSecurity())
                    .ifPresent(operation::putAll);
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }
}
