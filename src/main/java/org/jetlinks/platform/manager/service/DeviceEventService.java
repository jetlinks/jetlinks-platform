package org.jetlinks.platform.manager.service;

import lombok.extern.slf4j.Slf4j;
import org.hswebframework.ezorm.core.param.QueryParam;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.jetlinks.platform.manager.elasticsearch.PagingQueryService;
import org.jetlinks.platform.manager.utils.GenerateDeviceEventIndex;
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
public class DeviceEventService {

    @Autowired
    private PagingQueryService queryService;


    public Mono<PagerResult<Map>> queryPager(QueryParam queryParam, String productId, String propertyId) {
        return queryService.query(Map.class, queryParam, GenerateDeviceEventIndex.generateIndex(productId, propertyId));
    }
}
