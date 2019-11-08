importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "search-box", "message"], function (request, tools, SearchBox, message) {
        var tag = request.getParameter("tag");
        new SearchBox({
            container: $("#search-box"),
            onSearch: search,
            initSize: 2
        }).init();

        tools.bindOnEnter("#search-box", search);

        var grid = window.grid = mini.get("datagrid");
        tools.initGrid(grid);
        grid.setUrl(request.basePath + "mqtt-client/_query");

        function search() {
            tools.searchGrid("#search-box", grid);
        }

        search();

        $(".add-button").click(function () {
            tools.openWindow("admin/rule-engine/manager/mqttclient/save.html", "新建mqtt客户端", "600", "500", function () {
                grid.reload();
            })
        });

        function changeStatus(id, status, mssagePre) {
            request.put("mqtt-client/" + id, {"status": status}, function (response) {
                console.log(response)
                if (response.status === 200) {
                    message.showTips(mssagePre + "成功");
                    grid.reload();
                } else {
                    message.showTips(mssagePre + "失败:" + response.message, "danger");
                }
            });
        }

        window.renderStatus = function (e) {
            if (e.value == 1) {
                return "启用";
            }
            return "禁用";
        }
        window.renderAction = function (e) {
            var row = e.record;
            var html = [];

            if (row.status !== 1) {
                html.push(tools.createActionButton("编辑", "icon-edit", function () {
                    tools.openWindow("admin/rule-engine/manager/mqttclient/save.html?id=" + row.id, "编辑mqtt客户端：" + row.name, "600", "500", function () {
                        grid.reload();
                    });
                }));
                html.push(tools.createActionButton("启用", "icon-key-start", function () {
                    request.post("mqtt-client/start/" + row.id, {}, function (response) {
                        if (response.status === 200) {
                            message.showTips("操作成功");
                            grid.reload();
                        } else {
                            message.showTips("操作失败:" + response.message, "danger");
                        }
                    });
                }));
                html.push(tools.createActionButton("删除", "icon-remove", function () {
                    message.confirm("确定删除客户端：" + row.name + "？删除后将无法恢复", function () {
                        var box = message.loading("删除中...");
                        request["delete"]("mqtt-client/" + row.id, function (response) {
                            box.hide();
                            if (response.status === 200) {
                                message.showTips("删除成功");
                                grid.reload();
                            } else {
                                message.showTips("删除失败:" + response.message, "danger");
                            }
                        });
                    });
                }));
            } else {
                html.push(tools.createActionButton("禁用", "icon-stop", function () {
                    request.post("mqtt-client/disable/" + row.id, {}, function (response) {
                        if (response.status === 200) {
                            message.showTips("操作成功");
                            grid.reload();
                        } else {
                            message.showTips("操作失败:" + response.message, "danger");
                        }
                    });
                }));
                html.push(tools.createActionButton("调试", "icon-bug-go", function () {
                    tools.openWindow("admin/rule-engine/manager/mqttclient/debug.html?id=" + row.id + "&name=" + row.name, "调试", "1000", "600", function () {
                        grid.reload();
                    });
                }));
                if (tag === 'select') {
                    html.push(tools.createActionButton("选择", "icon-ok", function () {
                        tools.closeWindow(row);
                    }));
                }
            }
            return html.join("");
        }

    });
});