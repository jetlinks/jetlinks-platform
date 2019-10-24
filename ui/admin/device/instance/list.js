importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "search-box"], function (request, tools, SearchBox) {

        new SearchBox({
            container: $("#search-box"),
            onSearch: search,
            initSize: 2
        }).init();

        tools.bindOnEnter("#search-box", search);

        var grid = window.grid = mini.get("datagrid");
        tools.initGrid(grid);

        //grid.setData([{"name1": "test1"}, {"name1": "test2"}])
        grid.load();

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

            return html.join("");
        }

    });
});