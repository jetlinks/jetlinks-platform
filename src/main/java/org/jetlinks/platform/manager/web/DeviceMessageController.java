package org.jetlinks.platform.manager.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.sort.Sort;
import lombok.SneakyThrows;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.QueryBuilder;
import org.hswebframework.easyorm.elasticsearch.ElasticSearchQueryParamTranslator;
import org.hswebframework.web.api.crud.entity.QueryParamEntity;
import org.hswebframework.web.exception.BusinessException;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.core.device.registry.DeviceRegistry;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.property.ReadPropertyMessageReply;
import org.jetlinks.platform.events.DeviceMessageEvent;
import org.jetlinks.platform.manager.entity.DevicePropertiesEntity;
import org.jetlinks.platform.manager.service.LocalDeviceInstanceService;
import org.jetlinks.platform.manager.service.LocalDevicePropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/device")
public class DeviceMessageController {

    @Autowired
    private DeviceRegistry registry;

//    @Autowired
//    private LocalDeviceInstanceService localDeviceInstanceService;

    @Autowired
    private LocalDevicePropertiesService propertiesService;

    @Autowired
    private JestClient jestClient;

    private Map<String, EmitterProcessor<Object>> eventProcessor = new ConcurrentHashMap<>();


    @EventListener
    public void handleDeviceEvent(DeviceMessageEvent<EventMessage> e) {

        Optional.ofNullable(eventProcessor.get(e.getSession().getId()))
                .ifPresent(processor -> {
                    if (processor.isCancelled()) {
                        eventProcessor.remove(e.getSession().getId());
                        return;
                    }
                    processor.onNext(e.getMessage());
                });

    }

    //获取实时事件
    @GetMapping(value = "/{deviceId}/event",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object> getEvent(@PathVariable String deviceId) {

        return eventProcessor
                .computeIfAbsent(deviceId, __ -> EmitterProcessor.create(100, true))
                .map(Function.identity());
    }

    @GetMapping("/{deviceId}/properties")
    public Flux<DevicePropertiesEntity> getDeviceProperties(@PathVariable String deviceId) {

        return propertiesService.createQuery()
                .where(DevicePropertiesEntity::getDeviceId, deviceId)
                .fetch();
    }


    public JSONObject toQueryBuilder(QueryParamEntity entity) {
        JSONObject data = new JSONObject();
        QueryBuilder builder = ElasticSearchQueryParamTranslator.translate(entity);

        data.put("query", JSON.parse(Strings.toString(builder)));
        data.put("size", entity.getPageSize());
        data.put("from", entity.getPageIndex() * entity.getPageSize());
        return data;
    }

    @GetMapping("/{deviceId}/property/{property}/history")
    @SneakyThrows
    public Mono<Object> getDeviceProperties(@PathVariable String deviceId,
                                            @PathVariable String property,
                                            QueryParamEntity entity) {
        return Mono.create(sink -> {
            JSONObject queryJson = entity.toQuery()
                    .where("deviceId", deviceId)
                    .and("property", property)
                    .execute(this::toQueryBuilder);

            Search.Builder builder = new Search.Builder(queryJson.toJSONString());
            builder.addIndex("device_properties")
                    .addSort(new Sort("updateTime", Sort.Sorting.DESC))
                    .addType("device");

            jestClient.executeAsync(builder.build(), new JestResultHandler<SearchResult>() {
                @Override
                public void completed(SearchResult result) {
                    if (result.isSucceeded()) {
                        sink.success(convert(result));
                    } else {
                        sink.error(new BusinessException(result.getErrorMessage()));
                    }
                }

                @Override
                public void failed(Exception ex) {
                    sink.error(ex);
                }
            });

        });

    }

    protected List<Map> convert(SearchResult result) {

        return result.getHits(Map.class)
                .stream()
                .map(hit -> {
                    hit.source.remove("es_metadata_id");
                    return hit.source;
                })
                .collect(Collectors.toList());
    }

//    @GetMapping("/{deviceId}/event/{event}")
//    @SneakyThrows
//    public ResponseMessage<Object> getEvents(@PathVariable String deviceId,
//                                             @PathVariable String event,
//                                             QueryParamEntity entity) {
//        JSONObject queryJson = entity.toQuery()
//                .where("deviceId", deviceId)
//                .execute(this::toQueryBuilder);
//
//        Search.Builder builder = new Search.Builder(queryJson.toJSONString());
//        builder.addIndex("device_event_" + event)
//                .addType("device")
//                .addSort(new Sort("time", Sort.Sorting.DESC));
//
//        SearchResult result = jestClient.execute(builder.build());
//        if (!result.isSucceeded()) {
//            return ResponseMessage.error(result.getErrorMessage());
//        }
//
//        return ResponseMessage.ok(convert(result));
//    }

    //强制同步设备真实状态到数据库
//    @PutMapping("/state/sync")
//    public ResponseMessage<Void> syncState(@RequestBody List<String> deviceIdList) {
//
//        localDeviceInstanceService.syncState(deviceIdList, true);
//
//        return ResponseMessage.ok();
//    }

    //获取设备属性
    @GetMapping("/{deviceId}/property/{property:.+}")
    @SneakyThrows
    public Mono<?> getProperties(@PathVariable String deviceId, @PathVariable String property) {
        return Mono.fromCompletionStage(registry.getDevice(deviceId)
                .messageSender()
                .readProperty(property.split("[, ;]"))
                .messageId(IDGenerator.SNOW_FLAKE_STRING.generate())
                .send())
                .map(ReadPropertyMessageReply::getProperties);

    }

//    //断开连接
//    @GetMapping("/{deviceId}/disconnect")
//    @SneakyThrows
//    public ResponseMessage<Boolean> disconnect(@PathVariable String deviceId) {
//        return registry.getDevice(deviceId)
//                .disconnect()
//                .thenApply(ResponseMessage::ok)
//                .toCompletableFuture()
//                .get();
//    }

}
