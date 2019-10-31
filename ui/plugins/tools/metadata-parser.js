define(["jquery"], function ($) {

    var valueTypeCharts = {
        "percent": function (value) {
            return "<progress value=\"" + value + "\" max=\"100\" style=\"width: 100%\"></progress>";
        },
        "_default": function (value) {
            return "<span class=\"info-key\">属性值" + value + "在正常范围内</span>";
        },
        "_discount": function () {
            return "<div class=\"info-key\" id=\"discount\"></div>";
        }
    };

    var unifyUnit = {
        "celsiusDegrees": {
            "name": "摄氏度",
            "symbol": "℃",
            "type": "temperature",
            "description": "温度单位:摄氏度(℃)",
            "getCharts": function (value) {
                var a = valueTypeCharts["_discount"]();
                require(['echarts'], function (echarts) {
                    var body = $("<div>").css("height", "20%");
                    console.log(body);
                    var c = body[0];
                    $("#discount").html("").append(body);
                    var myChart = echarts.init(c);
                    // 指定图表的配置项和数据
                    var option = {
                        tooltip: {},
                        xAxis: {
                            axisLine: {show:false},//轴线不显示
                            axisTick: {show:false},//
                            show:false,
                            data: ["00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00"]
                        },
                        yAxis: {
                            show:false,
                            axisLine: {show:false},//轴线不显示
                            axisTick: {show:false},//
                            splitLine: {
                                show: false //隐藏网格线
                            }
                        },
                        grid: {
                            top: '-1%',// 等价于 y: '16%'
                            left: '-10%',
                            right: '-10%',
                            bottom: '-13%'
                        },
                        series: [{
                            name: '温度',
                            type: 'line',
                            data: [5, 20, 40, 10, 10, 20, 5, 10, 20, 5],
                            color: "#33CCFF",//颜色样式
                            areaStyle: {},//折线下方阴影
                            symbolSize: 2,   //设定实心点的大小
                            center:["150%","50%"]
                        }]
                    };
                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);
                });
                return a;
            }
        },
        "percent": {
            "name": "百分比",
            "symbol": "%",
            "type": "common",
            "description": "百分比(%)",
            "getCharts": function (value) {
                return valueTypeCharts["percent"](value);
            }
        },
        "count": {
            "name": "次",
            "symbol": "count",
            "type": "common",
            "description": "次",
            "getCharts": function (value) {
                return valueTypeCharts["_default"](value);
            }
        },
        "_undefine": {
            "name": "",
            "symbol": "",
            "type": "",
            "description": "",
            "getCharts": function (value) {
                return valueTypeCharts["_default"](value);
            }
        }
    };

    function normalProperty(property) {
        var resultMap = {};
        mapDeepCopy(property["expands"], resultMap);
        mapShallowCopy(property, resultMap);
        resultMap.getValueType = function () {
            var unit = property["valueType"].unit;
            if (unit) {
                property["valueType"].unifyUnit = unifyUnit[unit];
            } else {
                property["valueType"].unifyUnit = unifyUnit["_undefine"];
            }
            return property["valueType"];
        };
        return resultMap;
    }

    function normalFunc(func) {
        var resultMap = {};
        mapDeepCopy(func["expands"], resultMap);
        mapShallowCopy(func, resultMap);
        resultMap.getInputs = function () {
            return propertyParser(func.inputs);
        };
        resultMap.getOutput = function () {
            return normalProperty(func.output);
        };
        return resultMap;
    }

    function normalEvent(event) {
        var resultMap = {};
        mapDeepCopy(event["expands"], resultMap);
        mapShallowCopy(event, resultMap);
        resultMap.getParameters = function () {
            return propertyParser(event.parameters);
        };
        return resultMap;
    }

    function propertyParser(properties) {
        var resultMap = {};
        if (!properties) {
            return resultMap;
        }
        for (let i = 0; i < properties.length; i++) {
            var property = properties[i];
            resultMap[property['id']] = normalProperty(property);
        }
        resultMap.getProperty = function (id) {
            return resultMap[id];
        };
        return resultMap;
    }


    //深拷贝
    var mapDeepCopy = function (source, target) {
        for (var key in source) {
            target[key] = typeof source[key] === 'object' ? mapDeepCopy(source[key], target) : source[key];
        }
        return target;
    };
    //浅拷贝
    var mapShallowCopy = function (source, target) {
        for (var key in source) {
            if (typeof source[key] !== 'object') {
                target[key] = source[key];
            }
        }
        return target;
    };

    function transObj(o) {
        if (typeof o === 'object') {
            return o;
        }
        return JSON.parse(o);
    }


    return {
        getProperties: function (metadata) {
            return propertyParser(transObj(metadata).properties);
        },
        getFunctions: function (metadata) {
            var funcs = transObj(metadata).functions;
            var resultMap = {};
            if (funcs) {
                for (let i = 0; i < funcs.length; i++) {
                    var func = funcs[i];
                    resultMap[func['name']] = normalFunc(func);
                }
                resultMap.getFunction = function (name) {
                    return resultMap[name];
                }
            }
            return resultMap;
        },
        getEvents: function (metadata) {
            var events = transObj(metadata).events;
            var resultMap = {};
            if (events) {
                for (let i = 0; i < events.length; i++) {
                    var event = events[i];
                    resultMap[event['name']] = normalEvent(event);
                }
                resultMap.getEvent = function (name) {
                    return resultMap[name];
                }
            }
            return resultMap;
        }
    }
});