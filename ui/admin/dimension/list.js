importResource("/admin/css/common.css");

require(["authorize"], function (authorize) {
    authorize.parse(document.body);
    window.authorize = authorize;
    importMiniui(function () {
        mini.parse();
        require(["miniui-tools", "request"], function (tools, request) {
            window.tools = tools;
            var dimensionType = request.getParameter("dimensionType");

            var grid = window.grid = mini.get("data-grid");
            tools.initGrid(grid);
            grid.setDataField("result");
            grid.setUrl(API_BASE_PATH + "dimension/_query/no-paging");

            function search() {
                if (dimensionType) {
                    tools.searchGrid("#search-box", grid, request.encodeQueryParam({typeId: dimensionType}));
                } else {
                    tools.searchGrid("#search-box", grid);
                }
            }

            $(".search-button").click(search);
            tools.bindOnEnter("#search-box", search);
            search();

            $(".save-all-button").on("click", function () {
                require(["message"], function (message) {
                    message.confirm("确认保存全部维度数据", function () {
                        grid.loading("保存中...");
                        request.patch("dimension/batch", grid.getData(), function (response) {
                            if (response.status == 200) {
                                message.showTips("保存成功");
                                grid.reload();
                            } else {
                                message.showTips("保存失败:" + response.message);
                            }
                        });
                    });
                })
            });
            window.renderAction = function (e) {
                var html = [];
                var row = e.record;
                if (authorize.hasPermission("dimension", "add")) {
                    html.push(tools.createActionButton("添加子维度", "icon-add", function () {
                        var sortIndex = row.sortIndex ? (row.sortIndex + "0" + (row.chidren ? row.chidren.length + 1 : 1)) : 1;
                        grid.addNode({sortIndex: sortIndex}, row.chidren ? row.chidren.length : 0, row);
                    }));
                }

                if (request.getParameter("selector") === '1') {
                    html.push(
                        tools.createActionButton("选中", "icon-ok", function () {
                            require(["message"], function (message) {
                                message.loading("绑定中..")
                                request.get("autz-setting/_query/no-paging",
                                    request.encodeQueryParam({
                                        permission: request.getParameter("permission"),
                                        dimensionTarget: row.id
                                    }), function (res) {
                                        message.loading().hide();
                                        if (res.status === 200) {
                                            console.log(res.result.length)
                                            if (res.result.length > 0) {
                                                message.showTips("该维度已绑定..", "danger");
                                            } else {
                                                tools.closeWindow(row);
                                            }
                                        }
                                    });
                            });
                        })
                    );
                } else {
                    html.push(
                        tools.createActionButton("维度赋权", "icon-find", function () {
                            tools.openWindow("/admin/autz-settings/permission-setting.html?priority=40&merge=true"
                                + "&settingFor=" + row.id
                                + "&dimension=" + row.id
                                + "&dimensionName=" + row.name
                                + "&dimensionType=" + row.typeId
                                + "&dimensionTypeName=" + row.typeId,//todo 维度类型加载
                                "维度赋权-" + row.name, "800", "600", function () {
                                });
                        })
                    );
                }
                if (row._state == "added" || row._state == "modified") {
                    html.push(tools.createActionButton("保存", "icon-save", function () {
                        var api = "dimension/";
                        require(["request", "message"], function (request, message) {
                            var loading = message.loading("保存中...");
                            request.patch(api, row, function (res) {
                                loading.hide();
                                if (res.status == 200) {
                                    request.get(api + res.result, function (data) {
                                        grid.updateNode(row, data.result);
                                        grid.acceptRecord(row);
                                        message.showTips("保存成功!");
                                    });
                                } else {
                                    message.showTips("保存失败:" + res.message, "danger");
                                }
                            })
                        });
                    }));
                }
                return html.join("");
            }
        });
    });
});
