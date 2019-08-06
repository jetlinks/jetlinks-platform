package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.commons.entity.RecordCreationEntity;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@Setter
@Table(name = "dev_product")
public class DeviceProductEntity extends SimpleGenericEntity<String> implements RecordCreationEntity {

    //名称
    @Column(name = "name")
    private String name;

    //所属项目
    @Column(name = "project_id")
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
    private String metadata;

    //传输协议: MQTT,COAP,UDP
    @Column(name = "transport_protocol")
    private String transportProtocol;

    //入网方式: 直连,组网...
    @Column(name = "network_way")
    private String networkWay;

    //设备类型: 网关，设备
    @Column(name = "product_type")
    private String productType;

    //注册方式: AUTO,MANUAL
    @Column(name = "registry_way")
    private String registryWay;

    //认证方式: 产品认证,设备认证.
    @Column(name = "auth_way")
    private String authWay;

    //安全配置
    @Column(name = "security_conf")
    private Map<String, Object> security;

    //系统配置，用于配置系统需要的设备配置
    @Column(name = "sys_conf")
    private Map<String, Object> sysConfiguration;

    //产品状态
    @Column(name = "state")
    private Byte state;

    @Column(name = "creator_id")
    private String creatorId;

    @Column(name = "create_time")
    private Long createTime;

}
