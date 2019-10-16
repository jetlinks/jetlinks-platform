package org.jetlinks.platform.manager.web;

import lombok.Getter;
import org.hswebframework.ezorm.rdb.mapping.ReactiveRepository;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.crud.web.reactive.ReactiveCrudController;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device-instance")
@Authorize
public class DeviceInstanceController implements
        ReactiveCrudController<DeviceInstanceEntity, String> {

    @Autowired
    @Getter
    private ReactiveRepository<DeviceInstanceEntity, String> repository;


}
