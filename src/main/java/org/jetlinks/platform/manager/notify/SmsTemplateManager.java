package org.jetlinks.platform.manager.notify;

import reactor.core.publisher.Mono;

public interface SmsTemplateManager {

    Mono<String> getTemplate(String id);
}
