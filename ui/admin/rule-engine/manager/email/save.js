importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message", "search-box"], function (request, tools, message, SearchBox) {

        window.tools = tools;
        var grid = window.grid = mini.get("otherGrid");
        tools.initGrid(grid);
        var func = request.post;
        var id = request.getParameter("id");
        var api = "email-sender";
        if (id) {
            loadData(id);
            api += "/" + id;
            func = request.put;
        }
        $(".save-button").on("click", (function () {
            require(["message"], function (message) {
                var data = getDataAndValidate();
                var configuration = mini.get("otherGrid").getData();
                var configurationMap = {};
                configuration.forEach(function (val) {
                    configurationMap[val.sortIndex] = val.describe;
                });
                data.configuration = configurationMap;
                if (!id) {
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
    window.renderAction = function (e) {
        return tools.createActionButton("删除", "icon-remove", function () {
            e.sender.removeRow(e.record);
        });
    }
});

function loadData(id) {
    require(["request", "message"], function (request, message) {
        var loading = message.loading("加载中...");
        request.get("email-sender/" + id, function (response) {
            loading.hide();
            if (response.status === 200) {
                var form = new mini.Form("#basic-info");
                form.setData(response.result);
                var configuration = response.result.configuration;
                var configurationList = [];
                Object.keys(configuration).forEach(function(index) {
                    configurationList.push({"sortIndex":index,"describe":configuration[index]})
                });
                mini.get("otherGrid").setData(configurationList)
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
