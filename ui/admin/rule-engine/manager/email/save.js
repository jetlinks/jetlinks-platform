importResource("/admin/css/common.css");


importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools"], function (request, tools) {

        var grid = window.grid = mini.get("otherGrid");
        tools.initGrid(grid);
        var func = request.post;
        var id = request.getParameter("id");
        var api = "email-sender";
        console.log(id)
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
    window.renderAction = function (e) {
        var html = [];
        var row = e.record;
        html.push(tools.createActionButton("添加配置", "fa fa-plus-circle text-success", function () {
            grid.addNode({sortIndex: sortIndex}, row.chidren ? row.chidren.length : 0, row);
        }));

        html.push(tools.createActionButton("删除配置", "fa fa-times text-danger", function () {
            if (row._state === "added") {
                e.sender.removeNode(row);
            } else {
                require(["request", "message"], function (request, message) {
                    message.confirm("确定删除该配置?", function () {
                        e.sender.removeNode(row);
                    });
                })
            }
        }));
        return html.join("");
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
