package org.jetlinks.platform.events.handler;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.property.ReadPropertyMessageReply;
import org.jetlinks.core.message.property.WritePropertyMessageReply;
import org.jetlinks.core.metadata.DataType;
import org.jetlinks.core.metadata.EventMetadata;
import org.jetlinks.core.metadata.PropertyMetadata;
import org.jetlinks.core.metadata.types.DateTimeType;
import org.jetlinks.core.metadata.types.NumberType;
import org.jetlinks.core.utils.FluxUtils;
import org.jetlinks.platform.events.DeviceMessageEvent;
import org.jetlinks.platform.events.GaugePropertyEvent;
import org.jetlinks.platform.logger.DeviceOperationLog;
import org.jetlinks.platform.manager.entity.DevicePropertiesEntity;
import org.jetlinks.platform.manager.enums.DeviceLogType;
import org.jetlinks.platform.manager.service.LocalDevicePropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Component
@Slf4j
public class DeviceEventMessageHandler {

    @Autowired
    private LocalDevicePropertiesService propertiesService;

    @Autowired
    private JestClient jestClient;

    @Autowired
    private DeviceRegistry registry;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @EventListener
    public void handleEvent(DeviceMessageEvent<EventMessage> event) {
        EventMessage message = event.getMessage();
        //属性上报
        boolean isReportProperty = message.getHeader("report-property")
                .filter(Boolean.TRUE::equals)
                .map(Boolean.class::cast)
                .orElse(false);
        if (isReportProperty) {
            syncDeviceProperty(message.getDeviceId(), ((Map) message.getData()), new Date(message.getTimestamp()));

        } else {
            syncEvent(message.getDeviceId(), message);
        }
        DeviceOperationLog deviceOperationType = DeviceOperationLog.builder()
                .deviceId(message.getDeviceId())
                .createTime(new Date(message.getTimestamp()))
                .content(message.getData())
                .type(isReportProperty ? DeviceLogType.reportProperty : DeviceLogType.event)
                .build();
        eventPublisher.publishEvent(deviceOperationType);
    }

    @EventListener
    public void handleWriteProperty(DeviceMessageEvent<WritePropertyMessageReply> event) {
        WritePropertyMessageReply message = event.getMessage();
        if (message.isSuccess()) {
            syncDeviceProperty(message.getDeviceId(), message.getProperties(), new Date(message.getTimestamp()));
        }
        DeviceOperationLog deviceOperationType = DeviceOperationLog.builder()
                .deviceId(message.getDeviceId())
                .createTime(new Date(message.getTimestamp()))
                .content(message.getProperties())
                .type(DeviceLogType.writeProperty)
                .build();
        eventPublisher.publishEvent(deviceOperationType);
    }

    @EventListener
    public void handleReadProperty(DeviceMessageEvent<ReadPropertyMessageReply> event) {
        ReadPropertyMessageReply message = event.getMessage();
        if (message.isSuccess()) {
            syncDeviceProperty(message.getDeviceId(), message.getProperties(), new Date(message.getTimestamp()));
        }
        DeviceOperationLog deviceOperationType = DeviceOperationLog.builder()
                .deviceId(message.getDeviceId())
                .createTime(new Date(message.getTimestamp()))
                .content(message.getProperties())
                .type(DeviceLogType.readProperty)
                .build();
        eventPublisher.publishEvent(deviceOperationType);
    }

    private void syncEvent(String device, EventMessage message) {

//        registry.getDevice(device)
//                .flatMap(DeviceOperator::getMetadata)
//                .subscribe(metadata -> {
//                    Object value = message.getData();
//
//                    List<PropertyMetadata> metadataList = metadata
//                            .getEvent(message.getEvent())
//                            .map(EventMetadata::getParameters)
//                            .orElseGet(Collections::emptyList);
//
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("deviceId", device);
//                    data.put("time", message.getTimestamp());
//                    if (value instanceof Map) {
//                        data.putAll(((Map) value));
//                    } else if (metadataList.size() == 1) {
//                        data.put(metadataList.get(0).getId(), value);
//                    } else {
//                        data.put("value", value);
//                    }
//                    Bulk.Builder builder = new Bulk.Builder()
//                            .defaultIndex("device_event_".concat(message.getEvent()))
//                            .defaultType("device");
//
//                    builder.addAction(new Index.Builder(data).build());
//
//
//                    jestClient.executeAsync(builder.build(), new JestResultHandler<JestResult>() {
//                        @Override
//                        public void completed(JestResult result) {
//                            if (!result.isSucceeded()) {
//                                log.error("保存设备事件失败:{}", result.getJsonString());
//                            }
//                        }
//
//                        @Override
//                        public void failed(Exception ex) {
//                            log.error("保存设备事件失败", ex);
//                        }
//                    });
//                });
//

    }

