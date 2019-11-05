# JetLinks Platform

JetLinks的核心功能整合,以及实现简单的业务功能.

# 启动

启动项目需要`Redis`,`ElasticSearch`,`Postgresql`
你可以通过配置文件`application.yml`的配置项
`spring.redis`以及`spring.elasticsearch.jest.uris`配置相关信息

也可以使用docker启动相关环境
```bash
   $ cd docker
   $ docker-compose up
```

使用maven命令:`mvn spring-boot:run` 或者使用IDE启动`JetLinksApplication.main`

# 测试
1. 成功启动服务后, 打开浏览器:`http://127.0.0.1:8844` 账号密码:`admin`
2. 使用模拟器模拟设备 

模拟器使用方式请[点击查看](https://github.com/jetlinks/device-simulator)

# 修改数据库类型
直接修改`application.yml`配置项`spring.r2dbc`以及`easyorm.`相关配置即可。
数据库支持`H2`,`Mysql`,`Oracle`,`PostgreSQL`,`SqlServer`

# 业务功能

 * [x]  协议管理
     * [x] 内置协议
     * [x] 自定义协议
     * [x] jar包实现协议SPI
     * [ ] 在线配置(脚本)
 * [ ] 设备管理
    * [x] 设备型号
         * [x] 设备型号模型
    * [x] 设备实例
         * [x] 设备添加,注册
         * [ ] 设备批量导入
         * [x] 设备属性,状态,日志
         * [ ] 设备调试
* [ ] 规则引擎
    * [ ] 规则模型
        * [ ] 定时调度
        * [ ] 设备消息订阅,发送
        * [ ] MQTT 服务端,客户端
        * [ ] SQL
        * [ ] elasticsearch
        * [ ] 动态脚本
        * [ ] 邮件通知
    * [ ] 规则实例
        * [ ] 运行状态
        * [ ] 运行日志
* [ ] 系统管理
    * [x] 用户管理
    * [x] 权限维度管理
    * [x] 权限设置
    * [ ] API管理
    
### 设备定义
```json
      {
       "id": "test",
       "name": "测试",
       "properties": [
         {
           "id": "name",
           "name": "名称",
           "valueType": {
             "type": "string"
           }
         }
       ],
       "functions": [
         {
           "id": "playVoice",
           "name": "播放声音",
           "inputs": [
             {
               "id": "text",
               "name": "文字内容",
               "valueType": {
                 "type": "string"
               }
             }
           ],
           "output": {
             "type": "boolean"
           }
         }
       ],
       "events": [
         {
           "id": "temp_sensor",
           "name": "温度传感器",
           "valueType": {
             "type": "double"
           }
         },
         {
           "id": "fire_alarm",
           "name": "火警",
           "valueType": {
             "type": "object",
             "properties": [
               {
                 "id": "location",
                 "name": "地点",
                 "valueType": {
                   "type": "string"
                 }
               },
               {
                 "id": "lng",
                 "name": "经度",
                 "valueType": {
                   "type": "double"
                 }
               },
               {
                 "id": "lat",
                 "name": "纬度",
                 "valueType": {
                   "type": "double"
                 }
               }
             ]
           }
         }
       ]
     }


```


