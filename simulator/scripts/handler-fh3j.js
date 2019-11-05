var _logger = logger;
var _simulator = simulator;

var count = 1;
var topics = {
        fire_alarm: function (session) {
            var deviceId = "863703032301165";
            var topic = "/chiefdata/push/fire_alarm/department/1/area/1/dev/" + deviceId;
            var json = {
                "devid": deviceId, // 设备编号 "pid": "TBS-110", // 设备型号
                "pname": "TBS-110", // 设备型号名称 "cid": 34, // 单位 ID
                "aid": 1, // 区域 ID
                "a_name": "未来科技城", // 区域名称 "bid": 2, // 建筑 ID
                "b_name": "C2 栋", // 建筑名称
                "lid": 5, // 位置 ID
                "l_name": "4-5-201", // 位置名称
                "time": "2018-01-04 16:28:50", // 消息时间
                "alarm_type": 1, // 报警类型
                "alarm_type_name": "火灾报警", // 报警描述
                "event_id": 32, // 事件 ID
                "event_count": count++, // 该事件消息次数
                "device_type": 1, // 设备的产品类型(1:烟感、2:温感、3:可燃气体、4:手报、5:声光 报、6:网关)
                "comm_type": 2, // 设备的通信方式(1:LoRaWAN、2:NB-IoT)
                "first_alarm_time": "2019-11-04 16:28:50",
                "last_alarm_time": "2019-11-04 16:28:50",
                "lng": 22.22,
                "lat": 23.23
            };

            session.sendMessage(topic, JSON.stringify(json))
        },
        fault_alarm: function (session) {
            var deviceId = "863703032301165";
            var topic = "/chiefdata/push/fault_alarm/department/1/area/1/dev/" + deviceId;
            var json = {
                "devid": deviceId, // 设备编号
                "pid": "TBS-110", // 设备型号
                "pname": "TBS-110", // 设备型号名称
                "cid": 34, // 单位 ID
                "aid": 1, // 区域 ID
                "a_name": "未来科技城", // 区域名称
                "bid": 2, // 建筑 ID
                "b_name": "C2 栋", // 建筑名称
                "lid": 5, // 位置 ID
                "l_name": "4-5-201", // 位置名称
                "time": "2018-01-04 16:28:50", // 消息时间
                "alarm_type": 2, // 报警类型
                "alarm_type_name": "低电压报警", // 报警描述
                "event_id": 32, // 事件 ID
                "event_count": 1, // 该事件消息次数
                "device_type": 1, // 设备的产品类型(1:烟感、2:温感、3:可燃气体...)
                "comm_type": 2, // 设备的通信方式(1:LoRaWAN、2:NB-IoT)
                "first_alarm_time": "2018-01-04 16:28:50",
                "last_alarm_time": "2018-01-04 16:28:50",
                "lng": 22.22,
                "lat": 23.23
            };
            session.sendMessage(topic, JSON.stringify(json));
        }
    }
;


simulator.onEvent(function (index, session) {
    topics.fault_alarm(session);
});

simulator.onConnect(function (session) {
    // _logger.info("[{}]:连接成功",session.auth.clientId)
});

simulator.onAuth(function ( index,auth) {
    auth.setClientId("test"+index );
    auth.setUsername("test" );
    auth.setPassword("test" );

});