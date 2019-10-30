package org.jetlinks.platform.manager.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.hswebframework.ezorm.core.param.QueryParam;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.hswebframework.web.api.crud.entity.QueryParamEntity;
import org.hswebframework.web.exception.BusinessException;
import org.jetlinks.platform.manager.elasticsearch.ElasticRestClient;
import org.jetlinks.platform.manager.elasticsearch.QueryParamTranslator;
import org.jetlinks.platform.manager.elasticsearch.SearchResponseTranslator;
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
public class DeviceOperationService {

    @Autowired
    private ElasticRestClient restClient;

    public Mono<PagerResult<DeviceOperationLog>> queryPagerByDeviceId(String deviceId) {
        return query(QueryParamEntity.of("deviceId", deviceId));
    }

    public Mono<PagerResult<DeviceOperationLog>> queryPager(QueryParam queryParam) {
        return query(queryParam);
    }

    private Mono<PagerResult<DeviceOperationLog>> query(QueryParam queryParam) {
        SearchRequest request = QueryParamTranslator.translate(queryParam, EsDataType.DEVICE_OPERATION);
        try {
            SearchResponse response = restClient.getClient().search(request, RequestOptions.DEFAULT);
            return SearchResponseTranslator.translate(DeviceOperationLog.class, response);
        } catch (Exception e) {
            log.error("查询设备操作日志失败:{}", e);
            throw new BusinessException("查询设备操作日志失败:" + e.getMessage());
        }

    }
}
