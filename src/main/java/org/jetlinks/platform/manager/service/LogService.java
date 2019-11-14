package org.jetlinks.platform.manager.service;

import lombok.extern.slf4j.Slf4j;
import org.hswebframework.ezorm.core.param.QueryParam;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.jetlinks.platform.manager.elasticsearch.index.DeviceEventIndexProvider;
import org.jetlinks.platform.manager.elasticsearch.index.ElasticIndexProvider;
import org.jetlinks.platform.manager.elasticsearch.query.ElasticSearchQueryService;
import org.jetlinks.platform.manager.elasticsearch.translate.SearchResponseTranslator;
import org.jetlinks.platform.manager.enums.EsDataType;
import org.jetlinks.platform.manager.logger.rule.info.ExecuteEventInfo;
import org.jetlinks.platform.manager.logger.rule.info.ExecuteLogInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Service
@Slf4j
public class LogService {

    @Autowired
    private ElasticSearchQueryService queryService;

    public Mono<PagerResult<Map>> queryPagerByDeviceEvent(QueryParam queryParam, String productId, String propertyId) {
        return queryService.query(queryParam, new DeviceEventIndexProvider(productId, propertyId))
                .map(response -> SearchResponseTranslator.translate(Map.class, response));
    }

    public Mono<PagerResult<ExecuteLogInfo>> queryPagerByLogInfo(QueryParam queryParam) {
        return queryService.query(queryParam,
                ElasticIndexProvider.createIndex(EsDataType.EXECUTE_LOG_INDEX.getIndex(),
                        EsDataType.EXECUTE_LOG_INDEX.getType())
        ).map(response -> SearchResponseTranslator.translate(ExecuteLogInfo.class, response));
    }

    public Mono<PagerResult<ExecuteEventInfo>> queryPagerByEventInfo(QueryParam queryParam) {
        return queryService.query(queryParam,
                ElasticIndexProvider.createIndex(EsDataType.EXECUTE_EVENT_LOG_INDEX.getIndex(),
                        EsDataType.EXECUTE_EVENT_LOG_INDEX.getType())
        ).map(response -> SearchResponseTranslator.translate(ExecuteEventInfo.class, response));
    }
}
