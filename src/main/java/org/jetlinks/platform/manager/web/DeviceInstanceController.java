package org.jetlinks.platform.manager.web;

import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.SimpleGenericEntityController;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.jetlinks.platform.manager.service.LocalDeviceInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device-instance")
public class DeviceInstanceController implements SimpleGenericEntityController<DeviceInstanceEntity,String, QueryParamEntity> {

    @Autowired
    private LocalDeviceInstanceService instanceService;

    @Override
    public LocalDeviceInstanceService getService() {
        return instanceService;
    }
}
