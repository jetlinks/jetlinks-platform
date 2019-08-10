//组件信息
var info = {
    groupId: "org.jetlinks",
    artifactId: "jetlinks-platform",
    version: "1.0.0",
    website: "jetinks.org",
    author: "zhouhao",
    comment: ""
};

//版本更新信息
var versions = [
    // {
    //     version: "3.0.0",
    //     upgrade: function (context) {
    //         java.lang.System.out.println("更新到3.0.2了");
    //     }
    // }
];
var JDBCType = java.sql.JDBCType;

function install(context) {
    var database = context.database;

    database.createOrAlter("rule_model")
        .addColumn().name("id").alias("id").varchar(32).notNull().primaryKey().comment("id").commit()
        .addColumn().name("name").varchar(128).notNull().comment("名称").commit()
        .addColumn().name("type").varchar(128).comment("模型ID").commit()
        .addColumn().name("description").varchar(1024).comment("说明").commit()
        .addColumn().name("model_type").varchar(128).notNull().comment("模型类型").commit()
        .addColumn().name("model_meta").clob().notNull().comment("模型元数据").commit()
        .addColumn().name("version").number(32).notNull().comment("用户状态").commit()
        .addColumn().name("modifier_id").alias("modifierId").varchar(32).comment("修改人ID").commit()
        .addColumn().name("modify_time").alias("modifyTime").number(32).comment("修改时间").commit()
        .addColumn().name("state").alias("state").varchar(32).comment("创建者ID").commit()
        .addColumn().name("creator_id").alias("creatorId").varchar(32).comment("创建者ID").commit()
        .addColumn().name("create_time").alias("createTime").number(32).notNull().comment("创建时间").commit()
        .index().name("idx_ri_creator_id").column("creator_id").commit()
        .comment("规则模型").commit();

    database.createOrAlter("rule_instance")
        .addColumn().name("id").alias("id").varchar(32).notNull().primaryKey().comment("id").commit()
        .addColumn().name("model_id").varchar(128).notNull().comment("模型ID").commit()
        .addColumn().name("name").varchar(128).notNull().comment("名称").commit()
        .addColumn().name("description").varchar(1024).comment("说明").commit()
        .addColumn().name("model_type").varchar(128).notNull().comment("模型类型").commit()
        .addColumn().name("model_meta").clob().notNull().comment("模型元数据").commit()
        .addColumn().name("model_version").number(32).notNull().comment("用户状态").commit()
        .addColumn().name("scheduler_id").alias("schedulerId").varchar(32).comment("调度器ID").commit()
        .addColumn().name("instance_detail_json").alias("instanceDetailJson").clob().comment("实例明细JSON").commit()

        .addColumn().name("state").alias("state").varchar(32).comment("创建者ID").commit()
        .addColumn().name("creator_id").alias("creatorId").varchar(32).comment("创建者ID").commit()
        .addColumn().name("create_time").alias("createTime").number(32).notNull().comment("创建时间").commit()
        .index().name("idx_ri_model_id").column("model_id").commit()
        .index().name("idx_ri_state").column("state").commit()

        .comment("规则实例表").commit();

    database.createOrAlter("dev_product")
        .addColumn().name("id").alias("id").comment("ID").jdbcType(java.sql.JDBCType.VARCHAR).length(32).primaryKey().commit()
        .addColumn().name("name").alias("name").notNull().comment("名称").jdbcType(java.sql.JDBCType.VARCHAR).length(32).commit()
        .addColumn().name("metadata").alias("metadata").comment("元数据").clob().commit()
        .addColumn().name("classified_id").alias("classifiedId").comment("分类ID").varchar(32).commit()

        .addColumn().name("message_protocol").alias("messageProtocol").comment("消息协议").varchar(32).commit()
        .addColumn().name("transport_protocol").alias("transport_protocol").comment("传输协议").varchar(32).commit()
        .addColumn().name("network_way").alias("networkWay").comment("入网方式").varchar(32).commit()
        .addColumn().name("device_type").alias("deviceType").comment("类型:DEVICE(设备),GATEWAY(网关)").varchar(32).commit()
        .addColumn().name("security_conf").alias("security").comment("安全配置").clob().commit()
        .addColumn().name("state").alias("state").comment("状态").jdbcType(java.sql.JDBCType.DECIMAL).length(4, 0).commit()

        .addColumn().name("creator_id").alias("creatorId").comment("创建人id").jdbcType(java.sql.JDBCType.VARCHAR).length(32).commit()
        .addColumn().name("creator_name").alias("creatorName").comment("创建人").jdbcType(java.sql.JDBCType.VARCHAR).length(128).commit()
        .addColumn().name("create_time").alias("createTime").comment("创建时间").jdbcType(java.sql.JDBCType.DECIMAL).length(32, 0).commit()

        .addColumn().name("describe").alias("describe").comment("说明").jdbcType(java.sql.JDBCType.VARCHAR).length(256).commit()
        .index().name("idx_prod_state").column("state").commit()
        .index().name("idx_prod_creator_id").column("creator_id").commit()
        .comment("设备产品").commit();

    database.createOrAlter("dev_device_instance")
        .addColumn().name("id").alias("id").comment("ID").jdbcType(java.sql.JDBCType.VARCHAR).length(32).primaryKey().commit()
        .addColumn().name("name").alias("name").notNull().comment("名称").jdbcType(java.sql.JDBCType.VARCHAR).length(32).commit()

        .addColumn().name("describe").alias("describe").comment("说明").jdbcType(java.sql.JDBCType.VARCHAR).length(256).commit()
        .addColumn().name("product_id").alias("productId").notNull().comment("产品ID").varchar(32).commit()
        .addColumn().name("product_name").alias("productName").notNull().comment("产品名称").varchar(32).commit()

        .addColumn().name("derive_metadata").alias("deriveMetadata").comment("派生元数据").clob().commit()
        .addColumn().name("security_conf").alias("security").comment("安全配置").clob().commit()
        .addColumn().name("state").alias("state").comment("状态").notNull().varchar(16).commit()

        .addColumn().name("creator_id").alias("creatorId").notNull().comment("创建人id").jdbcType(java.sql.JDBCType.VARCHAR).length(32).commit()
        .addColumn().name("creator_name").alias("creatorName").comment("创建人").jdbcType(java.sql.JDBCType.VARCHAR).length(128).commit()
        .addColumn().name("create_time").alias("createTime").notNull().comment("创建时间").jdbcType(java.sql.JDBCType.DECIMAL).length(32, 0).commit()
        .addColumn().name("registry_time").alias("registryTime").comment("注册时间").jdbcType(java.sql.JDBCType.DECIMAL).length(32, 0).commit()

        .index().name("idx_dci_product_id").column("product_id").commit()
        .index().name("idx_dci_creator_id").column("creator_id").commit()
        .index().name("idx_dci_state").column("state").commit()
        .comment("设备实例").commit();

}

//设置依赖
dependency.setup(info)
    .onInstall(install)
    .onUpgrade(function (context) { //更新时执行
        var upgrader = context.upgrader;
        upgrader.filter(versions)
            .upgrade(function (newVer) {
                newVer.upgrade(context);
            });
    })
    .onUninstall(function (context) { //卸载时执行

    });