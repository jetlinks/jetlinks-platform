importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools"], function (request, tools) {

        var dimensionId = request.getParameter("dimensionId");
        var dimensionName = request.getParameter("dimensionName");
        var dimensionTypeId = request.getParameter("dimensionTypeId");

        var grid = window.grid = mini.get("datagrid");
        tools.initGrid(grid);

        grid.setUrl(request.basePath + "user/_query");

        function search() {
            tools.searchGrid("#search-box", grid);
        }

        $(".search-button").click(search);
        tools.bindOnEnter("#search-box", search);
        $(".add-button").click(function () {
            tools.openWindow("admin/user/save.html", "添加用户", "550", "430", function (e) {
                grid.reload();
            })
        });
        search();
        window.renderStatus = function (e) {
            return e.value == 1 ? "是" : "否";
        }

        function edit(id) {
            tools.openWindow("admin/user/save.html?id=" + id, "编辑用户", "550", "430", function (e) {
                grid.reload();
            })
        }


        $(".save-button").click(function () {
            var rows = grid.getSelecteds();
            require(["message"], function (message) {
                if (rows && rows.length > 0) {
                    var data = [];
                    $(rows).each(function () {
                        var param = {};
                        param.dimensionTypeId = dimensionTypeId;
                        param.dimensionId = dimensionId;
                        param.dimensionName = dimensionName;
                        param.userId = this.id;
                        param.userName = this.name;
                       data.push(param) ;
                    });
                    console.log(data)
                    var loading = message.loading("绑定中..");
                    request.post("dimension-user/_batch", data, function (res) {
                        loading.hide();
                        if (res.status === 200) {
                            message.showTips("绑定成功");
                            tools.closeWindow(rows);
                        } else {
                            message.showTips("绑定失败:" + res.message, "danger");
                        }
                    });

                } else {
                    message.showTips("请先选择用户", "danger");
                }
            });
        });

        window.renderAction = function (e) {
            var row = e.record;

            var html = [
                tools.createActionButton("编辑", "icon-edit", function () {
                    edit(row.id);
                })
            ];

            if (request.getParameter("selector") === '1') {
                html.push(
                    tools.createActionButton("选中", "icon-ok", function () {
                        require(["message"], function (message) {
                            message.loading("绑定中..")
                            request.get("dimension-user/_query/no-paging",
                                request.encodeQueryParam({
                                    dimensionTypeId: dimensionTypeId,
                                    dimensionId: dimensionId,
                                    userId: row.id
                                }), function (res) {
                                    message.loading().hide();
                                    if (res.status === 200) {
                                        if (res.result.length > 0) {
                                            message.showTips("该维度已绑定..", "danger");
                                        } else {
                                            var param = {};
                                            param.dimensionTypeId = dimensionTypeId;
                                            param.dimensionId = dimensionId;
                                            param.dimensionName = dimensionName;
                                            param.userId = row.id;
                                            param.userName = row.username;
                                            request.post("dimension-user", param, function (r) {
                                                if (r.status === 200) {
                                                    tools.closeWindow(r);
                                                    message.showTips("绑定成功");
                                                }
                                            });
                                        }
                                    }
                                });
                        });
                    })
                );
            } else {
                html.push(
                    tools.createActionButton("用户赋权", "icon-find", function () {
                        tools.openWindow("admin/autz-settings/permission-setting.html?priority=10&merge=true&type=user" +
                            "&settingFor=" + row.id
                            + "&dimension=" + row.id
                            + "&dimensionName=" + row.name
                            + "&dimensionType=" + "user"
                            + "&dimensionTypeName=" + "用户",
                            "用户赋权-" + row.name, "800", "600", function () {
                            });
                    })
                );
            }
            return html.join("");
        }

    });
});
