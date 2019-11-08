importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["miniui-tools", "request", "message", "search-box"], function (tools, request, message, SearchBox) {
        new SearchBox({
            container: $("#search-box"),
            onSearch: search,
            initSize: 2
        }).init();

        var grid = window.grid = mini.get("datagrid");
        tools.initGrid(grid);
        grid.setUrl(API_BASE_PATH + "rule-engine/instance/_query");

        function search() {
            tools.searchGrid("#search-box", grid);
        }

        $(".search-button").click(search);
        tools.bindOnEnter("#search-box", search);
        $(".add-button").click(function () {
            mini.get('add-model').show()
        });

        grid.getColumn("action").renderer = function (e) {
            var row = e.record;
            var html = [
                tools.createActionButton("查看规则", "icon-find", function (e) {
                    tools.openWindow("admin/rule-engine/instance/info.html?id=" + row.id, "查看规则", "80%", "80%", function (e) {

                    })
                })
            ];
            row.state = row.state || {value: 'stopped'};
            if (row.state.value === 'stopped') {
                html.push(tools.createActionButton("点击启动规则", "icon-ok", function () {
                    message.confirm("确认启动此规则?", function () {
                        grid.loading("启动中...");
                        request.post("rule-engine/instance/" + row.id + "/_start", {}, function (response) {
                            grid.reload();
                            if (response.status === 200) {
                                message.showTips("启动成功.")
                            } else {
                                message.showTips("启动失败:" + response.message, "danger")
                            }
                        })
                    })
                }));
            } else if (row.state.value === 'started') {
                html.push(tools.createActionButton("点击停止规则", "icon-remove", function () {
                    message.confirm("确认停止此规则?", function () {
                        grid.loading("启动中");
                        request.post("rule-engine/instance/" + row.id + "/_stop", {}, function (response) {
                            grid.reload();
                            if (response.status === 200) {
                                message.showTips("停止成功.")
                            } else {
                                message.showTips("停止失败:", response.message)
                            }
                        })
                    })
                }));
            }
            if (row.state.value !== 'started') {
                html.push(
                    tools.createActionButton("删除", "icon-remove", function () {
                        message.confirm("确认删除?", function () {
                            grid.loading("删除中...");
                            request['delete']("rule-engine/instance/" + row.id, function (response) {
                                if (response.status === 200) {
                                    grid.reload();
                                } else {
                                    message.showTips("删除失败:" + response.message);
                                }
                            });
                        })
                    })
                )
            }
            html.push(
                tools.createActionButton("生成规则模型", "icon-arrow-undo", function () {
                    request.post("rule-engine/model", JSON.stringify(row), function (response) {
                        if (response.status === 200) {
                            message.showTips("生成成功");
                        } else {
                            message.showTips("生成失败:" + response.message, "danger");
                        }
                    })
                })
            )
            return html.join("");
        };


        search();
    });


});
