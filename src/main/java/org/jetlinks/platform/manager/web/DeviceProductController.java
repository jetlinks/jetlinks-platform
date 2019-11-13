package org.jetlinks.platform.manager.web;

import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.web.ResponseMessage;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.core.metadata.unit.UnifyUnit;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.jetlinks.platform.manager.service.LocalDeviceProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @PostMapping("/deploy/{productId:.+}")
    public Mono<Integer> deviceDeploy(@PathVariable String productId) {
        return productService.deploy(productId);
    }

    @PostMapping("/cancelDeploy/{productId:.+}")
    public Mono<Integer> cancelDeploy(@PathVariable String productId) {
        return productService.cancelDeploy(productId);
    }

//    /**
//     * 已发布的设备型号查询
//     *
//     * @return
//     */
//    @GetMapping("/registered/query")
//    public Flux<DeviceProductEntity> queryRegisteredDeviceProduct() {
//        return productService.queryRegisteredDeviceProduct();
//    }
}
