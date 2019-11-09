importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools"], function (request, tools) {

        var grid = window.grid = mini.get("data-grid");
        tools.initGrid(grid);

        grid.setUrl(request.basePath + "sms-sender/_query/no-paging");

        grid.setDataField("result");

        function search() {
            tools.searchGrid("#search-box", grid);
        }

        $(".search-button").click(search);
        tools.bindOnEnter("#search-box", search);
        $(".add-button").click(function () {
            tools.openWindow("admin/sms/save.html", "添加发信人", "550", "430", function (e) {
                grid.reload();
            })
        });
        search();
        window.renderStatus = function (e) {
            return e.value === 1 ? "是" : "否";
        }

        function edit(id) {
            tools.openWindow("admin/sms/save.html?id=" + id, "编辑发信人", "550", "430", function (e) {
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
            if (request.getParameter("selector") === "1") {
                html.push(
                    tools.createActionButton("选中", "icon-ok", function () {
                        tools.closeWindow(row);
                    })
                );
            }
            html.push(
                tools.createActionButton("删除", "icon-remove", function () {
                    require(["message"], function (message) {
                        message.confirm("确定删除该发信人?", function () {
                            var loading = message.loading("删除中...");
                            request["delete"]("sms-sender/" + row.id, {}, function (res) {
                                loading.close();
                                if (res.status === 200) {
                                    grid.reload();
                                    message.showTips("删除成功");
                                } else {
                                    message.showTips("删除失败:" + res.message);
                                }
                            })
                        });
                    });
                })
            );
            return html.join("");
        }

    });
});
