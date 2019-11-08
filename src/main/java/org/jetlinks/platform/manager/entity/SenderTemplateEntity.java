package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.ezorm.rdb.mapping.annotation.ColumnType;
import org.hswebframework.ezorm.rdb.mapping.annotation.Comment;
import org.hswebframework.ezorm.rdb.mapping.annotation.JsonCodec;
import org.hswebframework.web.api.crud.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.sql.JDBCType;
import java.util.Map;

/**
 * @author wangzheng
 * @see
 * @since 1.0
 */
@Setter
@Getter
@Table(name = "rule_sender_templates")
public class SenderTemplateEntity extends GenericEntity<String> {

    @Comment("模板内容")
    @Column
    @ColumnType(jdbcType = JDBCType.CLOB)
    private String template;

    @Column
    @Comment("模板类型")
    private String type;

    @Column
    @Comment("模板名称")
    private String name;
}
