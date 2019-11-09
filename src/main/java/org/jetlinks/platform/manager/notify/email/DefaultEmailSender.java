package org.jetlinks.platform.manager.notify.email;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.exception.BusinessException;
import org.hswebframework.web.utils.ExpressionUtils;
import org.jetlinks.platform.manager.entity.SenderTemplateEntity;
import org.jetlinks.platform.manager.service.SenderTemplateService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class DefaultEmailSender extends AbstractTemplateEmailSender {


    private JavaMailSender javaMailSender;

    private String sender;

    private SenderTemplateService templateService;

    @Override
    protected Mono<SenderTemplateEntity> getTemplateEntity(String id) {
        return templateService.findById(Mono.just(id));
    }

    @Override
    protected Mono<Boolean> doSend(Mono<DefaultEmailTemplate> template, List<String> sendTo) {
        return template.map(t -> {
            try {
//                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);// 第二个参数设置为true，表示允许添加附件
//                helper.setFrom(sender);
//                helper.setTo(sendTo.toArray(new String[0]));
//                helper.setSubject(t.getSubject());
//                helper.setText(t.getText());
//                javaMailSender.send(mimeMessage);
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(sendTo.toArray(new String[0]));
                message.setFrom(sender);
                message.setSubject(t.getSubject());
                message.setText(t.getText());
                javaMailSender.send(message);
                return true;
            } catch (Exception e) {
                throw new BusinessException("发送邮件失败", e);
            }
        });
    }

    @Override
    protected Mono<DefaultEmailTemplate> convert(Mono<SenderTemplateEntity> templateEntity, Map<String, Object> context) {
        return templateEntity.map(entity -> {
            try {
                JSONObject template = JSON.parseObject(entity.getTemplate());
                String subject = template.getString("subject");
                String text = template.getString("text");
                if (StringUtils.isEmpty(subject) || StringUtils.isEmpty(text)) {
                    throw new BusinessException("模板内容错误，text 或者 subject 不能为空。template:" + entity.getTemplate());
                }
                return DefaultEmailTemplate.builder()
                        .text(ExpressionUtils.analytical(text, context, "spel"))
                        .subject(ExpressionUtils.analytical(subject, context, "spel"))
                        .build();
            } catch (Exception e) {
                throw new BusinessException("解析模板内容失败", e);
            }
        });
    }

    @Override
    protected Mono<DefaultEmailTemplate> convert(String subject, String text, Map<String, Object> context) {
        return Mono.fromSupplier(() -> DefaultEmailTemplate.builder()
                .text(ExpressionUtils.analytical(subject, context, "spel"))
                .subject(ExpressionUtils.analytical(text, context, "spel"))
                .build());
    }

}
