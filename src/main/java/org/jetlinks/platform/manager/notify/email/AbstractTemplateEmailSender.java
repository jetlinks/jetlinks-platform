package org.jetlinks.platform.manager.notify.email;

import org.jetlinks.platform.manager.entity.SenderTemplateEntity;
import org.jetlinks.platform.manager.notify.SmsTemplateManager;
import org.jetlinks.rule.engine.executor.node.notify.EmailSender;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author bsetfeng
 * @since 1.0
 **/
public abstract class AbstractTemplateEmailSender implements EmailSender {

    protected abstract Mono<SenderTemplateEntity> getTemplateEntity(String id);

    @Override
    public Mono<Boolean> send(String subject, String text, Map<String, Object> context, List<String> sendTo) {
        return doSend(convert(subject, text, context), sendTo);
    }

    @Override
    public Mono<Boolean> sendTemplate(String templateId, Map<String, Object> context, List<String> sendTo) {
        return doSend(convert(getTemplateEntity(templateId), context), sendTo);
    }

    protected abstract Mono<Boolean> doSend(Mono<DefaultEmailTemplate> template, List<String> sendTo);

    protected abstract Mono<DefaultEmailTemplate> convert(Mono<SenderTemplateEntity> templateEntity, Map<String, Object> context);

    protected abstract Mono<DefaultEmailTemplate> convert(String subject, String text, Map<String, Object> context);
}
