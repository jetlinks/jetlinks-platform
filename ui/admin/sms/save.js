importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message", "search-box"], function (request, tools, message, SearchBox) {

        window.tools = tools;
        var grid = window.grid = mini.get("otherGrid");
        tools.initGrid(grid);

        var id = request.getParameter("id");


        //加载服务商下拉列表
        request.get("sms-sender/provider/all", function (res) {
                if (res.status === 200) {
                    console.log(res.result.join(","))
                    mini.getbyName("provider").setData(res.result);
                    if (id) loadData(id);
                }
            }
        );

        $(".save-button").on("click", (function () {
            require(["message"], function (message) {
                var data = getDataAndValidate();
                var configuration = mini.get("otherGrid").getData();
                var configurationMap = {};
                configuration.forEach(function (val) {
                    configurationMap[val.sortIndex] = val.describe;
                });
                data.configuration = configurationMap;

                if (!data) return;
                var api = "sms-sender";
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
        //加載數據
        request.get("sms-sender/" + id, function (response) {
            loading.hide();
            if (response.status === 200) {
                var form = new mini.Form("#basic-info");
                form.setData(response.result);
                var configuration = response.result.configuration;
                var configurationList = [];
                Object.keys(configuration).forEach(function (index) {
                    configurationList.push({"sortIndex": index, "describe": configuration[index]})
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
