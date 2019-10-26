importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "metadata-parser"], function (request, tools, metadataParser) {

        var id = request.getParameter("id");
        var deviceRunInfoJson = {
            "state": "运行中",
            "online": "2019-10-24 18:24:33"
        }
        var valueTypeIcon = {
            "celsiusDegrees": "℃",
            "percent": "%"
        }

        request.get("device-instance/" + id, function (response) {
            var data = response.result;
            var metadata = data.deriveMetadata;
            initPropertyPlate(metadata);
        });


        deviceInfo();

        function deviceInfo() {
            $(".device-online-time").text(deviceRunInfoJson.online);
            $(".device-state").text(deviceRunInfoJson.state);
        }

        $(".device-info-refresh").on("click", function () {
            var hour = randomNum(10, 23);
            var minute = randomNum(10, 59);
            var second = randomNum(10, 59);

            deviceRunInfoJson.online = "2019-10-24 " + hour + ":" + minute + ":" + second;
            deviceInfo();
        });

        function randomNum(minNum, maxNum) {
            switch (arguments.length) {
                case 1:
                    return parseInt(Math.random() * minNum + 1, 10);
                    break;
                case 2:
                    return parseInt(Math.random() * (maxNum - minNum + 1) + minNum, 10);
                    break;
                default:
                    return 0;
                    break;
            }
        }


        var valueTypeCharts = {
            "celsiusDegrees": function (value) {
                return "<span class=\"info-key\">温度" + value + "%在正常范围内</span>";
            },
            "percent": function (value) {
                return "<progress value=\"" + value + "\" max=\"100\" style=\"width: 100%\"></progress>";
            }
        }

        //
        function plate(name, value, icon, chart) {
            return "<div style=\"background: #ffffff; margin-right: 2%; width: 23%;height: 30%; padding: 2%; margin-bottom: 2%; display: inline-block\">\n" +
                "            <div class=\"row\">\n" +
                "                <div class=\"mini-col-11\" style=\"margin-bottom: 10%\"><span class=\"info-key\">" + name + "</span></div>\n" +
                "                <div class=\"mini-col-1\" style=\"margin-bottom: 10%\"><i class=\"fa fa-refresh refresh-effect\"></i></div>\n" +
                "                <div class=\"mini-col-12\" style=\"margin-bottom: 20%\"><span class=\"info-value-big\">" + value + icon + "</span><span class=\"mac\"></span></div>\n" +
                "                <div class=\"mini-col-12\">" + chart + "</div>\n" +
                "            </div>\n" +
                "        </div>";
        }

        function propertyPlate(property) {
            var unit = property.getValueType().unit;
            request.get("device/" + id + "/property/" + property.id, function (response) {
                var value = "";
                if (response) {
                    value = response[0][property.id];
                }
                var plate1 = plate(property.name, value, valueTypeIcon[unit], valueTypeCharts[unit](value));
                $(".propertyPlate").append(plate1)
            });
        }


        function initPropertyPlate(metadata) {
            var properties = metadataParser.getProperties(metadata);
            for (var key in properties) {
                if (typeof (properties.getProperty(key)) === 'object') {
                    propertyPlate(properties.getProperty(key));
                }
            }
        }


    });
});


