package org.jetlinks.platform.manager.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.sort.Sort;
import lombok.SneakyThrows;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.QueryBuilder;
import org.hswebframework.easyorm.elasticsearch.ElasticSearchQueryParamTranslator;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.core.device.registry.DeviceRegistry;
import org.jetlinks.platform.manager.entity.DevicePropertiesEntity;
import org.jetlinks.platform.manager.service.LocalDeviceInstanceService;
import org.jetlinks.platform.manager.service.LocalDevicePropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/device")
public class DeviceMessageController {

    @Autowired
    private DeviceRegistry registry;

    @Autowired
    private LocalDeviceInstanceService localDeviceInstanceService;

    @Autowired
    private LocalDevicePropertiesService propertiesService;

    @Autowired
    private JestClient jestClient;


    @GetMapping("/{deviceId}/properties")
    public ResponseMessage<List<DevicePropertiesEntity>> getDeviceProperties(@PathVariable String deviceId) {

        return ResponseMessage.ok(propertiesService.createQuery()
                .where(DevicePropertiesEntity::getDeviceId, deviceId)
                .listNoPaging());
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
    public ResponseMessage<Object> getDeviceProperties(@PathVariable String deviceId,
                                                       @PathVariable String property,
                                                       QueryParamEntity entity) {

        JSONObject queryJson = entity.toQuery()
                .where("deviceId", deviceId)
                .and("property", property)
                .execute(this::toQueryBuilder);

        Search.Builder builder = new Search.Builder(queryJson.toJSONString());
        builder.addIndex("device_properties")
                .addSort(new Sort("updateTime", Sort.Sorting.DESC))
                .addType("device");

        SearchResult result = jestClient.execute(builder.build());
        if (!result.isSucceeded()) {
            return ResponseMessage.error(result.getErrorMessage());
        }

        return ResponseMessage.ok(convert(result));
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

    @GetMapping("/{deviceId}/event/{event}")
    @SneakyThrows
    public ResponseMessage<Object> getEvents(@PathVariable String deviceId,
                                             @PathVariable String event,
                                             QueryParamEntity entity) {
        JSONObject queryJson = entity.toQuery()
                .where("deviceId", deviceId)
                .execute(this::toQueryBuilder);

        Search.Builder builder = new Search.Builder(queryJson.toJSONString());
        builder.addIndex("device_event_" + event)
                .addType("device")
                .addSort(new Sort("time", Sort.Sorting.DESC));

        SearchResult result = jestClient.execute(builder.build());
        if (!result.isSucceeded()) {
            return ResponseMessage.error(result.getErrorMessage());
        }

        return ResponseMessage.ok(convert(result));
    }

    //强制同步设备真实状态到数据库
    @PutMapping("/state/sync")
    public ResponseMessage<Void> syncState(@RequestBody List<String> deviceIdList) {

        localDeviceInstanceService.syncState(deviceIdList, true);

        return ResponseMessage.ok();
    }

    //获取设备属性
    @GetMapping("/{deviceId}/property/{property:.+}")
    @SneakyThrows
    public ResponseMessage<?> getProperties(@PathVariable String deviceId, @PathVariable String property) {
        return registry.getDevice(deviceId)
                .messageSender()
                .readProperty(property.split("[, ;]"))
                .messageId(IDGenerator.SNOW_FLAKE_STRING.generate())
                .trySend(10, TimeUnit.SECONDS)
                .map(reply -> {
                    if (reply.isSuccess()) {
                        return ResponseMessage.ok(reply.getProperties());
                    } else {
                        return ResponseMessage.error(500, reply.getMessage()).code(reply.getCode());
                    }
                })
                .recover(error -> ResponseMessage.error(error.getMessage()))
                .get();
    }

    //断开连接
    @GetMapping("/{deviceId}/disconnect")
    @SneakyThrows
    public ResponseMessage<Boolean> disconnect(@PathVariable String deviceId) {
        return registry.getDevice(deviceId)
                .disconnect()
                .thenApply(ResponseMessage::ok)
                .toCompletableFuture()
                .get();
    }

}