    private void syncDeviceProperty(String device, Map<String, Object> properties, Date time) {
        registry.getDevice(device)
                .flatMap(DeviceOperator::getMetadata)
                .subscribe(metadata -> {

                    Map<String, PropertyMetadata> propertyMetadata = metadata.getProperties().stream()
                            .collect(Collectors.toMap(PropertyMetadata::getId, Function.identity()));

                    List<DevicePropertiesEntity> entities = properties.entrySet()
                            .stream()
                            .map(entry -> {
                                DevicePropertiesEntity entity = new DevicePropertiesEntity();
                                entity.setDeviceId(device);
                                entity.setUpdateTime(time);
                                entity.setProperty(entry.getKey());
                                entity.setPropertyName(entry.getKey());
                                ofNullable(propertyMetadata.get(entry.getKey()))
                                        .ifPresent(prop -> {
                                            DataType type = prop.getValueType();
                                            entity.setPropertyName(prop.getName());
                                            if (type instanceof NumberType) {
                                                NumberType numberType = (NumberType) type;
                                                entity.setNumberValue(new BigDecimal(numberType.convert(entry.getValue()).toString()));
                                                eventPublisher.publishEvent(GaugePropertyEvent.builder()
                                                        .deviceId(device)
                                                        .propertyName(entry.getKey())
                                                        .propertyValue(entry.getValue())
                                                        .build()
                                                );
                                            } else if (type instanceof DateTimeType) {
                                                DateTimeType dateTimeType = (DateTimeType) type;
                                                entity.setNumberValue(new BigDecimal(dateTimeType.convert(entry.getValue()).toString()));
                                            } else {
                                                entity.setStringValue(String.valueOf(entry.getValue()));
                                            }
                                            entity.setValue(entry.getValue().toString());
                                            ofNullable(type.format(entry.getValue()))
                                                    .map(String::valueOf)
                                                    .ifPresent(entity::setFormatValue);
                                        });
                                return entity;
                            })
                            .collect(Collectors.toList());
                    syncDeviceProperty(entities);
                });

    }

    private EmitterProcessor<DevicePropertiesEntity> processor = EmitterProcessor.create();


    @PostConstruct
    @SuppressWarnings("all")
    public void init() {

        processor.subscribe(entity -> {
            propertiesService
                    .createUpdate()
                    .set(entity)
                    .where(entity::getDeviceId)
                    .and(entity::getProperty)
                    .execute()
                    .filter(i -> i != 0)
                    .switchIfEmpty(propertiesService.insert(Mono.just(entity)))
                    .subscribe(i -> {
                        log.debug("同步设备属性成功:{}", entity);
                    });
        });
    }

    private void syncDeviceProperty(List<DevicePropertiesEntity> list) {
//        Bulk.Builder builder = new Bulk.Builder()
//                .defaultIndex("device_properties")
//                .defaultType("device");
//
//        for (DevicePropertiesEntity entity : list) {
//
//            builder.addAction(new Index
//                    .Builder(entity.toMap())
//                    .build());
//            processor.onNext(entity);
//        }
//
//        jestClient.executeAsync(builder.build(), new JestResultHandler<JestResult>() {
//            @Override
//            public void completed(JestResult result) {
//                if (!result.isSucceeded()) {
//                    log.error("保存设备属性记录失败:{}", result.getJsonString());
//                }
//            }
//
//            @Override
//            public void failed(Exception ex) {
//                log.error("保存设备属性记录失败", ex);
//            }
//        });

    }
}
