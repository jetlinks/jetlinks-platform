importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools"], function (request, tools) {

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
            return e.value==1 ? "是" : "否";
        }
        function edit(id) {
            tools.openWindow("admin/user/save.html?id=" + id, "编辑用户", "550", "430", function (e) {
                grid.reload();
            })
        }

        window.renderAction = function (e) {
            var row = e.record;

            var html = [
                tools.createActionButton("编辑", "icon-edit", function () {
                    edit(row.id);
                })
            ];

            if(request.getParameter("selector")==='1'){
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
            }else{
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
