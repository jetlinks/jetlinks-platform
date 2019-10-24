importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request",  "miniui-tools", 'script-editor'], function (request, tools, editorBuilder) {

        var jsEditor;
        editorBuilder.createEditor("js-script", function (editor) {
            jsEditor = editor;
            editor.init("html",
                "//{\n//\"type\":\"readProperty\",\n" +
                "//\"properties'\":[\"memory\"]\n//}");

        });



        $(".add-strategy").click(function () {
            tools.openWindow("admin/device/instance/tab/simulator.html?id=" + id, "模拟策略", "600", "700", function () {
                grid.reload();
            })
        });

    });
});


