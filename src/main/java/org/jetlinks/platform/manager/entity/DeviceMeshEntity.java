package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.ezorm.rdb.mapping.annotation.ColumnType;
import org.hswebframework.web.api.crud.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.sql.JDBCType;

/**
 * 设备组网网格配置
 */
@Table(name = "dev_mesh")
@Getter
@Setter
public class DeviceMeshEntity extends GenericEntity<String> {

    @Column(length = 32)
    private String name;

    @Column(length = 1024, name = "description")
    private String description;

    @Column(length = 32)
    private String format;

    @Column
    @ColumnType(jdbcType = JDBCType.CLOB)
    private String metadata;

}
