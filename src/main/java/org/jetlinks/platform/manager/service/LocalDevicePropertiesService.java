package org.jetlinks.platform.manager.service;

import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.GenericEntityService;
import org.jetlinks.platform.manager.dao.DevicePropertiesDao;
import org.jetlinks.platform.manager.entity.DevicePropertiesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocalDevicePropertiesService extends GenericEntityService<DevicePropertiesEntity,String> {

    @Autowired
    private DevicePropertiesDao propertiesDao;

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public DevicePropertiesDao getDao() {
        return propertiesDao;
    }
}
