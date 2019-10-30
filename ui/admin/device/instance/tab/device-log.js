importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "search-box", "miniui-tools"], function (request, SearchBox, tools) {

        var id = request.getParameter("id");

        new SearchBox({
            container: $("#search-box"),
            onSearch: search,
            initSize: 2
        }).init();

        tools.bindOnEnter("#search-box", search);
        var grid = window.grid = mini.get("logDataGrid");
        tools.initGrid(grid);
        grid.setUrl(request.basePath + "device-operation/_query");
        function search() {
            searchGrid("#search-box", grid, undefined, {"deviceId": id});

        }
        search();
        function searchGrid(formEL, grid, defaultParam){
            var param = new mini.Form(formEL).getData(true, false);
            if (defaultParam) {
                for (var field in defaultParam) {
                    param[field] = defaultParam[field];
                }
            }
            transTimeFormat(param);
            param = request.encodeQueryParam(param);
            grid.load(param);
        }
        function transTimeFormat(param){
            for (var key in param){
                if (param["createTime$GtE"]) {
                    param["createTime$GtE"] = param["createTime$GtE"].substring(0,10) + "T" + param["createTime$GtE"].substring(11,19);
                }
                if (param["createTime$Lt"]) {
                    param["createTime$Lt"] = param["createTime$Lt"].substring(0,10) + "T" + param["createTime$Lt"].substring(11,19);
                }
            }
        }

        window.renderTime = function (e) {
            var row = e.record;
            var date = new Date(row.createTime);
            return mini.formatDate(date, "yyyy-MM-dd HH:mm:ss")
        };
    });
});


