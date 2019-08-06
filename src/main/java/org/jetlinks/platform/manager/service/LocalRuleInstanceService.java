package org.jetlinks.platform.manager.service;

import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.GenericEntityService;
import org.jetlinks.platform.manager.dao.RuleInstanceDao;
import org.jetlinks.platform.manager.entity.RuleInstanceEntity;
import org.jetlinks.platform.manager.entity.RuleModelEntity;
import org.jetlinks.platform.manager.enums.RuleInstanceState;
import org.jetlinks.rule.engine.api.Rule;
import org.jetlinks.rule.engine.api.RuleEngine;
import org.jetlinks.rule.engine.api.RuleInstanceContext;
import org.jetlinks.rule.engine.api.model.RuleEngineModelParser;
import org.jetlinks.rule.engine.api.model.RuleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

@Service
public class LocalRuleInstanceService extends GenericEntityService<RuleInstanceEntity, String> implements CommandLineRunner {

    @Autowired
    private RuleInstanceDao ruleInstanceDao;

    @Autowired
    private RuleEngineModelParser modelParser;

    @Autowired
    private RuleEngine ruleEngine;

    public String deploy(RuleModelEntity modelEntity) {
        //解析
        modelParser.parse(modelEntity.getModelType(), modelEntity.getModelMeta());

        return insert(modelEntity.toInstance());
    }

    public void start(String id) {
        Assert.hasText(id, "id不能为空");
        RuleInstanceEntity instance = Objects.requireNonNull(selectByPk(id), "实例不存在");
        doStart(instance);
        createUpdate()
                .set(RuleInstanceEntity::getState, RuleInstanceState.started)
                .where(RuleInstanceEntity::getId, id)
                .exec();
    }


    public void stop(String id) {
        Optional.ofNullable(id)
                .map(ruleEngine::getInstance)
                .ifPresent(RuleInstanceContext::stop);
        createUpdate()
                .set(RuleInstanceEntity::getState, RuleInstanceState.stopped)
                .where(RuleInstanceEntity::getId, id)
                .exec();
    }

    protected void doStart(RuleInstanceEntity instance) {
        RuleModel ruleModel = modelParser.parse(instance.getModelType(), instance.getModelMeta());

        Rule rule = new Rule();
        rule.setId(instance.getId());
        rule.setVersion(instance.getModelVersion());
        rule.setModel(ruleModel);
        ruleEngine.startRule(rule);
    }

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public CrudDao<RuleInstanceEntity, String> getDao() {
        return ruleInstanceDao;
    }


    @Override
    public void run(String... args) {
        createQuery()
                .where(RuleInstanceEntity::getState, RuleInstanceState.started)
                .listNoPaging()
                .forEach(this::doStart);
    }
}
