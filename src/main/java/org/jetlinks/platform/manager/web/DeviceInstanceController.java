package org.jetlinks.platform.manager.web;

import lombok.Getter;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.jetlinks.platform.manager.entity.DevicePropertiesEntity;
import org.jetlinks.platform.manager.service.LocalDeviceInstanceService;
import org.jetlinks.platform.manager.service.LocalDevicePropertiesService;
import org.jetlinks.platform.manager.web.response.DeviceInfo;
import org.jetlinks.platform.manager.web.response.DeviceRunInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.cache.CacheMono;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

}
