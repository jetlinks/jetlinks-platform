package org.jetlinks.platform.manager.web;

import org.hswebframework.ezorm.rdb.mapping.ReactiveRepository;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.web.reactive.ReactiveCrudController;
import org.jetlinks.platform.manager.entity.RuleModelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rule-engine/model")
@Resource(id="rule-model",name = "规则引擎-模型")
public class RuleModelController implements ReactiveCrudController<RuleModelEntity,String> {

    @Autowired
    private ReactiveRepository<RuleModelEntity, String> repository;

    @Override
    public ReactiveRepository<RuleModelEntity, String> getRepository() {
        return repository;
    }
}
