importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "metadata-parser", "message"], function (request, tools, metadataParser, message) {
        var _metadata = "";
        var id = request.getParameter("id");
        request.get("device-instance/" + id, function (response) {
            var data = response.result;
            var metadata = data.deriveMetadata;
            _metadata = metadata;
            initPropertyPlate(metadata);
        });

        initDeviceRunInfo();

        function initDeviceRunInfo() {
            request.get("device-instance/run-info/" + id, function (response) {
                var data = response.result;
                $(".device-state").text(data.state.text);
                var explain = "";
                switch (data.state.value) {
                    case 'online':
                        explain = "上线时间: " + transTimeToString(data.onlineTime);
                        break;
                    case 'offline':
                        explain = "离线时间: " + transTimeToString(data.offlineTime);
                        break;
                    default:
                        explain = "设备未激活";
                }

                $(".device-online-time").text(explain);
            });
        }

        function transTimeToString(time) {
            var date = new Date(time + 8 * 3600 * 1000); // 增加8小时
            return date.toJSON().substr(0, 19).replace('T', ' ');
        }


        $(".device-info-refresh").on("click", function () {
            initDeviceRunInfo();
        });

        function structurePlate(propertyId, name, value, icon, chart) {
            return "<div style=\"background: #ffffff; margin-right: 2%; width: 23%;height: 30%; padding: 2%; margin-bottom: 2%; display: inline-block\">\n" +
                "            <div class=\"row\">\n" +
                "                <div class=\"mini-col-11\" style=\"margin-bottom: 10%\"><span class=\"info-key\">" + name + "</span></div>\n" +
                "                <div class=\"mini-col-1\" style=\"margin-bottom: 10%\"><i class=\"fa fa-refresh refresh-effect property-refresh\" title='" + propertyId + "'></i></div>\n" +
                "                <div class=\"mini-col-12\" style=\"margin-bottom: 20%\"><span class=\"info-value-big property-value\">" + value + "</span><span>" + icon + "</span><span class=\"mac\"></span></div>\n" +
                "                <div class=\"mini-col-12 property-chart\">" + chart + "</div>\n" +
                "            </div>\n" +
                "        </div>";
        }

        function loadPropertyPlate(property) {
            var unifyUnit = property.getValueType().unifyUnit;
            request.get("device-instance/" + id + "/property/" + property.id, function (response) {
                if (response.status === 200) {
                    var value = response.result.value;
                    var plate1 = structurePlate(property.id, property.name, value, unifyUnit.symbol, unifyUnit.getCharts(value));
                    $(".propertyPlate").append(plate1);
                }
            });
        }

        $(document).on('click', '.property-refresh', function () {
            var propertyId = this.title;
            var _this = this;
            request.get("device/" + id + "/property/" + propertyId, function (response) {
                if (!response.status || response.status === 200) {
                    var unifyUnit = metadataParser.getProperties(_metadata).getProperty(propertyId).getValueType().unifyUnit;
                    $(_this).parents(".row").find(".property-value").html(response[0][propertyId]);
                    $(_this).parents(".row").find(".property-chart").html(unifyUnit.getCharts(response[0][propertyId]));
                } else {
                    message.showTips(response.message);
                }
            });
        });

        function initPropertyPlate(metadata) {
            var properties = metadataParser.getProperties(metadata);
            for (var key in properties) {
                if (typeof (properties.getProperty(key)) === 'object') {
                    loadPropertyPlate(properties.getProperty(key));
                }
            }
        }
    });
});


