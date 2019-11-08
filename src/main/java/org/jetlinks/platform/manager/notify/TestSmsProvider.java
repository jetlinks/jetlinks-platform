package org.jetlinks.platform.manager.notify;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.utils.ExpressionUtils;
import org.jetlinks.rule.engine.executor.node.notify.SmsSender;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TestSmsProvider implements SmsProvider, SmsSender {
    @Override
    public String getProvider() {
        return "test";
    }

    @Override
    public Mono<SmsSender> createSender(Map<String, Object> configuration) {
        return Mono.just(this);
    }

    @Override
    public Mono<Boolean> sendTemplate(String templateId, Map<String, Object> context, List<String> sendTo) {

        return Mono.fromSupplier(() -> {
            log.info("按模版{}发送短信到{}. {} ", templateId, sendTo, context);
            return true;
        });
    }

    @Override
    @SneakyThrows
    public Mono<Boolean> send(String text, Map<String, Object> context, List<String> sendTo) {

        return Mono.fromSupplier(() -> {
            log.info("发送短信:{} 到:{}", ExpressionUtils.analytical(text, context, "spel"), sendTo);
            return true;
        });
    }
}
