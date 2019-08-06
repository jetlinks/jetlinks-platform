# JetLinks Platform

JetLinks的核心功能整合,以及实现简单的业务功能.

 [ ] *. 

# 启动

启动项目需要`Redis`,你可以通过配置文件`application.yml`的配置项`jetlinks.redis`配置redis相关信息


# 测试
1. 成功启动服务后,运行模拟器`simulator/start.sh`,观察控制台日志.
2. 浏览器打开 http://127.0.0.1:8844/device/test0/property/name ,观察服务以及模拟器控制台.

模拟器使用方式请[点击查看](https://github.com/jetlinks/device-simulator)

# 修改数据库类型
直接修改`application.yml`配置项`spring.datasource` 相关配置即可。
数据库支持`H2`,`Mysql`,`Oracle`,`PostgreSQL`

# 业务功能

## 设备管理
TODO

## 规则引擎
TODO

## 前端实现
TODO
