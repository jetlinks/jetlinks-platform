package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.commons.entity.RecordCreationEntity;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;
import org.jetlinks.platform.manager.enums.DeviceState;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Map;

@Getter
@Setter
@Table(name = "dev_device_instance")
public class DeviceInstanceEntity extends SimpleGenericEntity<String> implements RecordCreationEntity {

    //设备实例名称
    @Column(name = "name")
    private String name;

    //说明
    @Column(name = "describe")
    private String describe;

    //产品id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name")
    private String productName;

    //安全配置
    @Column(name = "security_conf")
    private Map<String, Object> security;

    //派生元数据,有的设备的属性，功能，事件可能会动态的添加
    @Column(name = "derive_metadata")
    private String deriveMetadata;

    @Column(name = "state")
    private DeviceState state;

    @Column(name = "creator_id")
    private String creatorId;

    @Column(name = "creator_name")
    private String creatorName;

    @Column(name = "create_time")
    private Long createTime;

    //注册时间
    @Column(name = "registry_time")
    private Long registryTime;

}
