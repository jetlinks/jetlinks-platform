package org.jetlinks.platform.manager.web;

import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.service.ReactiveCrudService;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.jetlinks.platform.manager.service.LocalDeviceProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
