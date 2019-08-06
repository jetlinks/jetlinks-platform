package org.jetlinks.platform.manager.web;

import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.SimpleGenericEntityController;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.jetlinks.platform.manager.entity.RuleModelEntity;
import org.jetlinks.platform.manager.service.LocalRuleModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rule-engine/model")
@Authorize(permission = "rule-engine-model",description = "规则引擎-模型管理")
public class RuleEngineModelController implements SimpleGenericEntityController<RuleModelEntity,String, QueryParamEntity> {

    @Autowired
    private LocalRuleModelService localRuleModelService;


    @Override
    public LocalRuleModelService getService() {
        return localRuleModelService;
    }


    @PostMapping("/{id}/deploy")
    public ResponseMessage<String> deploy(@PathVariable String id){
        return ResponseMessage.ok(localRuleModelService.deploy(id));

    }

}
