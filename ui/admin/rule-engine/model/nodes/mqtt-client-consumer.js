define(["miniui-tools"], function (tools) {

    return {
        init: function (panel, model) {
            var mqttConnect = mini.getByName("config.clientId");
            mqttConnect.on("buttonclick",function (e) {
                tools.openWindow("admin/mqtt/client/list.html?tag=" + "select", "mqtt客户端", "700", "400", function (data) {
                    if (data !== 'close'){
                        mqttConnect.setText(data.name);
                        mqttConnect.setValue(data.clientId);
                    }
                })
            })
        },
        debugSupport: true,
        debugStopSupport: true
    }
});