/**
 * 烟感设备模拟器
 */
var _logger = logger;
var _simulator = simulator;
var propertyDepository = {};
var count = 1;
//事件类型
var events = {
    fire_alarm: function (index, session, devicePrefix) {
        if (!devicePrefix) {
            devicePrefix = "fh3j";
        }
        var deviceId = devicePrefix + index;
        var topic = "/chiefdata/push/fire_alarm/department/1/area/1/dev/" + deviceId;
        var json = {
            "devid": deviceId, // 设备编号 "pid": "TBS-110", // 设备型号
            "pname": "TBS-110", // 设备型号名称 "cid": 34, // 单位 ID
            "aid": 1, // 区域 ID
            "a_name": "未来科技城", // 区域名称 "bid": 2, // 建筑 ID
            "b_name": "C2 栋", // 建筑名称
            "lid": 5, // 位置 ID
            "l_name": "4-5-201", // 位置名称
            //"time": "2019-11-06 16:28:50", // 消息时间
            "alarm_type": 1, // 报警类型
            "alarm_type_name": "火灾报警", // 报警描述
            "event_id": 32, // 事件 ID
            "event_count": count++, // 该事件消息次数
            "device_type": 1, // 设备的产品类型(1:烟感、2:温感、3:可燃气体、4:手报、5:声光 报、6:网关)
            "comm_type": 2, // 设备的通信方式(1:LoRaWAN、2:NB-IoT)
            "first_alarm_time": "2019-11-06 16:28:50",
            "last_alarm_time": "2019-11-06 16:28:50",
            "lng": 22.22,
            "lat": 23.23
        };
        session.sendMessage(topic, JSON.stringify(json))
    },
    fault_alarm: function (index, session, devicePrefix) {
        if (!devicePrefix) {
            devicePrefix = "fh3j";
        }
        var deviceId = devicePrefix + index;
        var topic = "/chiefdata/push/fault_alarm/department/1/area/1/dev/" + deviceId;
        var json = {
            "devid": deviceId, // 设备编号
            "pid": "TBS-110", // 设备型号
            "pname": "TBS-110", // 设备型号名称
            // "cid": 34, // 单位 ID
            // "aid": 1, // 区域 ID
            "a_name": "未来科技城", // 区域名称
            "bid": 2, // 建筑 ID
            "b_name": "C2 栋", // 建筑名称
            "lid": 5, // 位置 ID
            "l_name": "4-5-201", // 位置名称
            //"time": "2018-01-04 16:28:50", // 消息时间
            "alarm_type": 2, // 报警类型
            "alarm_type_name": "低电压报警", // 报警描述
            "event_id": 32, // 事件 ID
            "event_count": 1, // 该事件消息次数
            "device_type": 1, // 设备的产品类型(1:烟感、2:温感、3:可燃气体...)
            "comm_type": 2, // 设备的通信方式(1:LoRaWAN、2:NB-IoT)
            "first_alarm_time": "2018-01-04 16:28:50",
            "last_alarm_time": "2018-01-04 16:28:50",
            // "lng": 22.22,
            // "lat": 23.23
        };
        session.sendMessage(topic, JSON.stringify(json));
    }
};
//功能类型
var functions = {
    close_alarm: function (message, session) {
        _simulator.runDelay(function () {
            session.sendMessage("/invoke-function-reply", JSON.stringify({
                functionId: "close_alarm",
                messageId: message.messageId,
                output: {
                    "result": "0",
                    "requestInput": JSON.parse(message.args)
                },
                code: 0,
                timestamp: new Date().getTime(),
                success: true
            }))
        }, 3000);
    }
}
//属性读取
var propertiesHandle = {
    "read": function (message, session) {
        var initProperties = propertyDepository[message.deviceId];
        if (!initProperties) initProperties = {"pname": "测试设备", "currentTemperature": "31"};
        var resultProperties = {};
        var properties = message.properties;
        if (properties) {
            for (var i = 0; i < properties.length; i++) {
                resultProperties[properties[i]] = initProperties[properties[i]]
            }
        }
        session.sendMessage("/read-property-reply", JSON.stringify({
            messageId: message.messageId,
            deviceId: message.deviceId,
            timestamp: new Date().getTime(),
            properties: resultProperties,
            success: true
        }));
    },
    "write": function (message, session) {
        var initProperties = propertyDepository[message.deviceId];
        if (!initProperties) initProperties = {};
        var properties = message.properties;
        if (properties) {
            for (var key in properties) {
                initProperties[key] = properties[key];
            }
        }
        propertyDepository[message.deviceId] = initProperties;
        session.sendMessage("/write-property-reply", JSON.stringify({
            messageId: message.messageId,
            timestamp: new Date().getTime(),
            properties: JSON.parse(properties),
            success: true
        }));
    }
};

//事件上报
simulator.onEvent(function (index, session) {
    events.fault_alarm(index, session);
});

//功能调用
simulator.bindHandler("/invoke-function", function (message, session) {
    var functionId = message.function;
    if (functions[functionId]) {
        functions[functionId](message, session)
    } else {
        session.sendMessage("/invoke-function-reply", JSON.stringify({
            functionId: functionId,
            messageId: message.messageId,
            output: "不支持的设备事件类型",
            code: -1,
            timestamp: new Date().getTime(),
            success: false
        }))
    }
});

simulator.bindHandler("/read-property", function (message, session) {
    propertiesHandle["read"](message, session);
});
simulator.bindHandler("/write-property", function (message, session) {
    propertiesHandle["write"](message, session);
});


simulator.onConnect(function (session) {
     _logger.info("[{}]:连接成功",session.auth.clientId);
    propertyDepository[session.auth.clientId] = {"pname": "测试设备型号", "currentTemperature": "31"};
    _logger.info("[{}]:propertyDepository Value",JSON.stringify(propertyDepository));
});

simulator.onAuth(function (index, auth) {
    auth.setClientId("fh3j" + index);
    auth.setUsername("admin");
    auth.setPassword("admin");
});