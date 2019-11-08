package org.jetlinks.platform.manager.web;

import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.service.ReactiveCrudService;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.platform.manager.entity.SenderTemplateEntity;
import org.jetlinks.platform.manager.service.SenderTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
@RestController
@RequestMapping("/sender-template")
@Authorize
@Resource(id = "sender-template", name = "消息发送模板管理")
public class SenderTemplateController implements ReactiveServiceCrudController<SenderTemplateEntity, String> {

    @Autowired
    private SenderTemplateService templateService;

    @Override
    public ReactiveCrudService<SenderTemplateEntity, String> getService() {
        return templateService;
    }
}
