# JetLinks Platform

JetLinks的核心功能整合,以及实现简单的业务功能.

# 启动

启动项目需要`Redis`,`ElasticSearch`,
你可以通过配置文件`application.yml`的配置项
`jetlinks.redis`以及`spring.elasticsearch.jest.uris`配置相关信息


# 测试
1. 成功启动服务后,运行模拟器`simulator/start.sh`,观察控制台日志.
2. 浏览器打开 http://127.0.0.1:8844/device/test0/property/name ,观察服务以及模拟器控制台.

模拟器使用方式请[点击查看](https://github.com/jetlinks/device-simulator)

# 修改数据库类型
直接修改`application.yml`配置项`spring.datasource` 相关配置即可。
数据库支持`H2`,`Mysql`,`Oracle`,`PostgreSQL`

# 业务功能

## 设备管理

### 设备定义
```json
      {
          "properties":[
               {
                   "id":"currentTemperature",
                   "name":"当前温度",
                   "readonly": true,
                   "valueType":{
                       "type":"double",
                       "unit": "celsiusDegrees"
                   }
               },
               {
                  "id":"cpuUsage",
                  "name":"cpu使用率",
                  "readonly": true,
                  "valueType":{
                      "type":"double",
                      "unit": "percent"
                  }
              }
          ],
          "functions":[
               {
                   "id":"playVoice",
                   "name":"播放声音",
                   "async":false, 
                   "inputs":[
                       {
                          "id":"text",
                          "name":"文字内容",
                          "valueType":{
                            "type":"string"
                          }
                       }
                     ],
                     "output":{
                          "id":"success",
                          "name":"是否成功",
                          "valueType":{
                            "type":"boolean"
                          }
                     }
               }
          ],
          "events":[
               {
                   "id":"temp_sensor",
                   "name":"温度传感器",
                   "parameters":[
                       {
                           "id":"temperature",
                           "name":"温度",
                           "valueType":{
                             "type":"double"
                           }
                       },{
                             "id":"get_time",
                             "name":"采集时间",
                             "valueType":{
                               "type":"timestamp"
                             }
                         }
                   ]
               }
          ]
      }


```

## 规则引擎
TODO

## 前端实现
TODO
