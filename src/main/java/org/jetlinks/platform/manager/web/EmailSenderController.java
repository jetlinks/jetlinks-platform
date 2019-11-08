package org.jetlinks.platform.manager.web;

import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.platform.manager.entity.EmailSenderEntity;
import org.jetlinks.platform.manager.entity.MqttClientEntity;
import org.jetlinks.platform.manager.service.EmailSenderService;
import org.jetlinks.platform.manager.service.MqttClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/email-sender")
@Resource(id = "email-sender", name = "邮件发送")
public class EmailSenderController implements ReactiveServiceCrudController<EmailSenderEntity, String> {

    @Autowired
    private EmailSenderService emailSenderService;

    @Override
    public EmailSenderService getService() {
        return emailSenderService;
    }


//    @PostMapping
//    public Mono<EmailSenderEntity> add(@RequestBody Mono<EmailSenderEntity> payload) {
//        return payload.flatMap(entity -> {
//            if (StringUtils.isEmpty(entity.getClientId())){
//                entity.setClientId(IDGenerator.MD5.generate());
//            }
//            return mqttClientService
//                    .insert(Mono.just(entity))
//                    .thenReturn(entity);
//        });
//
//    }
}
