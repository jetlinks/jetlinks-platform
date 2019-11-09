package org.jetlinks.platform.manager.notify.email;

import lombok.extern.slf4j.Slf4j;
import org.jetlinks.platform.manager.entity.EmailSenderEntity;
import org.jetlinks.platform.manager.service.EmailSenderService;
import org.jetlinks.platform.manager.service.SenderTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Slf4j
@Component
public class DefaultEmailSenderManager extends AbstractEmailSenderManager {

    @Autowired
    private EmailSenderService senderService;

    @Autowired
    private SenderTemplateService templateService;


    @Override
    protected Mono<EmailConfig> getConfig(String id) {
        return senderService
                .findById(Mono.just(id))
                .map(this::convert);
    }

    public EmailConfig convert(EmailSenderEntity senderEntity) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(senderEntity.getHost());
        mailSender.setPort(senderEntity.getPort());
        mailSender.setUsername(senderEntity.getUsername());
        mailSender.setPassword(senderEntity.getPassword());
        // TODO: 2019/11/8 其它配置类型未设置
        return EmailConfig.builder()
                .id(senderEntity.getId())
                .sender(senderEntity.getSender())
                .templateService(templateService)
                .mailSender(mailSender)
                .build();
    }
}
