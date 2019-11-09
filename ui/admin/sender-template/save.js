importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message", "search-box"], function (request, tools, message, SearchBox) {

        var typeData = [
            {id:"sms", text:"短信"},
            {id:"email", text:"邮件"}
        ];

        var type = mini.getbyName("type");
        type.setData(typeData);

        window.tools = tools;

        var id = request.getParameter("id");

        if (id) loadData(id);

        $(".save-button").on("click", (function () {
            require(["message"], function (message) {
                var data = getDataAndValidate();

                if (!data) return;
                var api = "sender-template";
                var func = request.post;
                if (id) {
                    api += "/" + id;
                    func = request.put;
                }
                var loading = message.loading("提交中");
                func(api, data, function (response) {
                    loading.close();
                    if (response.status === 200) {
                        message.showTips("保存成功");
                        if (!id) id = response.result.id;
                    } else {
                        message.showTips("保存失败:" + response.message, "danger");
                        if (response.result)
                            tools.showFormErrors("#basic-info", response.result);
                    }
                })
            });
        }));
    });
    window.renderAction = function (e) {
        return tools.createActionButton("删除", "icon-remove", function () {
            e.sender.removeRow(e.record);
        });
    }
});

function loadData(id) {
    require(["request", "message"], function (request, message) {
        var loading = message.loading("加载中...");
        request.get("sender-template/" + id, function (response) {
            loading.hide();
            if (response.status === 200) {
                var form = new mini.Form("#basic-info");
                form.setData(response.result);
            } else {
                message.showTips("加载数据失败", "danger");
            }
        });
    });
}

function getDataAndValidate() {
    var form = new mini.Form("#basic-info");
    form.validate();
    if (form.isValid() === false) {
        return;
    }
    return form.getData();
}
