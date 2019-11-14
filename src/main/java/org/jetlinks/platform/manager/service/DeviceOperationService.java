package org.jetlinks.platform.manager.service;

import lombok.extern.slf4j.Slf4j;
import org.hswebframework.ezorm.core.param.QueryParam;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.jetlinks.platform.manager.elasticsearch.index.ElasticIndexProvider;
import org.jetlinks.platform.manager.elasticsearch.query.ElasticSearchQueryService;
import org.jetlinks.platform.manager.elasticsearch.translate.SearchResponseTranslator;
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
    private ElasticSearchQueryService queryService;


    public Mono<PagerResult<DeviceOperationLog>> queryPager(QueryParam queryParam) {
        return queryService.query(
                queryParam,
                ElasticIndexProvider.createIndex(
                        EsDataType.DEVICE_OPERATION.getIndex(),
                        EsDataType.DEVICE_OPERATION.getType())
        ).map(response -> SearchResponseTranslator.translate(DeviceOperationLog.class, response));
    }
}
