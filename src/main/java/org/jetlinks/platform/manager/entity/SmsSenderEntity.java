package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.ezorm.rdb.mapping.annotation.ColumnType;
import org.hswebframework.web.api.crud.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.sql.JDBCType;
import java.util.Map;

@Table(name = "rule_sms_sender")
@Getter
@Setter
public class SmsSenderEntity extends GenericEntity<String> {

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String provider;

    @Column
    @ColumnType(jdbcType = JDBCType.CLOB)
    private Map<String, Object> configuration;
}
