package org.jetlinks.platform.manager.logger;


import lombok.extern.slf4j.Slf4j;
import org.jetlinks.core.utils.FluxUtils;
import org.jetlinks.platform.events.DeviceConnectedEvent;
import org.jetlinks.platform.events.DeviceDisconnectedEvent;
import org.jetlinks.platform.manager.elasticsearch.index.ElasticIndexProvider;
import org.jetlinks.platform.manager.elasticsearch.save.SaveService;
import org.jetlinks.platform.manager.enums.DeviceLogType;
import org.jetlinks.platform.manager.enums.EsDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Component
@Slf4j
public class DeviceLogHandler {

    @Autowired
    private SaveService saveService;

    private volatile FluxSink<DeviceOperationLog> deviceIdSink;

    @PostConstruct
    public void init() {
        collectDeviceLog(Flux.create(fluxSink -> deviceIdSink = fluxSink));
    }

    @EventListener
    public void handleDeviceDisConnectEvent(DeviceDisconnectedEvent event) {
        DeviceOperationLog deviceOperationType = DeviceOperationLog.builder()
                .deviceId(event.getDeviceId())
                .type(DeviceLogType.offline)
                .createTime(new Date())
                .content("设备下线")
                .build();
        if (deviceIdSink != null) {
            deviceIdSink.next(deviceOperationType);
        } else {
            collectDeviceLog(Flux.just(deviceOperationType));
        }
    }

    @EventListener
    public void handleDeviceConnectEvent(DeviceConnectedEvent event) {
        DeviceOperationLog deviceOperationType = DeviceOperationLog.builder()
                .deviceId(event.getDeviceId())
                .type(DeviceLogType.online)
                .createTime(new Date())
                .content("设备上线")
                .build();
        if (deviceIdSink != null) {
            deviceIdSink.next(deviceOperationType);
        } else {
            collectDeviceLog(Flux.just(deviceOperationType));
        }
    }

    @EventListener
    public void handleDeviceOperationEvent(DeviceOperationLog operationLog) {
        if (deviceIdSink != null) {
            deviceIdSink.next(operationLog);
        } else {
            collectDeviceLog(Flux.just(operationLog));
        }
    }

    private void collectDeviceLog(Flux<DeviceOperationLog> deviceOperations) {
        FluxUtils.bufferRate(deviceOperations, 800, Duration.ofSeconds(2))
                .flatMap(this::recordLog)
                .doOnError(ex -> log.error("保存设备操作日志失败", ex))
                .subscribe(s -> log.info("保存设备操作日志成功"));
    }


    private Mono<Boolean> recordLog(List<DeviceOperationLog> datas) {
        return saveService.asyncBulkSave(
                datas.stream().map(DeviceOperationLog::toSimpleMap).collect(Collectors.toList()),
                ElasticIndexProvider.createIndex(EsDataType.DEVICE_OPERATION.getIndex(),
                        EsDataType.DEVICE_OPERATION.getType())
        );
    }
}
