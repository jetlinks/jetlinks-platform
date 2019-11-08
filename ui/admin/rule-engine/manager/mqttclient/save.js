importResource("/admin/css/common.css");


importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools","search-box"], function (request, tools, searchBox) {

        var func = request.post;
        var id = request.getParameter("id");
        var api = "mqtt-client";
        if (id) {
            loadData(id);
            api += "/" + id;
            func = request.put;
        }
        $(".save-button").on("click", (function () {
            require(["message"], function (message) {
                var data = getDataAndValidate();
                if(!id){
                    data.status = "0";
                }
                if (!data) return;
                var loading = message.loading("提交中");
                func(api, data, function (response) {
                    loading.close();
                    if (response.status === 200) {
                        message.showTips("保存成功");
                        if (!id) id = response.result;
                    } else {
                        message.showTips("保存失败:" + response.message, "danger");
                        if (response.result)
                            tools.showFormErrors("#basic-info", response.result);
                    }
                })
            });
        }));
    });
});

function loadData(id) {
    require(["request", "message"], function (request, message) {
        var loading = message.loading("加载中...");
        request.get("mqtt-client/" + id, function (response) {
            loading.hide();
            if (response.status === 200) {
                var form = new mini.Form("#basic-info");
                form.setData(response.result);
               // form.getField("id").setReadOnly(true);
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
