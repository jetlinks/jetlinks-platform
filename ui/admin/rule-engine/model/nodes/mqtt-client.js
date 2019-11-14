define(["miniui-tools"], function (tools) {

    return {
        init: function (panel, model) {
            var mqttConnect = mini.getByName("config.clientId");
            mqttConnect.on("buttonclick", function (e) {
                tools.openWindow("admin/rule-engine/manager/mqttclient/list.html?tag=select", "mqtt客户端", "1000", "500", function (data) {
                    if (data !== 'close') {
                        mqttConnect.setText(data.name);
                        mqttConnect.setValue(data.id);
                    }
                })
            })
        },
        debugSupport: true,
        debugStopSupport: true
    }
});