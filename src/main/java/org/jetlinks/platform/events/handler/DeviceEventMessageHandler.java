package org.jetlinks.platform.events.handler;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.id.IDGenerator;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.jetlinks.core.device.DeviceOperation;
import org.jetlinks.core.device.registry.DeviceRegistry;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.property.ReadPropertyMessageReply;
import org.jetlinks.core.message.property.WritePropertyMessageReply;
import org.jetlinks.core.metadata.DataType;
import org.jetlinks.core.metadata.DeviceMetadata;
import org.jetlinks.core.metadata.EventMetadata;
import org.jetlinks.core.metadata.PropertyMetadata;
import org.jetlinks.core.metadata.types.DateTimeType;
import org.jetlinks.core.metadata.types.NumberType;
import org.jetlinks.platform.events.DeviceMessageEvent;
import org.jetlinks.platform.manager.entity.DevicePropertiesEntity;
import org.jetlinks.platform.manager.service.LocalDevicePropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.*;

@Component
@Slf4j
public class DeviceEventMessageHandler {

    @Autowired
    private LocalDevicePropertiesService propertiesService;

    @Autowired
    private JestClient jestClient;

    @EventListener
    @Async
    public void handleEvent(DeviceMessageEvent<EventMessage> event) {
        EventMessage message = event.getMessage();
        DeviceOperation device = event.getSession().getOperation();

        //属性上报
        if (message.getHeader("report-property")
                .filter(Boolean.TRUE::equals)
                .map(Boolean.class::cast)
                .orElse(false)) {
            syncDeviceProperty(device, ((Map) message.getData()), new Date(message.getTimestamp()));
        } else {
            syncEvent(device, message);
        }
    }

    @EventListener
    @Async
    public void handleWriteProperty(DeviceMessageEvent<WritePropertyMessageReply> event) {
        WritePropertyMessageReply message = event.getMessage();
        if (message.isSuccess()) {
            syncDeviceProperty(event.getSession().getOperation(), message.getProperties(), new Date(message.getTimestamp()));
        }
    }

    @EventListener
    @Async
    public void handleReadProperty(DeviceMessageEvent<ReadPropertyMessageReply> event) {
        ReadPropertyMessageReply message = event.getMessage();
        if (message.isSuccess()) {
            syncDeviceProperty(event.getSession().getOperation(), message.getProperties(), new Date(message.getTimestamp()));
        }
    }

    private void syncEvent(DeviceOperation device, EventMessage message) {
        Object value = message.getData();

        List<PropertyMetadata> metadataList = device.getMetadata()
                .getEvent(message.getEvent())
                .map(EventMetadata::getParameters)
                .orElseGet(Collections::emptyList);

        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", device.getDeviceId());
        data.put("time", message.getTimestamp());
        if (value instanceof Map) {
            data.putAll(((Map) value));
        } else if (metadataList.size() == 1) {
            data.put(metadataList.get(0).getId(), value);
        } else {
            data.put("value", value);
        }
        Bulk.Builder builder = new Bulk.Builder()
                .defaultIndex("device_event_".concat(message.getEvent()))
                .defaultType("device");

        builder.addAction(new Index.Builder(data).build());


        jestClient.executeAsync(builder.build(), new JestResultHandler<JestResult>() {
            @Override
            public void completed(JestResult result) {
                if (!result.isSucceeded()) {
                    log.error("保存设备事件失败:{}", result.getJsonString());
                }
            }

            @Override
            public void failed(Exception ex) {
                log.error("保存设备事件失败", ex);
            }
        });

    }

    private void syncDeviceProperty(DeviceOperation device, Map<String, Object> properties, Date time) {
        DeviceMetadata metadata = device.getMetadata();
        Map<String, PropertyMetadata> propertyMetadata = metadata.getProperties().stream()
                .collect(Collectors.toMap(PropertyMetadata::getId, Function.identity()));

        List<DevicePropertiesEntity> entities = properties.entrySet()
                .stream()
                .map(entry -> {
                    DevicePropertiesEntity entity = new DevicePropertiesEntity();
                    entity.setDeviceId(device.getDeviceId());
                    entity.setUpdateTime(time);
                    entity.setProperty(entry.getKey());
                    entity.setPropertyName(entry.getKey());
                    ofNullable(propertyMetadata.get(entry.getKey()))
                            .ifPresent(prop -> {
                                DataType type = prop.getValueType();
                                entity.setPropertyName(prop.getName());
                                if (type instanceof NumberType) {
                                    NumberType numberType = (NumberType) type;
                                    entity.setNumberValue(numberType.convert(entry.getValue()));
                                } else if (type instanceof DateTimeType) {
                                    DateTimeType dateTimeType = (DateTimeType) type;
                                    entity.setNumberValue(dateTimeType.convert(entry.getValue()).getTime());
                                } else {
                                    entity.setStringValue(String.valueOf(entry.getValue()));
                                }

                                ofNullable(type.format(entry.getValue()))
                                        .map(String::valueOf)
                                        .ifPresent(entity::setFormatValue);
                            });
                    return entity;
                })
                .collect(Collectors.toList());
        syncDeviceProperty(entities);
    }

    private void syncDeviceProperty(List<DevicePropertiesEntity> list) {
        Bulk.Builder builder = new Bulk.Builder()
                .defaultIndex("device_properties")
                .defaultType("device");

        for (DevicePropertiesEntity entity : list) {

            builder.addAction(new Index
                    .Builder(entity.toMap())
                    .build());

            int num = propertiesService.createUpdate()
                    .set(entity::getNumberValue)
                    .set(entity::getStringValue)
                    .set(entity::getFormatValue)
                    .set(entity::getUpdateTime)
                    .where(entity::getDeviceId)
                    .and("property", entity.getProperty())
                    .exec();
            if (num == 0) {
                entity.setId(IDGenerator.MD5.generate());
                propertiesService.getDao().insert(entity);
            }
        }

        jestClient.executeAsync(builder.build(), new JestResultHandler<JestResult>() {
            @Override
            public void completed(JestResult result) {
                if (!result.isSucceeded()) {
                    log.error("保存设备属性记录失败:{}", result.getJsonString());
                }
            }

            @Override
            public void failed(Exception ex) {
                log.error("保存设备属性记录失败", ex);
            }
        });

    }
}
