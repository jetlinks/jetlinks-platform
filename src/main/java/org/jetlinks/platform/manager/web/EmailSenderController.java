package org.jetlinks.platform.manager.web;

import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.platform.manager.entity.EmailSenderEntity;
import org.jetlinks.platform.manager.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
