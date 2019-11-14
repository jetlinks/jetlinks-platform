importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message", "search-box"], function (request, tools, message, SearchBox) {
        if (window.onInit) {
            window.onInit(function (content) {
                var formatContent = "";
                try {
                    formatContent = JSON.stringify(JSON.parse(content), null, 2);
                } catch (e) {
                    formatContent = content;
                }
                var form = new mini.Form("#basic-info");
                form.setData({"content": formatContent});
            });
        }
    });
});
