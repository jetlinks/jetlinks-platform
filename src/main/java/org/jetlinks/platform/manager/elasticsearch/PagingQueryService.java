package org.jetlinks.platform.manager.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.hswebframework.ezorm.core.param.QueryParam;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.hswebframework.web.exception.BusinessException;
import org.jetlinks.platform.manager.entity.ElasticSearchIndexEntity;
import org.jetlinks.platform.manager.enums.EsDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Service
@Slf4j
public class PagingQueryService {

    @Autowired
    private ElasticRestClient restClient;


    public <T> Mono<PagerResult<T>> query(Class<T> clazz, QueryParam queryParam, ElasticSearchIndexEntity index) {
        return query(clazz, queryParam, index.getIndex(), index.getType());
    }

    public <T> Mono<PagerResult<T>> query(Class<T> clazz, QueryParam queryParam, EsDataType esDataType) {
        return query(clazz, queryParam, esDataType.getIndex(), esDataType.getType());
    }

    public <T> Mono<PagerResult<T>> query(Class<T> clazz, QueryParam queryParam, String index, String type) {
//        if (indexIsExists(index)) {
//            SearchRequest request = QueryParamTranslator.translate(queryParam, index, type);
//            return search(request, clazz);
//        } else {
//            log.warn("es查询索引 index:{} 不存在", index);
//            return Mono.just(PagerResult.empty());
//        }
        return search(QueryParamTranslator.translate(queryParam, index, type), clazz);
    }

    private <T> Mono<PagerResult<T>> search(SearchRequest request, Class<T> clazz) {
        try {
            SearchResponse response = restClient.getClient().search(request, RequestOptions.DEFAULT);
            return SearchResponseTranslator.translate(clazz, response);
        } catch (Exception e) {
            log.error("分页查询失败:{}", e.getMessage());
            return Mono.just(PagerResult.empty());
        }
    }

    private boolean indexIsExists(String index) {
        try {
            GetIndexRequest request = new GetIndexRequest(index);
            return restClient.getClient().indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("查询es index 是否存在失败:{}", e);
            throw new BusinessException("查询es index 是否存在失败:" + e.getMessage());
        }
    }
}
