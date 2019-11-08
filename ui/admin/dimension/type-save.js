importResource("/admin/css/common.css");


var defaultPassword = Math.random() + "";

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools"], function (request, tools) {

        var id = request.getParameter("id");

        $(".save-button").on("click", (function () {
            require(["message"], function (message) {
                var data = getDataAndValidate();
                if (!data) return;
                var api = "dimension-type";
                var func = request.post;
                if (id) {
                    func = request.put;
                    api+= "/" + id;
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

        function getDataAndValidate() {
            var form = new mini.Form("#basic-info");
            form.validate();
            if (form.isValid() === false) {
                return;
            }
            var data = form.getData();
            if (data.password === defaultPassword) {
                delete data.password;
            }
            if (id) data.id = id;
            return data;
        }

    });
});

function loadData(id) {
    require(["request", "message"], function (request, message) {
        var loading = message.loading("加载中...");
        request.get("dimension-type/" + id, function (response) {
            loading.hide();
            if (response.status === 200) {
                response.result.password = defaultPassword;
                new mini.Form("#basic-info").setData(response.result);
            } else {
                message.showTips("加载数据失败", "danger");
            }
        });
    });
}
