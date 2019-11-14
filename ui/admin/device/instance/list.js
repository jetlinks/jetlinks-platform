importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "search-box", "message", "general-web-uploader"], function (request, tools, SearchBox, message, webUploader) {
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
            tools.openWindow("admin/device/instance/save.html", "新建设备实例", "40%", "50%", function () {
                grid.reload();
            })
        });


        webUploader.initWebUploader(function (file, response) {
            console.log(response);
            var loading = message.loading("导入中...");
            if (response.status === 200 && response.result) {
                var fileUrl = response.result;
                request.post("device-instance/import", fileUrl, function (rep) {
                    if (rep.status === 200) {
                        message.showTips("导入成功，数量：" + rep.result)
                    } else {
                        message.showTips("导入失败" + response.message, "danger")
                    }
                    loading.hide();
                    grid.reload();
                });
            }
        });

        function deploy(id) {
            var loding = message.loading("发布中...");
            request.post("device-instance/deploy/" + id, {}, function (response) {
                loding.close();
                if (response.result === 1) {
                    message.showTips("发布成功");
                } else {
                    message.showTips("发布失败：" + response.message, "danger");
                }
                grid.reload();
            });
        }

        function cancelDeploy(id) {
            var loding = message.loading("取消布中...");
            request.post("device-instance/cancelDeploy/" + id, {}, function (response) {
                loding.close();
                if (response.result === 1) {
                    message.showTips("取消发布成功");
                } else {
                    message.showTips("取消发布失败：" + response.message, "danger");
                }
                grid.reload();
            });
        }

        window.renderAction = function (e) {
            var row = e.record;
            var html = [];

            html.push(tools.createActionLink("查看", "查看", function () {
                tools.openWindow("admin/device/instance/detail.html?id=" + row.id + "&productId=" + row.productId, "查看设备", "70%", "90%", function () {
                    grid.reload();
                })
            }));
            html.push(tools.createActionLink("编辑", "<sapn>&nbsp;&nbsp;&nbsp;编辑</sapn>", function () {
                tools.openWindow("admin/device/instance/save.html?id=" + row.id, "编辑设备实例：" + row.name, "40%", "50%", function () {
                    grid.reload();
                });
            }));
            if (row.state.text === '未激活') {
                html.push(tools.createActionLink("发布", "<sapn>&nbsp;&nbsp;&nbsp;发布</sapn>", function () {
                    deploy(row.id);
                }));
            } else {
                html.push(tools.createActionLink("取消发布", "<sapn>&nbsp;&nbsp;&nbsp;取消发布</sapn>", function () {
                    cancelDeploy(row.id);
                }));
            }
            if (row.state.text === '未激活') {
                html.push(tools.createActionLink("删除", "<sapn>&nbsp;&nbsp;&nbsp;删除</sapn>", function () {
                    message.confirm("确定删除设备实例为：" + row.name + "？删除后将无法恢复", function () {
                        var box = message.loading("删除中...");
                        request["delete"]("device-instance/" + row.id, function (response) {
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
            }

            return html.join("");
        }

    });
});