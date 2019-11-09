define(["miniui-tools"], function (tools) {
    
    return {
        init: function (panel, model) {
            var sender = mini.getByName("config.senderId");
            var template = mini.getByName("config.templateId");
            sender.on("buttonclick", function (e) {
                tools.openWindow("admin/rule-engine/manager/email/list.html?tag=select", "发件人选择", "1000", "500", function (data) {
                    if (data !== 'close') {
                        sender.setText(data.name);
                        sender.setValue(data.id);
                    }
                })
            })

            template.on("buttonclick", function (e) {
                tools.openWindow("admin/sender-template/list.html?selector=1", "选择模板", "1000", "500", function (data) {
                    if (data !== "cancel" || data !== "close") {
                        template.setValue(data.id);
                        template.setText(data.name);
                    }
                });
            })
        },
        debugSupport: true
    }
});