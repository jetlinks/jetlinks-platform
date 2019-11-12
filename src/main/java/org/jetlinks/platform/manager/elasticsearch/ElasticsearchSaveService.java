package org.jetlinks.platform.manager.elasticsearch;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.hswebframework.web.exception.BusinessException;
import org.jetlinks.platform.manager.entity.ElasticSearchIndexEntity;
import org.jetlinks.platform.manager.enums.EsDataType;
import org.jetlinks.platform.manager.logger.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Slf4j
@Component
public class ElasticsearchSaveService implements SaveService {

    @Autowired
    private ElasticRestClient restClient;


    @Override
    public <T> Mono<Boolean> asyncBulkSave(List<T> data, Object saveTarget) {
        return asyncSave(data, judgeTargetType(saveTarget));
    }

    @Override
    public <T> Mono<Boolean> asyncSave(T data, Object saveTarget) {
        return asyncSave(Collections.singletonList(data), judgeTargetType(saveTarget));
    }

    private ElasticSearchIndexEntity judgeTargetType(Object saveTarget) {
        if (saveTarget instanceof ElasticSearchIndexEntity) {
            return (ElasticSearchIndexEntity) saveTarget;
        } else if (saveTarget instanceof EsDataType) {
            EsDataType type = (EsDataType) saveTarget;
            return new ElasticSearchIndexEntity(type.getIndex(), type.getType());
        } else {
            throw new BusinessException("es不支持的批量存储目标类型:" + saveTarget.getClass());
        }
    }


    private <T> Mono<Boolean> asyncSave(List<T> data, ElasticSearchIndexEntity entity) {
        return Mono.create(sink -> {
            BulkRequest bulkRequest = new BulkRequest(entity.getIndex(), entity.getType());
            IndexRequest request = new IndexRequest();
            data.forEach(d -> {
                if (d instanceof String){
                    request.source(d, XContentType.JSON);
                }else {
                    request.source(JSON.toJSONString(d), XContentType.JSON);
                }

            });
            bulkRequest.add(request);
            restClient.getClient().bulkAsync(bulkRequest, RequestOptions.DEFAULT, new ActionListener<BulkResponse>() {
                @Override
                public void onResponse(BulkResponse responses) {
                    int result = responses.status().getStatus();
                    sink.success(result == 200 || result == 201);
                }

                @Override
                public void onFailure(Exception e) {
                    sink.error(e);
                }
            });
        });

    }
//    private <T> void asyncSave(List<T> data, ElasticSearchIndexEntity entity) {
//        Bulk.Builder builder = new Bulk.Builder()
//                .defaultIndex(entity.getIndex())
//                .defaultType(entity.getType());
//        data.forEach(d -> builder.addAction(new Index.Builder(d).build()));
//        jestClient.executeAsync(builder.build(), new JestResultHandler<JestResult>() {
//            @Override
//            public void completed(JestResult result) {
//                if (!result.isSucceeded()) {
//                    log.error("批量存储数据到es失败:{}", result.getJsonString());
//                } else {
//                    log.info("批量存储数据到es成功:size{}", data.size());
//                }
//            }
//
//            @Override
//            public void failed(Exception ex) {
//                log.error("批量存储数据到es失败", ex);
//            }
//        });
//    }
}
