importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "metadata-parser", "message", "text!admin/device/instance/panel/property.html", "text!admin/device/instance/panel/event.html"], function (request, tools, metadataParser, message, _propertyPanel, _eventPanel) {
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
                    metadata = data.metadata;
                    var lastPropertyValueMap = {};
                    request.get("device-instance/" + id + "/properties", function (response) {
                        if (response.status === 200) {
                            var d = response.result;
                            for (let i = 0; i < d.length; i++) {
                                lastPropertyValueMap[d[i]["property"]] = d[i]["value"];
                            }
                        }
                        initPropertyPanel(metadata, lastPropertyValueMap);
                        initEventPanel(data.metadata);
                    });
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


        var eventLevelMap = {
            "ordinary": "<i class='fa fa-circle-o' style='color: #00CC66;width: 70%'>普通</i>",
            "warn": "<i class='fa fa-exclamation' style='color: #ffb333;width: 70%'>警告</i>",
            "urgent": "<i class='fa fa-exclamation-triangle' style='color: #CC0033;width: 70%'>紧急</i>",
            "default": "<i class='fa fa-circle-o' style='color: #00CC66;width: 70%'>普通</i>"
        }

        //加载属性板块
        function loadPropertyPanel(property, lastPropertyValueMap) {
            var template = $($(_propertyPanel).html());
            var unifyUnit = property.getValueType().unifyUnit;
            var value = '--';
            if (lastPropertyValueMap[property.id] !== undefined) {
                value = lastPropertyValueMap[property.id];
            }
            template.find("i").attr("title",property.id)
            template.find(".property-name").text(property.name);
            template.find(".property-value").text(value);
            template.find(".property-icon").text(unifyUnit.symbol);
            template.find(".property-chart").append(unifyUnit.getCharts(value));
            $(".dataPanel").append(template);
        }

        //加载事件板块
        function loadEventPanel(event) {
            var eventLevel = event.level;
            if (!eventLevel) {
                eventLevel = 'default';
            }
            var template = $($(_eventPanel).html());
            template.find("a").attr("title", event.id)
            template.find("i").attr("title", event.id)
            template.find(".event-name").text(event.name);
            template.find(".event-value").text(0);
            template.find(".event-level").append(eventLevelMap[eventLevel]);
            $(".dataPanel").append(template);
            loadEventPanelData(template);
        }
        function loadEventPanelData(template) {
            var loading = message.el_loading("加载中",template[0]);
            request.get("log/device-event/" + template.find("i").attr("title") + "/productId/" + productId, request.encodeQueryParam({"deviceId.keyword": id}), function (response) {
                if (response.status === 200) {
                    template.find(".event-value").text(response.result.total);
                }else {
                    message.showTips(response.message);
                }
                loading.hide();
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
            request.get("log/device-event/" + eventId + "/productId/" + productId, request.encodeQueryParam({"deviceId.keyword": id}), function (response) {
                if (response.status === 200) {
                    var result = response.result;
                    var data = result.data;
                    if (data && data.length > 0) {
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
                        grid.setUrl(request.basePath + "log/device-event/" + eventId + "/productId/" + productId);
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
            var panel = $(this).parents(".panel");
            var loading = message.el_loading("加载中",panel[0]);
            request.get("device/" + id + "/property/" + propertyId, function (response) {
                if (!response.status || response.status === 200) {
                    var result = response.result;
                    var unifyUnit = metadataParser.getProperties(metadata).getProperty(propertyId).getValueType().unifyUnit;
                    panel.find(".property-value").html(result[0][propertyId]);
                    panel.find(".property-chart").html(unifyUnit.getCharts(result[0][propertyId]));
                } else {
                    message.showTips(response.message);
                }
                loading.hide();
            });
        });
        //事件板块刷新
        $(document).on('click', '.event-refresh', function () {
            loadEventPanelData($(this).parents(".panel"));
        });


        function initPropertyPanel(metadata, lastPropertyValueMap) {
            if (metadata === '' || metadata === undefined) {
                return;
            }
            var properties = metadataParser.getProperties(metadata);
            for (var key in properties) {
                if (typeof (properties.getProperty(key)) === 'object') {
                    loadPropertyPanel(properties.getProperty(key), lastPropertyValueMap);
                }
            }
        }

        function initEventPanel(metadata) {
            if (metadata === '' || metadata === undefined) {
                return;
            }
            var event = metadataParser.getEvents(metadata);
            for (var key in event) {
                if (typeof (event[key]) === 'object') {
                    loadEventPanel(event[key]);
                }
            }
        }
    });
});


