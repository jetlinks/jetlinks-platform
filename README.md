# JetLinks Platform

JetLinks的核心功能整合,以及实现简单的业务功能.

# 启动

启动项目需要`Redis`,`ElasticSearch`,
你可以通过配置文件`application.yml`的配置项
`jetlinks.redis`以及`spring.elasticsearch.jest.uris`配置相关信息


# 测试
1. 成功启动服务后,运行模拟器`simulator/start.sh`,观察控制台日志.
2. 浏览器打开 http://127.0.0.1:8844/device/test0/property/name ,观察服务以及模拟器控制台.
3. 在浏览器中实时获取设备上报数据
```js
 // F12打开浏览器开发者模式执行
 var source= new EventSource("http://localhost:8844/device/test0/event");
 source.onmessage = function(e){
     console.log(e.data)
 }
 
```

模拟器使用方式请[点击查看](https://github.com/jetlinks/device-simulator)

# 修改数据库类型
直接修改`application.yml`配置项`spring.r2dbc`以及`easyorm.`相关配置即可。
数据库支持`H2`,`Mysql`,`Oracle`,`PostgreSQL`,`SqlServer`

# 业务功能

 * [ ]  协议管理
     * [ ] 内置协议
     * [ ] 自定义协议
     * [ ] jar包实现协议SPI
     * [ ] 在线配置(脚本)
 * [ ] 设备管理
    * [ ] 设备型号
         * [ ] 设备型号模型
    * [ ] 设备实例
         * [ ] 设备注册,导入
         * [ ] 设备属性,状态
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
    * [ ] 用户管理
    * [ ] 权限维度管理
    * [ ] 权限设置
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


