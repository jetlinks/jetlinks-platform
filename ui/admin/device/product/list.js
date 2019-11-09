importResource("/admin/css/common.css");
require(["css!admin/device/product/instance.css"]);
importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message", "search-box", "plugin/webuploader/webuploader.min", "storejs"], function (request, tools, message, SearchBox, WebUploader, storejs) {
        new SearchBox({
            container: $("#search-box"),
            onSearch: search,
            initSize: 2
        }).init();

        request.get("protocol/supports", function (response) {
            if (response.status === 200) {
                var messageProtocol = mini.getByName("messageProtocol");
                var data = [];
                response.result.forEach(function (val) {
                    data.push({"id": val.id, "name": val.name + "(" + val.id + ")"})
                });
                messageProtocol.setData(data);
            }
        });

        var deviceType = mini.get("deviceType");
        deviceType.on("valuechanged", function () {
            search();
        });
        var transportProtocol = mini.get("transportProtocol");
        transportProtocol.on("valuechanged", function () {
            search();
        });

        var messageProtocol = mini.get("messageProtocol");
        request.get("protocol/supports",function (response) {
            if (response.status === 200) {
                var data = [];
                response.result.forEach(function (val) {
                    data.push({"id": val.id, "name": val.name + "(" + val.id + ")"})
                });
                messageProtocol.setData(data);
            }
        });
        messageProtocol.on("valuechanged", function () {
            search();
        });


        tools.bindOnEnter("#search-box", search);
        var grid = window.grid = mini.get("datagrid");
        tools.initGrid(grid);
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

        window.createTime = function (e) {
            return mini.formatDate(new Date(e.value), "yyyy-MM-dd HH:mm:ss");
        };

        initWebUploader(WebUploader, storejs, function (file, response) {
            if (response.status === 200 && response.result) {
                var fileUrl = response.result;
                require(["request", "message"], function (request, message) {
                    var loading = message.loading("导入中...");
                    request.get(fileUrl, function (res) {

                        if (res.state) delete res.state;
                        request.post("device-product", res, function (response) {
                            loading.close();
                            if (response.status === 200) {
                                grid.reload();
                                message.showTips("导入成功");
                            } else {
                                message.showTips("导入失败:" + response.message, "danger");
                            }
                        })
                    });
                });
            } else {
                require(["message"], function (message) {
                    message.showTips("服务器繁忙..");
                });
            }
        });

        function productDeploy(id) {
            var loding = message.loading("发布中...");
            request.post("device-product/deploy/" + id, {}, function (response) {
                loding.close();
                if (response.result === 1) {
                    message.showTips("发布成功");
                    grid.reload();
                } else {
                    message.showTips("发布失败", "danger");
                }
            });
        }

        function productCancelDeploy(id) {
            var loding = message.loading("重新发布中...");
            request.post("device-product/cancelDeploy/" + id, {}, function (response) {
                loding.close();
                if (response.result === 1) {
                    message.showTips("重新发布成功");
                    grid.reload();
                } else {
                    message.showTips("重新发布失败", "danger");
                }
            });
        }

        window.stateAction = function (e) {
            var html = "";
            var row = e.record;
            if (row.state === 1) {
                html = tools.createActionButton("已发布,重新发布", "fa fa-check text-success", function () {
                    productDeploy(row.id);
                });
            } else {
                html = tools.createActionButton("未发布,现在发布", "fa fa-times text-danger", function () {
                    productDeploy(row.id);
                })
            }
            return html;
        };

        window.renderAction = function (e) {
            var row = e.record;
            var html = [];

            html.push(tools.createActionButton("编辑", "icon-edit", function () {
                tools.openWindow("admin/device/product/save.html?id=" + row.id, "编辑设备型号：" + row.name, "80%", "80%", function () {
                    grid.reload();
                });
            }));

            html.push(tools.createActionButton("删除", "icon-remove", function () {
                require(["message", "request"], function (message, request) {
                    message.confirm("确定删除设备型号为：" + row.name + "？删除后将无法恢复", function () {
                        var box = message.loading("删除中...");
                        request.createQuery("device-instance/_query").where("productId", row.id).exec(function (res) {
                            if (res.status === 200) {
                                if (res.result.total === 0) {
                                    request["delete"]("device-product/" + row.id, function (response) {
                                        box.hide();
                                        if (response.status === 200) {
                                            message.showTips("删除成功");
                                            grid.reload();
                                        } else {
                                            message.showTips("删除失败:" + response.message, "danger");
                                        }
                                    });
                                } else {
                                    box.hide();
                                    message.showTips("删除失败:该型号已绑定示例,无法删除", "danger");
                                }
                            } else {
                                box.hide();
                                message.showTips("删除失败:" + res.message, "danger");
                            }
                        })
                    });
                });
            }));

            html.push(tools.createActionButton("下载配置", "icon-download", function () {
                download("设备型号-" + row.name + ".json", JSON.stringify(row));
            }));
            return html.join("");
        };

        function download(name, data) {
            // 创建隐藏的可下载链接
            var eleLink = document.createElement('a');
            eleLink.download = name;
            eleLink.style.display = 'none';
            // 字符内容转变成blob地址
            var blob = new Blob([data]);
            eleLink.href = URL.createObjectURL(blob);
            // 触发点击
            document.body.appendChild(eleLink);
            eleLink.click();
            // 然后移除
            document.body.removeChild(eleLink);
            // console.log(html[0].outerHTML);
        }

    });
});

function initWebUploader(WebUploader, storejs, responseCall) {
    var uploader = WebUploader.create({
        // swf文件路径
        swf: '../plugin/webuploader/Uploader.swf',

        // 文件接收服务端。
        server: API_BASE_PATH + 'file/static',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#upload-button',
        multiple: false,
        auto: true,
        threads: 1,
        duplicate: true,
        extensions: 'xlsx,csv',
        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false
    });

    uploader.on('fileQueued', function (file) {
        uploader.upload();
    });
    uploader.on("uploadBeforeSend", function (e, param, headers) {
        let token = storejs.get("X-Access-Token");
        if (token) {
            headers['X-Access-Token'] = token;
        }
    });

    uploader.on('uploadSuccess', function (file, response) {
        responseCall(file, response);
    });

    uploader.on('uploadError', function (file) {
    });

    uploader.on('uploadComplete', function (file) {
    });
}