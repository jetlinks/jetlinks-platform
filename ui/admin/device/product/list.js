importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message", "search-box"], function (request, tools, message, SearchBox) {
        new SearchBox({
            container: $("#search-box"),
            onSearch: search,
            initSize: 2
        }).init();

        tools.bindOnEnter("#search-box", search);
        var grid = window.grid = mini.get("datagrid");
        tools.initGrid(grid);
        grid.load();
        grid.setUrl(request.basePath + "device-product/_query");

        function search() {
            tools.searchGrid("#search-box", grid);
        }

        search();

        $(".add-button").click(function () {
            tools.openWindow("admin/device/product/save.html", "添加设备型号", "80%", "80%", function () {
                grid.reload();
            })
        });

        window.createTime = function(e){
            return mini.formatDate(new Date(e.value), "yyyy-MM-dd HH:mm:ss");
        };

        function productDeploy(id) {
            var loding = message.loading("发布中...");
            request.post("device-product/deploy/" + id, {}, function (response) {
                loding.close();
                if (response.result === 1){
                    message.showTips("发布成功");
                } else {
                    message.showTips("发布失败");
                }
                grid.reload();
            });
        }

        function productCancelDeploy(id) {
            var loding = message.loading("取消中...");
            request.post("device-product/deploy/" + id, {}, function (response) {
                loding.close();
                if (response.result === 1){
                    message.showTips("取消成功");
                } else {
                    message.showTips("取消失败");
                }
                grid.reload();
            });
        }

        window.renderAction = function (e) {
            var row = e.record;
            var html = [];

            html.push(tools.createActionButton("编辑", "icon-edit", function () {
                tools.openWindow("admin/device/product/save.html?id=" + row.id, "编辑设备型号：" + row.name, "80%", "80%", function () {
                    grid.reload();
                });
            }));

            if (row.state === 1){
                html.push(tools.createActionButton("取消发布", "icon-download", function () {
                    productCancelDeploy(row.id);
                }));
            } else {
                html.push(tools.createActionButton("发布", "icon-upload", function () {
                    productDeploy(row.id);
                }));
            }

            html.push(tools.createActionButton("删除", "icon-remove", function () {
                require(["message", "request"], function (message, request) {
                    message.confirm("确定删除设备型号为：" + row.name + "？删除后将无法恢复", function () {
                        var box = message.loading("删除中...");
                        request["delete"]("device-product/" + row.id, function (response) {
                            box.hide();
                            if (response.status === 200) {
                                message.showTips("删除成功");
                                grid.reload();
                            } else {
                                message.showTips("删除失败:" + response.message, "danger");
                            }
                        });
                    });
                });
            }));
            return html.join("");
        };
    });
});