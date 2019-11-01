package org.jetlinks.platform.manager.web;

import org.hswebframework.ezorm.core.param.QueryParam;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.jetlinks.platform.manager.service.DeviceEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@RequestMapping("/device-event")
@RestController
public class DeviceEventController {

    @Autowired
    private DeviceEventService eventService;

    @GetMapping("/{propertyId}/productId/{productId}")
    public Mono<PagerResult<Map>> queryPager(QueryParam queryParam, @PathVariable String productId, @PathVariable String propertyId) {
        return eventService.queryPager(queryParam, productId, propertyId);
    }
}
