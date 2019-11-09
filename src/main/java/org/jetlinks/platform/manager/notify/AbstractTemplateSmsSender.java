package org.jetlinks.platform.manager.notify;

import lombok.SneakyThrows;
import org.hswebframework.web.utils.ExpressionUtils;
import org.jetlinks.rule.engine.executor.node.notify.SmsSender;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public abstract class AbstractTemplateSmsSender implements SmsSender {

    protected abstract SmsTemplateManager getTemplateManager();

    @SneakyThrows
    protected String render(String template, Map<String, Object> context) {
        return ExpressionUtils.analytical(template, context, "spel");
    }

    @Override
    public Mono<Boolean> sendTemplate(String templateId, Map<String, Object> context, List<String> sendTo) {
        return getTemplateManager()
                .getTemplate(templateId)
                .map(template -> render(template, context))
                .flatMap(text -> this.doSend(text, context, sendTo));
    }

    @Override
    public Mono<Boolean> send(String text, Map<String, Object> context, List<String> sendTo) {
        return doSend(render(text, context), context, sendTo);
    }


    protected abstract Mono<Boolean> doSend(String text, Map<String, Object> context, List<String> sendTo);


}
