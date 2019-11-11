define(["miniui-tools"], function (tools) {

    return {
        init: function (panel, model) {
            var senderId = mini.getbyName("config.senderId");
            senderId.on("buttonclick", function () {
                tools.openWindow("admin/sms/list.html?selector=1", "选择发信人", "600", "400", function (data) {
                    if (data !== "cancel" || data !== "close") {
                        senderId.setValue(data.id);
                        senderId.setText(data.name)
                    }
                });
            });

            var templateId = mini.getbyName("config.templateId");
            templateId.on("buttonclick", function () {
                tools.openWindow("admin/sender-template/list.html?selector=1&type=sms", "选择模板", "600", "400", function (data) {
                    if (data !== "cancel" || data !== "close") {
                        templateId.setValue(data.id);
                        templateId.setText(data.name);
                    }
                });
            });
        },
        debugSupport: true
    }
});