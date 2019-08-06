package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.User;
import org.hswebframework.web.commons.entity.RecordCreationEntity;
import org.hswebframework.web.commons.entity.RecordModifierEntity;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;
import org.jetlinks.platform.manager.enums.RuleInstanceState;

import javax.persistence.Column;
import javax.persistence.Table;

@Getter
@Setter
@Table(name = "rule_model")
public class RuleModelEntity extends SimpleGenericEntity<String> implements RecordCreationEntity, RecordModifierEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "model_type")
    private String modelType;

    @Column(name = "model_meta")
    private String modelMeta;

    @Column(name = "version")
    private Integer version;

    @Column(name = "creator_id")
    private String creatorId;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "modifier_id")
    private String modifierId;

    @Column(name = "modify_time")
    private Long modifyTime;


    public RuleInstanceEntity toInstance() {
        RuleInstanceEntity instanceEntity = new RuleInstanceEntity();
        // rule-1:1
        instanceEntity.setId(getId().concat("-").concat(String.valueOf(getVersion())));
        instanceEntity.setState(RuleInstanceState.stopped);
        instanceEntity.setModelId(getId());
        instanceEntity.setCreateTimeNow();
        instanceEntity.setDescription(getDescription());
        instanceEntity.setName(getName());
        instanceEntity.setModelVersion(getVersion());
        instanceEntity.setModelMeta(getModelMeta());
        instanceEntity.setModelType(getModelType());

        Authentication.current()
                .map(Authentication::getUser)
                .map(User::getId)
                .ifPresent(instanceEntity::setCreatorId);

        return instanceEntity;

    }
}
