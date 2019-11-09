package org.jetlinks.platform.manager.service;

import org.hswebframework.web.crud.service.GenericReactiveCrudService;
import org.jetlinks.platform.manager.entity.SenderTemplateEntity;
import org.jetlinks.platform.manager.notify.SmsTemplateManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
@Service
public class SenderTemplateService extends GenericReactiveCrudService<SenderTemplateEntity, String> implements SmsTemplateManager {


    @Override
    public Mono<String> getTemplate(String id) {
        return createQuery()
                .where(SenderTemplateEntity::getId, id)
                .fetchOne()
                .map(SenderTemplateEntity::getTemplate);
    }

}
