importResource("/admin/css/common.css");

require(["authorize"], function (authorize) {
    authorize.parse(document.body);
    window.authorize = authorize;
    importMiniui(function () {
        mini.parse();
        require(["miniui-tools", "request", "search-box"], function (tools, request, SearchBox) {

            new SearchBox({
                container: $("#search-box"),
                onSearch: search,
                initSize: 2
            }).init();

            window.tools = tools;

            var grid = window.grid = mini.get("data-grid");
            tools.initGrid(grid);
            grid.setDataField("result");
            grid.setUrl(API_BASE_PATH + "protocol/_query/no-paging");

            function search() {
                tools.searchGrid("#search-box", grid);
            }

            $(".search-button").click(search);
            tools.bindOnEnter("#search-box", search);
            search();

            $(".add-button").on("click", function () {
                tools.openWindow("/admin/protocol/save.html", "新增协议", "600", "450", function () {
                    grid.reload();
                });
            });
            window.renderAction = function (e) {
                var html = [];
                var row = e.record;

                html.push(tools.createActionButton("编辑", "icon-edit", function () {
                    tools.openWindow("/admin/protocol/save.html?id=" + row.id + "&type=" + row.type,
                        "编辑协议",
                        "600",
                        "450",
                        function () {
                            grid.reload();
                        });
                }));

                html.push(tools.createActionButton("删除", "icon-remove", function () {

                    require(["request", "message"], function (request, message) {
                        message.confirm("确定删除该协议?", function () {
                            var loading = message.loading("删除中...");
                            request["delete"]("protocol/" + row.id, {}, function (res) {
                                loading.close();
                                if (res.status === 200) {
                                    grid.reload();
                                    message.showTips("删除成功");
                                } else {
                                    message.showTips("删除失败:" + res.message);
                                }
                            })
                        });
                    })

                }));
                return html.join("");
            }
        });
    });
});
