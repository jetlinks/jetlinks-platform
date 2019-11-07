package org.jetlinks.platform.manager.notify;

import lombok.extern.slf4j.Slf4j;
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
        log.info("按模版{}发送短信到{}. {} ", templateId, sendTo, context);
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> send(String text, Map<String, Object> context, List<String> sendTo) {
        log.info("发送短信:{} 到:{}", text, sendTo);
        return Mono.just(true);
    }
}
