importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["miniui-tools", "request", "message", "search-box", "plugin/webuploader/webuploader.min", "storejs"],
        function (tools, request, message, SearchBox, WebUploader, storejs) {

            new SearchBox({
                container: $("#search-box"),
                onSearch: search,
                initSize: 2
            }).init();

            var grid = window.grid = mini.get("datagrid");
            tools.initGrid(grid);
            grid.setUrl(API_BASE_PATH + "rule-engine/model/_query");

            function search() {
                tools.searchGrid("#search-box", grid);
            }

            $(".search-button").click(search);
            tools.bindOnEnter("#search-box", search);
            $(".add-button").click(function () {
                tools.openWindow("admin/rule-engine/model/editor.html", "创建模型", "90%", "90%", function (e) {
                    grid.reload();
                })
            });


            search();
            grid.getColumn("action").renderer = function (e) {
                var row = e.record;
                var html = [
                    tools.createActionButton("编辑", "icon-edit", function () {
                        edit(row.id);
                    })
                ];
                html.push(
                    tools.createActionButton("发布", "icon-ok", function () {
                        message.confirm("确认发布此模型?", function () {
                            grid.loading("发布中...");
                            request['post']("rule-engine/model/" + row.id + "/_deploy", {}, function (response) {
                                grid.reload();
                                if (response.status === 200) {
                                    message.showTips("发布成功");
                                } else {
                                    message.showTips("发布失败:" + response.message);
                                }
                            });
                        })
                    })
                );
                html.push(
                    tools.createActionButton("删除", "icon-remove", function () {
                        message.confirm("确认删除?", function () {
                            grid.loading("删除中...");
                            request['delete']("rule-engine/model/" + row.id, function (response) {
                                if (response.status === 200) {
                                    grid.reload();
                                } else {
                                    message.showTips("删除失败:" + response.message);
                                }
                            });
                        })
                    })
                )
                html.push(tools.createActionButton("下载配置", "icon-download", function () {
                    download("规则模型-" + row.name + ".json", JSON.stringify(row));
                }));
                html.push(
                    tools.createActionButton("复制", "icon-page-copy", function () {
                        copy(row.id);
                    })
                )
                return html.join("");
            }

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

            function edit(id) {
                tools.openWindow("admin/rule-engine/model/editor.html?id=" + id, "编辑模型", "90%", "90%", function (e) {
                    grid.reload();
                })
            }

            function copy(id) {
                tools.openWindow("admin/rule-engine/model/editor.html?id=" + id + "&copyTag=copy", "复制模型", "90%", "90%", function (e) {
                    grid.reload();
                })
            }

            initWebUploader(WebUploader, storejs, function (file, response) {
                require(["message"], function (message) {
                    var loading = message.loading("导入中...");
                    loadConfigure(response);
                });
            });

            function loadConfigure(response) {
                if (response.status === 200 && response.result) {
                    var fileUrl = response.result;
                    require(["request", "message"], function (request, message) {
                        var loading = message.loading("导入中...");
                        request.get(fileUrl, function (res) {

                            if (res.state) delete res.state;
                            request.post("rule-engine/model", res, function (response) {
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
        pick: {
            id: '#upload-button',
            multiple: false
        },
        multiple: false,
        auto: true,
        threads: 1,
        duplicate: false,
        extensions: 'jar',
        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false
    });

    uploader.on('fileQueued', function (file) {

        uploader.upload();
    });


    uploader.on("uploadBeforeSend", function (e, param, headers) {
        var token = storejs.get("X-Access-Token");
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

    window.setTimeout(function () {
        $($(".webuploader-container").children()[1]).css("height", "32")
    }, 1000)

}
