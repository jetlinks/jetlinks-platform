package org.jetlinks.platform.manager.web;

import org.hswebframework.ezorm.core.param.QueryParam;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.Resource;
import org.jetlinks.platform.manager.logger.rule.info.ExecuteEventInfo;
import org.jetlinks.platform.manager.logger.rule.info.ExecuteLogInfo;
import org.jetlinks.platform.manager.service.LogService;
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
@RequestMapping("/log")
@RestController
@Authorize
@Resource(id = "log", name = "日志管理")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/device-event/{propertyId}/productId/{productId}")
    public Mono<PagerResult<Map>> queryPagerByDeviceEvent(QueryParam queryParam, @PathVariable String productId, @PathVariable String propertyId) {
        return logService.queryPagerByDeviceEvent(queryParam, productId, propertyId);
    }

    @GetMapping("/rule/exec-event")
    public Mono<PagerResult<ExecuteEventInfo>> queryPagerByEventInfo(QueryParam queryParam) {
        return logService.queryPagerByEventInfo(queryParam);
    }

    @GetMapping("/rule/exec")
    public Mono<PagerResult<ExecuteLogInfo>> queryPagerByLogInfo(QueryParam queryParam) {
        return logService.queryPagerByLogInfo(queryParam);
    }
}
