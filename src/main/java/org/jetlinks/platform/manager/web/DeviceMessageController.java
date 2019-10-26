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
import org.hswebframework.web.exception.NotFoundException;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.message.ReadPropertyMessageSender;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.property.ReadPropertyMessageReply;
import org.jetlinks.platform.events.DeviceMessageEvent;
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
    private JestClient jestClient;

    private Map<String, EmitterProcessor<Object>> eventProcessor = new ConcurrentHashMap<>();

    @EventListener
    public void handleDeviceEvent(DeviceMessageEvent<EventMessage> e) {

        Optional.ofNullable(eventProcessor.get(e.getMessage().getDeviceId()))
                .ifPresent(processor -> {
                    if (processor.isCancelled()) {
                        eventProcessor.remove(e.getMessage().getDeviceId());
                        return;
                    }
                    processor.onNext(e.getMessage());
                });

    }

    //获取实时事件
    @GetMapping(value = "/{deviceId}/event", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object> getEvent(@PathVariable String deviceId) {
        return eventProcessor
                .computeIfAbsent(deviceId, __ -> EmitterProcessor.create(100, true))
                .map(Function.identity());
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


    //获取设备属性
    @GetMapping("/{deviceId}/property/{property:.+}")
    @SneakyThrows
    public Flux<?> getProperties(@PathVariable String deviceId, @PathVariable String property) {

        return registry
                .getDevice(deviceId)
                .switchIfEmpty(Mono.error(NotFoundException::new))
                .map(DeviceOperator::messageSender)
                .map(sender -> sender.readProperty(property).messageId(IDGenerator.SNOW_FLAKE_STRING.generate()))
                .flatMapMany(ReadPropertyMessageSender::send)
                .map(ReadPropertyMessageReply::getProperties);

    }


    //获取设备所有属性
    @PostMapping("/{deviceId}/properties")
    @SneakyThrows
    public Flux<?> getProperties(@PathVariable String deviceId, @RequestBody Mono<List<String>> properties) {

        return properties
                .flatMapMany(list-> registry
                        .getDevice(deviceId)
                        .switchIfEmpty(Mono.error(NotFoundException::new))
                        .map(DeviceOperator::messageSender)
                        .map(sender -> sender.readProperty(list.toArray(new String[0]))
                                .messageId(IDGenerator.SNOW_FLAKE_STRING.generate()))
                        .flatMapMany(ReadPropertyMessageSender::send)
                        .map(ReadPropertyMessageReply::getProperties));

    }


}
