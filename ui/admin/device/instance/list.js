importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "search-box", "message"], function (request, tools, SearchBox, message) {
        var deviceState = [
            {"id": "online", "text": "上线"},
            {"id": "offline", "text": "离线"},
            {"id": "notActive", "text": "未激活"}
        ];
        var comboboxState = mini.get("_state");
        comboboxState.setData(deviceState);

        request.get("device-product/_query/no-paging", request.encodeQueryParam({"state": 1}), function (response) {
            var products = [];
            if (response.status === 200) {
                for (var i = 0; i < response.result.length; i++) {
                    products.push({"id": response.result[i].id, "text": response.result[i].name})
                }
                var comboboxProduct = mini.get("_productId");
                comboboxProduct.setData(products);
            }
        });

        new SearchBox({
            container: $("#search-box"),
            onSearch: search,
            initSize: 2
        }).init();

        tools.bindOnEnter("#search-box", search);

        var grid = window.grid = mini.get("datagrid");
        tools.initGrid(grid);

        grid.setUrl(request.basePath + "device-instance/_query");

        function search() {
            tools.searchGrid("#search-box", grid);
        }

        search();

        $(".add-button").click(function () {
            tools.openWindow("admin/device/instance/save.html", "新建设备实例", "700", "400", function () {
                grid.reload();
            })
        });

        function deviceDeploy(id) {
            var loding = message.loading("发布中...");
            request.post("device-instance/deploy/" + id, {}, function (response) {
                loding.close();
                if (response.result === 1){
                    message.showTips("发布成功");
                } else {
                    message.showTips("发布失败");
                }
                grid.reload();
            });
        }

        window.renderAction = function (e) {
            var row = e.record;
            var html = [];

            // if (authorize.hasPermission("home-supplier", "update")) {
            //
            // }
            html.push(tools.createActionLink("查看", "查看", function () {
                tools.openWindow("admin/device/instance/detail.html?id=" + row.id, "查看设备", "1300", "850", function () {
                    grid.reload();
                })
            }));
            if (row.state.text === '未激活') {
                html.push(tools.createActionLink("发布", "<sapn>&nbsp;&nbsp;&nbsp;发布</sapn>", function () {
                    deviceDeploy(row.id);
                }));
            }

            return html.join("");
        }

    });
});