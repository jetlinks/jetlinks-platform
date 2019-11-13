package org.jetlinks.platform.manager.elasticsearch.query;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.hswebframework.ezorm.core.param.QueryParam;
import org.jetlinks.platform.manager.elasticsearch.ElasticRestClient;
import org.jetlinks.platform.manager.elasticsearch.index.ElasticIndexProvider;
import org.jetlinks.platform.manager.elasticsearch.translate.QueryParamTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Service
@Slf4j
public class ElasticSearchQueryService {


    @Autowired
    private ElasticRestClient restClient;


    public Mono<SearchResponse> query(QueryParam queryParam, ElasticIndexProvider provider) {
        return search(searchRequestStructure(queryParam, provider));
    }

    public Mono<Long> count(QueryParam queryParam, ElasticIndexProvider provider) {
        return Mono.<CountResponse>create(sink -> {
            restClient.getClient()
                    .countAsync(countRequestStructure(queryParam, provider),
                            RequestOptions.DEFAULT, translatorActionListener(sink));
        }).map(CountResponse::getCount);
    }


    private Mono<SearchResponse> search(SearchRequest request) {
        return Mono.create(sink -> restClient.getClient().searchAsync(request, RequestOptions.DEFAULT, translatorActionListener(sink)));
    }

    private <T> ActionListener<T> translatorActionListener(MonoSink<T> sink) {
        return new ActionListener<T>() {
            @Override
            public void onResponse(T response) {
                sink.success(response);

            }

            @Override
            public void onFailure(Exception e) {
                if (e instanceof ElasticsearchException) {
                    if (((ElasticsearchException) e).status().getStatus() == 404) {
                        sink.success();
                        return;
                    }
                }
                sink.error(e);
            }
        };
    }

    public Mono<Boolean> indexIsExists(String index) {
        return Mono.create(sink -> {
            try {
                GetIndexRequest request = new GetIndexRequest(index);
                sink.success(restClient.getClient().indices().exists(request, RequestOptions.DEFAULT));
            } catch (Exception e) {
                log.error("查询es index 是否存在失败:{}", e);
                sink.error(e);
            }
        });
    }

    public SearchRequest searchRequestStructure(QueryParam queryParam, ElasticIndexProvider provider) {
        SearchRequest request = new SearchRequest(provider.getIndex())
                .types(provider.getType())
                .source(QueryParamTranslator.transSourceBuilder(queryParam));
        log.debug("es查询参数:{}", request.source().toString());
        return request;
    }

    public CountRequest countRequestStructure(QueryParam queryParam, ElasticIndexProvider provider) {
        CountRequest request = new CountRequest(provider.getIndex())
                .source(QueryParamTranslator.transSourceBuilder(queryParam));
        log.debug("es查询参数:{}", request.source().toString());
        return request;
    }
}
