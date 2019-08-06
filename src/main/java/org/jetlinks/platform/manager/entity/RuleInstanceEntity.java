package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.commons.entity.RecordCreationEntity;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;
import org.jetlinks.platform.manager.enums.RuleInstanceState;

import javax.persistence.Column;
import javax.persistence.Table;

@Getter
@Setter
@Table(name = "rule_instance")
public class RuleInstanceEntity extends SimpleGenericEntity<String> implements RecordCreationEntity {

    @Column(name = "model_id")
    private String modelId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "model_type")
    private String modelType;

    @Column(name = "model_meta")
    private String modelMeta;

    @Column(name = "model_version")
    private Integer modelVersion;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "creator_id")
    private String creatorId;

    @Column(name = "state")
    private RuleInstanceState state;

    @Column(name = "scheduler_id")
    private String schedulerId;

    @Column(name = "instance_detail_json")
    private String instanceDetailJson;

}
