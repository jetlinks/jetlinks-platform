package org.jetlinks.platform.manager.web;

import org.hswebframework.ezorm.core.param.QueryParam;
import org.hswebframework.web.api.crud.entity.PagerResult;
import org.jetlinks.platform.manager.logger.DeviceOperationLog;
import org.jetlinks.platform.manager.service.DeviceOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@RequestMapping("/device-operation")
@RestController
public class DeviceOperationController {


    @Autowired
    private DeviceOperationService operationService;

    @GetMapping("/_query")
    public Mono<PagerResult<DeviceOperationLog>> queryPager(QueryParam queryParam) {
        return operationService.queryPager(queryParam);
    }

//    @GetMapping("/{deviceId:.+}")
//    public Mono<PagerResult<DeviceOperationLog>> queryPagerByDeviceId(@PathVariable String deviceId) {
//        return operationService.queryPagerByDeviceId(deviceId);
//    }

}
