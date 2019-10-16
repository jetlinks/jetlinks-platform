package org.jetlinks.platform.manager.web;

import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.jetlinks.platform.manager.service.LocalDeviceProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device-product")
public class DeviceProductController  {

    @Autowired
    private LocalDeviceProductService productService;


}
