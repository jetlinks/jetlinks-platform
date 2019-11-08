importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "search-box", "message"], function (request, tools, SearchBox, message) {
        // var deviceState = [
        //     {"id": "online", "text": "上线"},
        //     {"id": "offline", "text": "离线"},
        //     {"id": "notActive", "text": "未激活"}
        // ];
        // var comboboxState = mini.get("_state");
        // comboboxState.setData(deviceState);
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
            tools.openWindow("admin/mqtt/client/save.html", "新建mqtt客户端", "700", "400", function () {
                grid.reload();
            })
        });

        window.renderAction = function (e) {
            var row = e.record;
            var html = [];
            if (tag !== 'select') {
                html.push(tools.createActionButton("编辑", "icon-edit", function () {
                    tools.openWindow("admin/mqtt/client/save.html?id=" + row.id, "编辑mqtt客户端：" + row.name, "80%", "80%", function () {
                        grid.reload();
                    });
                }));

                //if (row.state.text === '未激活') {
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
                html.push(tools.createActionButton("选择", "icon-ok", function () {
                    tools.closeWindow(row);
                }));
            }

            return html.join("");
        }

    });
});