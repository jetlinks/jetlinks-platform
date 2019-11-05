package org.jetlinks.platform.manager.web;

import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.authorization.annotation.ResourceAction;
import org.hswebframework.web.crud.service.ReactiveCrudService;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.platform.manager.entity.RuleModelEntity;
import org.jetlinks.platform.manager.service.RuleModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("rule-engine/model")
@Resource(id="rule-model",name = "规则引擎-模型")
public class RuleModelController implements ReactiveServiceCrudController<RuleModelEntity,String> {

    @Autowired
    private RuleModelService ruleModelService;

    @Override
    public ReactiveCrudService<RuleModelEntity, String> getService() {
        return ruleModelService;
    }

    @PostMapping("/{id}/_deploy")
    @ResourceAction(id="deploy",name = "发布")
    public Mono<Boolean> deploy(@PathVariable String id){
        return ruleModelService.deploy(id);
    }
}
