package org.jetlinks.platform.manager.web;

import org.hswebframework.web.api.crud.entity.QueryParamEntity;
import org.hswebframework.web.authorization.annotation.QueryAction;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.service.ReactiveCrudService;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.jetlinks.platform.manager.service.LocalDeviceProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/device-product")
@Resource(id = "device-product", name = "设备型号")
public class DeviceProductController implements ReactiveServiceCrudController<DeviceProductEntity, String> {

    @Autowired
    private LocalDeviceProductService productService;

    @Override
    public LocalDeviceProductService getService() {
        return productService;
    }


    /**
     * 已发布的设备型号查询
     *
     * @return
     */
    @GetMapping("/registered/query")
    public Flux<DeviceProductEntity> queryRegisteredDeviceProduct() {
        return productService.queryRegisteredDeviceProduct();
    }
}
