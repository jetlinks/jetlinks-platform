package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.commons.entity.RecordCreationEntity;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;
import org.jetlinks.platform.manager.enums.DeviceType;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Map;

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
    @Column(name = "device_type")
    private DeviceType deviceType;

    //安全配置
    @Column(name = "security_conf")
    private Map<String, Object> security;

    //产品状态
    @Column(name = "state")
    private Byte state;

    @Column(name = "creator_id")
    private String creatorId;

    @Column(name = "create_time")
    private Long createTime;

}
