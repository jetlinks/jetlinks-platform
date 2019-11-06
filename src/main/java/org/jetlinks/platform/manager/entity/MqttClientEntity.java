package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.ezorm.rdb.mapping.annotation.ColumnType;
import org.hswebframework.ezorm.rdb.mapping.annotation.JsonCodec;
import org.hswebframework.web.api.crud.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.sql.JDBCType;
import java.util.Map;

@Table(name = "rule_mqtt_client")
@Getter
@Setter
public class MqttClientEntity extends GenericEntity<String> {

    @Column
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private Integer port;

    @Column(nullable = false)
    private String clientId;

    @Column
    private Byte status;

    @Column
    @JsonCodec
    @ColumnType(jdbcType = JDBCType.CLOB)
    private Map<String,Object> secureConfiguration;

    @Column
    @JsonCodec
    @ColumnType(jdbcType = JDBCType.CLOB)
    private Map<String,Object> sslConfiguration;

}
