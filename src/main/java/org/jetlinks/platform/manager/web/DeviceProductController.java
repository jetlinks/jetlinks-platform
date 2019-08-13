package org.jetlinks.platform.manager.web;

import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.SimpleGenericEntityController;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.jetlinks.platform.manager.service.LocalDeviceProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device-product")
public class DeviceProductController implements SimpleGenericEntityController<DeviceProductEntity,String, QueryParamEntity> {

    @Autowired
    private LocalDeviceProductService productService;

    @Override
    public LocalDeviceProductService getService() {
        return productService;
    }
}
