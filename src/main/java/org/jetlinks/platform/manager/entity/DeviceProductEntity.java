package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.ezorm.rdb.mapping.annotation.ColumnType;
import org.hswebframework.ezorm.rdb.mapping.annotation.JsonCodec;
import org.hswebframework.web.api.crud.entity.GenericEntity;
import org.hswebframework.web.api.crud.entity.RecordCreationEntity;
import org.jetlinks.platform.manager.enums.DeviceType;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import java.sql.JDBCType;
import java.util.Map;

@Getter
@Setter
@Table(name = "dev_product")
public class DeviceProductEntity extends GenericEntity<String> implements RecordCreationEntity {

    @Override
    @GeneratedValue(generator = "snow_flake")
    public String getId() {
        return super.getId();
    }

    //名称
    @Column(name = "name")
    private String name;

    //所属项目
    @Column(name = "project_id",length = 32)
    private String projectId;

    //项目名称
    @Column(name = "project_name")
    private String projectName;

    //说明
    @Column(name = "describe")
    private String describe;

    //分类ID
    @Column(name = "classified_id")
    private String classifiedId;

    //消息协议: Alink,JetLinks
    @Column(name = "message_protocol")
    private String messageProtocol;

    //协议元数据
    @Column(name = "metadata")
    @ColumnType(jdbcType = JDBCType.CLOB)
    private String metadata;

    //传输协议: MQTT,COAP,UDP
    @Column(name = "transport_protocol")
    private String transportProtocol;

    //入网方式: 直连,组网...
    @Column(name = "network_way")
    private String networkWay;

    //设备类型: 网关，设备
    @Column(name = "device_type")
    @ColumnType(javaType =String.class )
    private DeviceType deviceType;

    //安全配置
    @Column(name = "security_conf")
    @JsonCodec
    @ColumnType(jdbcType = JDBCType.CLOB)
    private Map<String, Object> security;

    //产品状态
    @Column(name = "state")
    private Byte state;

    @Column(name = "creator_id")
    private String creatorId;

    @Column(name = "create_time")
    private Long createTime;

}
