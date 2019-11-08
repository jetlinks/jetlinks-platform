package org.jetlinks.platform.manager.web;

import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.service.ReactiveCrudService;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.platform.manager.entity.SmsSenderEntity;
import org.jetlinks.platform.manager.service.SmsSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
@RestController
@RequestMapping("/sms-sender")
@Authorize
@Resource(id = "sms-sender", name = "短信服务商管理")
public class SmsSenderController implements ReactiveServiceCrudController<SmsSenderEntity, String> {

    @Autowired
    private SmsSenderService smsSenderService;

    @Override
    public ReactiveCrudService<SmsSenderEntity, String> getService() {
        return smsSenderService;
    }
}
