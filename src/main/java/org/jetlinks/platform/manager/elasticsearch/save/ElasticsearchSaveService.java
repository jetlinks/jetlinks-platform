package org.jetlinks.platform.manager.elasticsearch.save;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.jetlinks.platform.manager.elasticsearch.index.ElasticIndexProvider;
import org.jetlinks.platform.manager.elasticsearch.ElasticRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
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
    public <T> Mono<Boolean> asyncBulkSave(List<T> data, ElasticIndexProvider provider) {
        return asyncSave(data, provider);
    }

    @Override
    public <T> Mono<Boolean> asyncSave(T data, ElasticIndexProvider provider) {
        return asyncSave(Collections.singletonList(data), provider);
    }



    private <T> Mono<Boolean> asyncSave(List<T> data, ElasticIndexProvider provider) {
        return Mono.create(sink -> {
            BulkRequest bulkRequest = new BulkRequest(provider.getIndex(), provider.getType());
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
}
