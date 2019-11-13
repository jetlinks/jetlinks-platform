importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "search-box", "miniui-tools"], function (request, SearchBox, tools) {

        var id = request.getParameter("id");
        var deviceLogType = [
            {"id": "event", "name": "事件上报"},
            {"id": "readProperty", "name": "属性读取"},
            {"id": "writeProperty", "name": "属性修改"},
            {"id": "reportProperty", "name": "属性上报"},
            {"id": "call", "name": "调用"},
            {"id": "reply", "name": "回复"},
            {"id": "offline", "name": "下线"},
            {"id": "online", "name": "上线"},
            {"id": "other", "name": "其它"}
        ];
        var comboboxType = mini.get("_type");
        comboboxType.setData(deviceLogType);
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
            searchGrid("#search-box", grid, {"deviceId": id});

        }

        search();

        function searchGrid(formEL, grid, defaultParam) {
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

        function transTimeFormat(param) {
            for (var key in param) {
                if (param["createTime$GtE"]) {
                    param["createTime$GtE"] = param["createTime$GtE"].substring(0, 10) + "T" + param["createTime$GtE"].substring(11, 19);
                }
                if (param["createTime$Lt"]) {
                    param["createTime$Lt"] = param["createTime$Lt"].substring(0, 10) + "T" + param["createTime$Lt"].substring(11, 19);
                }
            }
        }

        window.renderTime = function (e) {
            var row = e.record;
            var date = new Date(row.createTime);
            return mini.formatDate(date, "yyyy-MM-dd HH:mm:ss")
        };

        window.renderAction = function (e) {
            var row = e.record;
            var html = [];

            html.push(tools.createActionLink("详细内容", "详细内容", function () {
                console.log(row)
                tools.openWindow("admin/device/instance/tab/device-log-detail.html?content="+1, "详细内容", "40%", "50%", function () {
                    grid.reload();
                },function () {
                    var iframe = this.getIFrameEl();
                    var win = iframe.contentWindow;

                    function init() {
                        win.onInit = function (call) {
                            call(row.content);
                        }
                    }
                    init();
                    $(iframe).on("load", init);
                })
            }));
            return html.join("");
        }
    });
});


