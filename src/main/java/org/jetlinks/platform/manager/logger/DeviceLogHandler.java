package org.jetlinks.platform.manager.logger;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import lombok.extern.slf4j.Slf4j;
import org.jetlinks.core.utils.FluxUtils;
import org.jetlinks.platform.events.DeviceConnectedEvent;
import org.jetlinks.platform.events.DeviceDisconnectedEvent;
import org.jetlinks.platform.manager.enums.DeviceLogType;
import org.jetlinks.platform.manager.enums.EsDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Component
@Slf4j
public class DeviceLogHandler {

    @Autowired
    private JestClient jestClient;

    private volatile FluxSink<DeviceOperationLog> deviceIdSink;

    @PostConstruct
    public void init() {
        collectDeviceLog(Flux.create(fluxSink -> deviceIdSink = fluxSink));
    }

    @EventListener
    public void handleDeviceDisConnectEvent(DeviceDisconnectedEvent event) {
        DeviceOperationLog deviceOperationType = DeviceOperationLog.builder()
                .deviceId(event.getSession().getDeviceId())
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
                .deviceId(event.getSession().getDeviceId())
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
                .subscribe(this::recordLog);
    }


    private void recordLog(List<DeviceOperationLog> datas) {
        try {
            Bulk.Builder builder = new Bulk.Builder()
                    .defaultIndex(EsDataType.DEVICE_OPERATION.getIndex())
                    .defaultType(EsDataType.DEVICE_OPERATION.getType());
            datas.forEach(data -> builder.addAction(new Index.Builder(data.toSimpleMap()).build()));
            jestClient.executeAsync(builder.build(), new JestResultHandler<JestResult>() {
                @Override
                public void completed(JestResult result) {
                    if (!result.isSucceeded()) {
                        log.error("保存设备操作日志失败:{}", result.getJsonString());
                    }else{
                        log.debug("保存设备操作日志成功:{}",datas.size());
                    }
                }

                @Override
                public void failed(Exception ex) {
                    log.error("保存设备操作日志失败", ex);
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
