package org.jetlinks.platform.manager.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.QueryAction;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.service.ReactiveCrudService;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.platform.manager.entity.SmsSenderEntity;
import org.jetlinks.platform.manager.notify.SmsProvider;
import org.jetlinks.platform.manager.service.SmsSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
@RestController
@RequestMapping("/sms-sender")
@Authorize
@Resource(id = "sms-sender", name = "短信发信人管理")
public class SmsSenderController implements ReactiveServiceCrudController<SmsSenderEntity, String> {

    @Autowired
    private SmsSenderService smsSenderService;

    @Autowired
    private List<SmsProvider> providers;

    @Override
    public ReactiveCrudService<SmsSenderEntity, String> getService() {
        return smsSenderService;
    }


    @GetMapping("provider/all")
    @QueryAction
    public Flux<Provider> getAllProvider() {
        return Flux.fromIterable(providers)
                .map(Provider::of);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Provider {
        private String id;
        private String name;

        static Provider of(SmsProvider smsProvider) {
            return new Provider(smsProvider.getProvider(), smsProvider.getName());
        }

    }
}
