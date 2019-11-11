package org.jetlinks.platform.manager.notify.email;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetlinks.platform.manager.service.SenderTemplateService;
import org.jetlinks.rule.engine.executor.node.notify.EmailSender;
import org.jetlinks.rule.engine.executor.node.notify.EmailSenderManager;
import org.springframework.mail.javamail.JavaMailSender;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Slf4j
public abstract class AbstractEmailSenderManager implements EmailSenderManager {

    private final Map<String, DefaultEmailSender> emailSenderCache = new ConcurrentHashMap<>();

    protected abstract Mono<EmailConfig> getConfig(String id);

    @Override
    public Mono<EmailSender> getSender(String id) {
        return Mono.justOrEmpty(emailSenderCache.get(id))
                .switchIfEmpty(Mono.defer(() -> getConfig(id)
                        .flatMap(this::createDefaultEmailSender)))
                .cast(EmailSender.class);
    }

    public void refreshCache(String id) {
        emailSenderCache.remove(id);
    }

    protected Mono<DefaultEmailSender> createDefaultEmailSender(EmailConfig config) {
        return Mono.create(sink -> {
            DefaultEmailSender emailSender;
            synchronized (emailSenderCache) {
                emailSender = emailSenderCache.get(config.getId());
                if (emailSender == null) {
                    emailSender = new DefaultEmailSender();
                    emailSender.setJavaMailSender(config.getMailSender());
                    emailSender.setSender(config.getSender());
                    emailSender.setTemplateService(config.getTemplateService());
                    emailSenderCache.put(config.getId(), emailSender);
                }
                sink.success(emailSender);
            }
        });
    }

    @Getter
    @Setter
    @Builder
    protected static class EmailConfig {
        private String id;

        private SenderTemplateService templateService;

        private String sender;

        private JavaMailSender mailSender;
    }
}
