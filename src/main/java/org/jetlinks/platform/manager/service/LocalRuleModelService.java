package org.jetlinks.platform.manager.service;

import org.hswebframework.ezorm.rdb.render.Sql;
import org.hswebframework.web.NotFoundException;
import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.GenericEntityService;
import org.jetlinks.platform.manager.dao.RuleModelDao;
import org.jetlinks.platform.manager.entity.RuleModelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocalRuleModelService extends GenericEntityService<RuleModelEntity, String>  {

    @Autowired
    private RuleModelDao ruleModelDao;

    @Autowired
    private LocalRuleInstanceService instanceService;

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public CrudDao<RuleModelEntity, String> getDao() {
        return ruleModelDao;
    }

    @Override
    public String insert(RuleModelEntity entity) {
        entity.setVersion(1);
        return super.insert(entity);
    }

    @Override
    public int updateByPk(String id, RuleModelEntity entity) {

        return createUpdate()
                .set(entity::getName)
                .set(entity::getDescription)
                .set(entity::getModelType)
                .set(entity::getModelMeta)
                .set(entity::getModifierId)
                .set(entity::getModifyTime)
                .set(RuleModelEntity::getVersion, Sql.build("version+1"))
                .where(RuleModelEntity::getId, id)
                .exec();
    }

    public String deploy(String modelId) {

        return Optional.ofNullable(modelId)
                .map(this::selectByPk)
                .map(instanceService::deploy)
                .orElseThrow(() -> new NotFoundException("模型不存在"));

    }

}
