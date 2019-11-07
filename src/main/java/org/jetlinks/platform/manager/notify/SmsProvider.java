package org.jetlinks.platform.manager.notify;

import org.jetlinks.rule.engine.executor.node.notify.SmsSender;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface SmsProvider {

    String getProvider();

    Mono<SmsSender> createSender(Map<String, Object> configuration);
}
