package org.jetlinks.platform.manager.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.SneakyThrows;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.QueryController;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.jetlinks.platform.manager.entity.RuleInstanceEntity;
import org.jetlinks.platform.manager.service.LocalRuleInstanceService;
import org.jetlinks.rule.engine.api.RuleData;
import org.jetlinks.rule.engine.api.RuleDataHelper;
import org.jetlinks.rule.engine.api.RuleEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/rule-engine/instance")
public class RuleEngineInstanceController implements QueryController<RuleInstanceEntity,String, QueryParamEntity> {

    @Autowired
    private RuleEngine engine;

    @Autowired
    private LocalRuleInstanceService instanceService;

    @PostMapping("/{instanceId}/stop")
    @SneakyThrows
    public ResponseMessage<Void> stop(@PathVariable String instanceId) {

        instanceService.stop(instanceId);
        return ResponseMessage.ok();
    }

    @PostMapping("/{instanceId}/start")
    @SneakyThrows
    public ResponseMessage<Void> start(@PathVariable String instanceId) {

        instanceService.start(instanceId);
        return ResponseMessage.ok();
    }

    @PostMapping("/{instanceId}/{startWith}/to/{endWith}")
    @SneakyThrows
    public ResponseMessage<Object> execute(@PathVariable String instanceId,
                                           @PathVariable String startWith,
                                           @PathVariable String endWith,
                                           @RequestBody Object payload) {

        RuleData ruleData = RuleData.create(payload);
        RuleDataHelper.markSyncReturn(ruleData, endWith);
        RuleDataHelper.markStartWith(ruleData, startWith);

        return ResponseMessage.ok(JSON.toJSONString(engine.getInstance(instanceId)
                .execute(ruleData)
                .toCompletableFuture()
                .get(10, TimeUnit.SECONDS)
                .getData(), SerializerFeature.PrettyFormat));
    }

    @Override
    public LocalRuleInstanceService getService() {
        return instanceService;
    }
}
