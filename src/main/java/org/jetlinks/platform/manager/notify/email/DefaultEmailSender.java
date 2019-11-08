package org.jetlinks.platform.manager.notify.email;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.utils.ExpressionUtils;
import org.jetlinks.platform.manager.entity.SenderTemplateEntity;
import org.jetlinks.platform.manager.service.SenderTemplateService;
import org.jetlinks.rule.engine.executor.node.notify.EmailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class DefaultEmailSender implements EmailSender {


    private JavaMailSender mailSender;

    private String sender;

    private SenderTemplateService templateService;

    @Override
    public Mono<Boolean> sendTemplate(String templateId, Map<String, Object> context, List<String> sendTo) {
        return templateService.findById(Mono.just(templateId))
                .map(this::convert)
                .map(o -> {
                    sendSimpleMail("", "", context, sendTo);
                    return true;
                });
    }

    @Override
    public Mono<Boolean> send(String subject, String text, Map<String, Object> context, List<String> sendTo) {
        return Mono.fromSupplier(() -> {
            sendSimpleMail(subject, text, context, sendTo);
            return true;
        }).publishOn(Schedulers.elastic());
    }

    private void sendSimpleMail(String subject, String text, Map<String, Object> context, List<String> sendTo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sendTo.toArray(new String[0]));
        message.setFrom(sender);
        message.setSubject(ExpressionUtils.analytical(subject, context, "spel"));
        message.setText(ExpressionUtils.analytical(text, context, "spel"));
        mailSender.send(message);
    }
    // TODO: 2019/11/8 实现其它类型邮件模板发送，ps:附件、图片、html

    protected Object convert(SenderTemplateEntity templateEntity) {
        return null;
    }

    protected Object convert(String subject, String text) {
        return null;
    }

}
