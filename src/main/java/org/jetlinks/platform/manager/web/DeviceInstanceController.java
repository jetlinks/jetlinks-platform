package org.jetlinks.platform.manager.web;

import lombok.Getter;
import org.hswebframework.ezorm.rdb.exception.DuplicateKeyException;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.hswebframework.web.exception.BusinessException;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.jetlinks.platform.manager.entity.DevicePropertiesEntity;
import org.jetlinks.platform.manager.service.LocalDeviceInstanceService;
import org.jetlinks.platform.manager.service.LocalDevicePropertiesService;
import org.jetlinks.platform.manager.web.response.DeviceInfo;
import org.jetlinks.platform.manager.web.response.DeviceRunInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import reactor.cache.CacheMono;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/device-instance")
@Authorize
@Resource(id = "device-instance", name = "设备实例")
public class DeviceInstanceController implements
        ReactiveServiceCrudController<DeviceInstanceEntity, String> {

    @Autowired
    @Getter
    private LocalDeviceInstanceService service;

    @Autowired
    private LocalDevicePropertiesService propertiesService;

    @GetMapping("/info/{id:.+}")
    public Mono<DeviceInfo> getDeviceInfoById(@PathVariable String id) {
        return service.getDeviceInfoById(id);
    }

    @GetMapping("/run-info/{id:.+}")
    public Mono<DeviceRunInfo> getRunDeviceInfoById(@PathVariable String id) {
        return service.getDeviceRunInfo(id);
    }

    @GetMapping("/{deviceId}/properties")
    public Flux<DevicePropertiesEntity> getDeviceProperties(@PathVariable String deviceId) {
        return propertiesService.getProperties(deviceId);
    }

    @GetMapping("/{deviceId}/property/{property:.+}")
    public Mono<DevicePropertiesEntity> getDeviceProperty(@PathVariable String deviceId, @PathVariable String property) {
        return propertiesService.getProperty(deviceId, property);
    }

    @PostMapping("/deploy/{deviceId:.+}")
    public Mono<Integer> deviceDeploy(@PathVariable String deviceId) {
        return service.deploy(deviceId);
    }

    @PostMapping("/cancelDeploy/{deviceId:.+}")
    public Mono<Integer> cancelDeploy(@PathVariable String deviceId) {
        return service.cancelDeploy(deviceId);
    }

    /**
     * 重置设备安全参数
     *
     * @param deviceId
     * @return
     */
    @PostMapping("/reset/security/{deviceId:.+}")
    public Mono<Object> resetSecurityProperties(@PathVariable String deviceId) {
        return service.resetSecurityProperties(deviceId);
    }


    @PostMapping
    public Mono<DeviceInstanceEntity> add(@RequestBody Mono<DeviceInstanceEntity> payload) {
        return payload.flatMap(entity -> service
                .insert(Mono.just(entity))
                // TODO: 2019/11/4 错误类型判断
                .onErrorMap(DuplicateKeyException.class, err-> new BusinessException("设备id重复",err))
                .thenReturn(entity));

    }
}
