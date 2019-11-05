importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "metadata-parser", "message"], function (request, tools, metadataParser, message) {
        var _metadata = "";
        var id = request.getParameter("id");
        var productId = request.getParameter("productId");

        var grid = window.grid = mini.get("eventGrid");
        var metadata = "";
        tools.initGrid(grid);
        grid.setShowFilterRow(true);
        initDeviceRunInfo(false);

        function initDeviceRunInfo(isRefresh) {
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
                if (isRefresh) {
                    message.showTips("刷新成功");
                } else {
                    // TODO: 2019-11-01 增加事件属性板块排序，每次加载按指定顺序。
                    metadata = data.metadata;
                    initPropertyPlate(data.metadata);
                    initEventPlate(data.metadata);
                }
            });
        }

        function transTimeToString(time) {
            var date = new Date(time + 8 * 3600 * 1000); // 增加8小时
            return date.toJSON().substr(0, 19).replace('T', ' ');
        }


        $(".device-info-refresh").on("click", function () {
            initDeviceRunInfo(true);
        });

        function structurePlate(propertyId, name, value, icon, chart) {
            return "<div style=\"background: #ffffff; margin-right: 2%; width: 22%;height: 30%; padding: 2%; margin-bottom: 2%; display: inline-block\">\n" +
                "            <div class=\"row\">\n" +
                "                <div class=\"mini-col-11\" style=\"margin-bottom: 10%\"><span class=\"info-key\">" + name + "</span></div>\n" +
                "                <div class=\"mini-col-1\" style=\"margin-bottom: 10%\"><i class=\"fa fa-refresh refresh-effect property-refresh\" title='" + propertyId + "'></i></div>\n" +
                "                <div class=\"mini-col-12\" style=\"margin-bottom: 20%\"><span class=\"info-value-big property-value\">" + value + "</span><span class=\"info-value-big property-value\" >" + icon + "</span><span class=\"mac\"></span></div>\n" +
                "                <div class=\"mini-col-12 property-chart\">" + chart + "</div>\n" +
                "            </div>\n" +
                "        </div>";
        }

        function eventStructurePlate(eventName, reportCount, eventId, eventLevel) {
            return "<div style=\"background: #ffffff; margin-right: 2%; width: 22%;height: 30%; padding: 2%; margin-bottom: 2%; display: inline-block\">\n" +
                "            <div class=\"row\">\n" +
                "                <div class=\"mini-col-11\" style=\"margin-bottom: 10%\"><span class=\"info-key\">" + eventName + "事件</span></div>\n" +
                "                <div class=\"mini-col-1\" style=\"margin-bottom: 10%\"><i class=\"fa fa-refresh refresh-effect event-refresh\" title='" + eventId + "'></i></div>\n" +
                "                <div class=\"mini-col-12\" style=\"margin-bottom: 20%\">上报次数:<span class=\"info-value-big event-value\">" + reportCount + "</span><span>次</span><span class=\"mac\"></span></div>\n" +
                "                <div class=\"mini-col-12 property-chart\">" + eventLevel + "<a href='#' style='width: 80%' class='event-detail' title='" + eventId + "'>查看详情</a></div>\n" +
                "            </div>\n" +
                "        </div>";
        }

        var eventLevelMap = {
            "ordinary": "<i class='fa fa-circle-o' style='color: #00CC66;width: 70%'>普通</i>",
            "warn": "<i class='fa fa-exclamation' style='color: #ffb333;width: 70%'>警告</i>",
            "urgent": "<i class='fa fa-exclamation-triangle' style='color: #CC0033;width: 70%'>紧急</i>",
            "default": "<i class='fa fa-circle-o' style='color: #00CC66;width: 70%'>普通</i>"
        }

        //加载属性板块
        function loadPropertyPlate(property) {
            var unifyUnit = property.getValueType().unifyUnit;
            request.get("device-instance/" + productId + "/property/" + property.id, function (response) {
                if (response.status === 200) {
                    var value = response.result.value;
                    if (value === '' || value === undefined) {
                        value = '--';
                    }
                    var plate1 = structurePlate(property.id, property.name, value, unifyUnit.symbol, unifyUnit.getCharts(value));
                    $(".dataPlate").append(plate1);
                }
            });
        }

        //加载事件板块
        function loadEventPlate(event) {
            request.get("device-event/" + event.id + "/productId/" + productId, request.encodeQueryParam({"deviceId.keyword": id}), function (response) {
                if (response.status === 200) {
                    var eventLevel = event.level;
                    if (!eventLevel) {
                        eventLevel = 'default';
                    }
                    var plate = eventStructurePlate(event.name, response.result.total, event.id, eventLevelMap[eventLevel]);
                    $(".dataPlate").append(plate);
                }
            });
        }


        //加载事件数据详情
        $(document).on('click', '.event-detail', function () {
            var fieldQueryTypeMap = {
                "deviceId": "string",
                "createTime": "date",
                "productId": "string",
                "value": "string"
            };
            var event = metadataParser.getEvents(metadata);
            var eventId = this.title;
            var valueType = event.getEvent(eventId).getValueType();
            var fieldMap = {
                getColumnName: function (field) {
                    var map = {"deviceId": "设备Id", "createTime": "创建时间", "productId": "型号id"};
                    if (valueType.type !== 'object') {
                        return valueType["description"];
                    } else {
                        var eventProperty = valueType.getProperties().getProperty(field);
                        var property = metadataParser.getProperties(metadata).getProperty(field);
                        if (property || eventProperty) {
                            var tempProperty = property ? property : eventProperty;
                            fieldQueryTypeMap[field] = tempProperty.getValueType().type;
                            return map[field] = tempProperty.name;
                        } else if (map[field]) {
                            return map[field];
                        }
                        return field;
                    }
                }
            };
            request.get("device-event/" + eventId + "/productId/" + productId, request.encodeQueryParam({"deviceId.keyword": id}), function (response) {
                if (response.status === 200) {
                    var result = response.result;
                    var data = result.data;
                    console.log(response)
                    if (data.length > 0) {
                        var columns = [];
                        for (var key in data[0]) {
                            columns.push({
                                "header": fieldMap.getColumnName(key),
                                "dateFormat": "yyyy-MM-dd HH:mm:ss",
                                "align": "center",
                                "field": key,
                                "filter": {
                                    type: "textbox",
                                    name: "searchCondition",
                                    id: key,
                                    "onvaluechanged": "searchGrid",
                                    showClose: true,
                                    width: "100%"
                                }
                            });
                        }
                        grid.setColumns(columns);
                        grid.setUrl(request.basePath + "device-event/" + eventId + "/productId/" + productId);
                        grid.load(request.encodeQueryParam({"deviceId.keyword": id}));
                        window.searchGrid = function (e) {
                            var names = mini.getsByName("searchCondition");
                            eventDetailSearch(names, fieldQueryTypeMap);
                        }
                        mini.get('eventDetailWindow').show();
                    } else {
                        message.showTips("暂无详细数据");
                    }
                }
            });
        });

        function eventDetailSearch(data, fieldQueryTypeMap) {

            var param = {"deviceId.keyword": id};
            for (var i = 0; i < data.length; i++) {
                if (!data[i].getValue() || data[i].getValue() === '') {
                    continue;
                }
                if (fieldQueryTypeMap[data[i].getId()] === 'string') {
                    param[data[i].getId() + ".keyword"] = data[i].getValue();
                } else if (fieldQueryTypeMap[data[i].getId()] === 'date') {
                    param[data[i].getId()] = translatorTimeFormat(data[i].getValue());
                } else {
                    param[data[i].getId()] = data[i].getValue();
                }
            }
            param = request.encodeQueryParam(param);
            grid.load(param);
        }

        function translatorTimeFormat(value) {
            return value.substring(0, 10) + "T" + value.substring(11, 19);
        }

        //属性板块刷新
        $(document).on('click', '.property-refresh', function () {
            var propertyId = this.title;
            var _this = this;
            request.get("device/" + id + "/property/" + propertyId, function (response) {
                if (!response.status || response.status === 200) {
                    var result = response.result;
                    var unifyUnit = metadataParser.getProperties(_metadata).getProperty(propertyId).getValueType().unifyUnit;
                    $(_this).parents(".row").find(".property-value").html(result[0][propertyId]);
                    $(_this).parents(".row").find(".property-chart").html(unifyUnit.getCharts(result[0][propertyId]));
                    message.showTips("刷新成功");
                } else {
                    message.showTips(response.message);
                }
            });
        });
        //事件板块刷新
        $(document).on('click', '.event-refresh', function () {
            var eventId = this.title;
            var _this = this;
            request.get("device-event/" + eventId + "/productId/" + productId, request.encodeQueryParam({"deviceId.keyword": id}), function (response) {
                if (response.status === 200) {
                    $(_this).parents(".row").find(".event-value").html(response.result.total);
                    message.showTips("刷新成功");
                } else {
                    message.showTips(response.message);
                }
            });
        });

        function initPropertyPlate(metadata) {
            if (metadata === '' || metadata === undefined) {
                return;
            }
            var properties = metadataParser.getProperties(metadata);
            for (var key in properties) {
                if (typeof (properties.getProperty(key)) === 'object') {
                    loadPropertyPlate(properties.getProperty(key));
                }
            }
        }

        function initEventPlate(metadata) {
            if (metadata === '' || metadata === undefined) {
                return;
            }
            var event = metadataParser.getEvents(metadata);
            for (var key in event) {
                if (typeof (event[key]) === 'object') {
                    loadEventPlate(event[key]);
                }
            }
        }
    });
});


