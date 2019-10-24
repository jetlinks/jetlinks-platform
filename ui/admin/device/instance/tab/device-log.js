importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "search-box", "miniui-tools", 'script-editor'], function (request, SearchBox, tools, editorBuilder) {
        new SearchBox({
            container: $("#search-box"),
            onSearch: search,
            initSize: 2
        }).init();
        var jsEditor;
        editorBuilder.createEditor("js-script", function (editor) {
            jsEditor = editor;
            editor.init("html",
                "//{\n//\"type\":\"readProperty\",\n" +
                "//\"properties'\":[\"memory\"]\n//}");

        });

        tools.bindOnEnter("#search-box", search);

        function search() {
            tools.searchGrid("#search-box", grid);
        }
        $(".add-strategy").click(function () {
            tools.openWindow("admin/device/instance/simulator.html?id=" + id, "模拟策略", "600", "700", function () {
                grid.reload();
            })
        });

    });
});


