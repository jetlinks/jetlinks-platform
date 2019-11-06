package org.jetlinks.platform.manager.service;

import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.jetlinks.platform.manager.entity.MqttClientEntity;
import org.springframework.stereotype.Service;

@Service
public class MqttClientService extends GenericReactiveCrudService<MqttClientEntity,String>  {

}
