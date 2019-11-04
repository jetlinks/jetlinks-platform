package org.jetlinks.platform.manager.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.hswebframework.ezorm.core.param.QueryParam;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.hswebframework.web.api.crud.entity.QueryParamEntity;
import org.hswebframework.web.exception.BusinessException;
import org.jetlinks.platform.manager.entity.ElasticSearchIndexEntity;
import org.jetlinks.platform.manager.enums.EsDataType;
import org.jetlinks.platform.manager.logger.DeviceOperationLog;
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
        SearchRequest request = QueryParamTranslator.translate(queryParam, index.getIndex(), index.getType());
        return search(request, clazz);
    }

    public <T> Mono<PagerResult<T>> query(Class<T> clazz, QueryParam queryParam, EsDataType esDataType) {
        SearchRequest request = QueryParamTranslator.translate(queryParam, esDataType);
        return search(request, clazz);
    }

    private <T> Mono<PagerResult<T>> search(SearchRequest request, Class<T> clazz) {
        try {
            SearchResponse response = restClient.getClient().search(request, RequestOptions.DEFAULT);
            return SearchResponseTranslator.translate(clazz, response);
        } catch (Exception e) {
            log.error("分页查询失败:{}", e);
            throw new BusinessException("分页查询失败:" + e.getMessage());
        }
    }
}
