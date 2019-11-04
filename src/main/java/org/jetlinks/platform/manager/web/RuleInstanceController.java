package org.jetlinks.platform.manager.web;

import org.hswebframework.ezorm.rdb.mapping.ReactiveRepository;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.web.reactive.ReactiveCrudController;
import org.jetlinks.platform.manager.entity.RuleInstanceEntity;
import org.jetlinks.platform.manager.entity.RuleModelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rule-engine/instance")
@Resource(id="rule-instance",name = "规则引擎-实例")
public class RuleInstanceController implements ReactiveCrudController<RuleInstanceEntity,String> {

    @Autowired
    private ReactiveRepository<RuleInstanceEntity, String> repository;

    @Override
    public ReactiveRepository<RuleInstanceEntity, String> getRepository() {
        return repository;
    }
}
