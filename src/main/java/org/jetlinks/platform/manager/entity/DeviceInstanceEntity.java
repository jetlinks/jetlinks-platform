package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.ezorm.rdb.mapping.annotation.ColumnType;
import org.hswebframework.ezorm.rdb.mapping.annotation.DefaultValue;
import org.hswebframework.ezorm.rdb.mapping.annotation.EnumCodec;
import org.hswebframework.ezorm.rdb.mapping.annotation.JsonCodec;
import org.hswebframework.web.api.crud.entity.GenericEntity;
import org.hswebframework.web.api.crud.entity.RecordCreationEntity;
import org.hswebframework.web.crud.generator.Generators;
import org.hswebframework.web.validator.CreateGroup;
import org.jetlinks.platform.manager.enums.DeviceState;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.sql.JDBCType;
import java.util.Map;

@Getter
@Setter
@Table(name = "dev_device_instance")
public class DeviceInstanceEntity extends GenericEntity<String> implements RecordCreationEntity {

    @Override
    @GeneratedValue(generator = Generators.SNOW_FLAKE)
    public String getId() {
        return super.getId();
    }

    //设备实例名称
    @Column(name = "name")
    @NotBlank(message = "设备名称不能为空", groups = CreateGroup.class)
    private String name;

    //说明
    @Column(name = "describe")
    private String describe;

    //产品id
    @Column(name = "product_id")
    @NotBlank(message = "产品ID不能为空", groups = CreateGroup.class)
    private String productId;

    @Column(name = "product_name")
    @NotBlank(message = "产品名称不能为空", groups = CreateGroup.class)
    private String productName;

    //安全配置
    @Column(name = "security_conf")
    @ColumnType(jdbcType = JDBCType.CLOB)
    @JsonCodec
    private Map<String, Object> security;

    //派生元数据,有的设备的属性，功能，事件可能会动态的添加
    @Column(name = "derive_metadata")
    @ColumnType(jdbcType = JDBCType.CLOB)
    private String deriveMetadata;

    @Column(name = "state")
    @EnumCodec
    @ColumnType(javaType = String.class)
    private DeviceState state;

    @Column(name = "creator_id")
    private String creatorId;

    @Column(name = "creator_name")
    private String creatorName;

    @Column(name = "create_time")
    @DefaultValue(generator = Generators.CURRENT_TIME)
    private Long createTime;

    //注册时间
    @Column(name = "registry_time")
    private Long registryTime;

}
