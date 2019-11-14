package org.jetlinks.platform.manager.notify.email;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.exception.BusinessException;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.utils.ExpressionUtils;
import org.jetlinks.platform.manager.entity.SenderTemplateEntity;
import org.jetlinks.platform.manager.service.SenderTemplateService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.HashMap;
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


    @Getter
    @Setter
    private JavaMailSender javaMailSender;

    @Getter
    @Setter
    private String sender;

    @Getter
    @Setter
    private SenderTemplateService templateService;

    @Getter
    MimeMessageHelper helper;

    @Getter
    MimeMessage mimeMessage;

    private MimeMessageHelper createEmailHelper() throws Exception {
        this.mimeMessage = this.javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(this.mimeMessage, true);// 第二个
        return helper;
    }

    @Override
    protected Mono<SenderTemplateEntity> getTemplateEntity(String id) {
        return templateService.findById(Mono.just(id));
    }

    @Override
    protected Mono<Boolean> doSend(Mono<DefaultEmailTemplate> template, List<String> sendTo) {
        return template.map(t -> {
            try {
                if (this.helper == null) {
                    this.helper = createEmailHelper();
                }
                this.helper.setFrom(this.sender);
                this.helper.setTo(sendTo.toArray(new String[0]));
                this.helper.setSubject(t.getSubject());
                this.helper.setText(t.getText(),true);
                this.javaMailSender.send(this.mimeMessage);
                for (Map.Entry<String, String> entry : t.getAttachments().entrySet()) {
                    helper.addAttachment(MimeUtility.encodeText(entry.getKey()), new File(entry.getKey()));
                }
                for (Map.Entry<String, String> entry : t.getImages().entrySet()) {
                    helper.addInline(entry.getKey(), new FileSystemResource(new File(entry.getValue())));
                }
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
                String sendText = render(text, context);
                Map<String, Object> tempAttachments = template.getJSONObject("attachments");
                Map<String, String> attachments = new HashMap<>();
                if (tempAttachments != null) {
                    tempAttachments.forEach((key, value) ->
                            attachments.put(key, render(value.toString(), context)));
                }
                return DefaultEmailTemplate.builder()
                        .attachments(attachments)
                        .images(extractSendTextImage(sendText))
                        .text(sendText)
                        .subject(render(subject, context))
                        .build();
            } catch (Exception e) {
                throw new BusinessException("解析模板内容失败", e);
            }
        });
    }

    @Override
    protected Mono<DefaultEmailTemplate> convert(String subject, String text, Map<String, Object> context) {
        return Mono.fromSupplier(() -> {
            String sendText = render(text, context);
            return DefaultEmailTemplate.builder()
                    .text(sendText)
                    .attachments(new HashMap<>())
                    .attachments(extractSendTextImage(sendText))
                    .subject(render(subject, context))
                    .build();
        });
    }

    private Map<String, String> extractSendTextImage(String sendText) {
        Map<String, String> images = new HashMap<>();
        Document doc = Jsoup.parse(sendText);
        for (Element src : doc.getElementsByTag("img")) {
            String s = src.attr("src");
            if (s.startsWith("http")) {
                continue;
            }
            String tempKey = IDGenerator.MD5.generate();
            src.attr("src", "cid:".concat(tempKey));
            images.put(tempKey, s);
        }
        return images;
    }

    private String render(String str, Map<String, Object> context) {
        return ExpressionUtils.analytical(str, context, "spel");
    }

}
