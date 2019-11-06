importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", 'script-editor', 'metadata-trans-tree'], function (request, tools, editorBuilder, transTree) {
        var jsEditor;

        var testMetadata = "{\n" +
            "    \"properties\":[\n" +
            "        {\n" +
            "            \"id\":\"currentTemperature\",\n" +
            "            \"name\":\"当前温度\",\n" +
            "            \"expands\":{\n" +
            "                \"readonly\":true\n" +
            "            },\n" +
            "            \"valueType\":{\n" +
            "                \"type\":\"double\",\n" +
            "                \"unit\":\"celsiusDegrees\",\n" +
            "                \"max\":100,\n" +
            "                \"min\":1\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\":\"cpuUsage\",\n" +
            "            \"name\":\"cpu使用率\",\n" +
            "            \"readonly\":true,\n" +
            "            \"valueType\":{\n" +
            "                \"type\":\"double\",\n" +
            "                \"unit\":\"percent\"\n" +
            "            }\n" +
            "        }\n" +
            "    ],\n" +
            "    \"functions\":[\n" +
            "        {\n" +
            "            \"id\":\"playVoice\",\n" +
            "            \"name\":\"播放声音\",\n" +
            "            \"async\":false,\n" +
            "            \"expands\":{\n" +
            "            },\n" +
            "            \"inputs\":[\n" +
            "                {\n" +
            "                    \"id\":\"text\",\n" +
            "                    \"name\":\"文字内容\",\n" +
            "                    \"valueType\":{\n" +
            "                        \"type\":\"string\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ],\n" +
            "            \"output\":{\n" +
            "                \"id\":\"success\",\n" +
            "                \"name\":\"是否成功\",\n" +
            "                \"valueType\":{\n" +
            "                    \"type\":\"boolean\"\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    ],\n" +
            "    \"events\":[\n" +
            "        {\n" +
            "            \"id\":\"temp_sensor\",\n" +
            "            \"name\":\"温度传感器\",\n" +
            "            \"parameters\":[\n" +
            "                {\n" +
            "                    \"id\":\"temperature\",\n" +
            "                    \"name\":\"温度\",\n" +
            "                    \"valueType\":{\n" +
            "                        \"type\":\"double\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\":\"get_time\",\n" +
            "                    \"name\":\"采集时间\",\n" +
            "                    \"valueType\":{\n" +
            "                        \"type\":\"timestamp\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}";
        var treeGrid = mini.get("metadataTreeGrid");
        var treeGridData = transTree.getTreeNode(testMetadata);
        treeGrid.loadList(treeGridData, "id", "parentId");


        treeGrid.on('nodeclick', function (e) {
            var nodes = treeGrid.getCheckedNodes(true);
            treeGrid.setText(operationStrSplicing(nodes));
        });
        treeGrid.on('beforenodeselect', function (e) {
            if (e.isLeaf === false) e.cancel = true;
        });

        editorBuilder.createEditor("js-script", function (editor) {
            jsEditor = editor;
            editor.init("html",
                "//{\n//\"type\":\"readProperty\",\n" +
                "//\"properties'\":[\"memory\"]\n//}");
        });

        function operationStrSplicing(nodes){
            var tempMap = {};
            var tempText = "";
            for (let i = 0; i < nodes.length; i++) {
                tempMap[nodes[i].parentId] = nodes[i];
            }
            var tempObj = tempMap["-1"];
            for (let i = 0; i < nodes.length; i++) {
                if (i !== 0){
                    tempText += " / ";
                }
                tempText += tempObj.name;
                tempObj = tempMap[tempObj.id];
            }
            return  tempText;
        }

        $(".add-strategy").click(function () {
            tools.openWindow("admin/device/instance/tab/simulator.html?id=" + id, "模拟策略", "600", "700", function () {
                grid.reload();
            })
        });

    });
});


