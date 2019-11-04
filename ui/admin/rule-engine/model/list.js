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
                        request['post']("rule-engine/model/" + row.id + "/deploy", {}, function (response) {
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
            return html.join("");
        }

        function edit(id) {
            tools.openWindow("admin/rule-engine/model/editor.html?id="+id, "编辑模型", "90%", "90%", function (e) {
                grid.reload();
            })
        }

    });


});
