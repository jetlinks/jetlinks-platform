define(["jquery"], function ($) {

    var valueTypeCharts = {
        "percent": function (value) {
            return "<progress value=\"" + value + "\" max=\"100\" style=\"width: 100%\"></progress>";
        },
        "_default": function (value) {
            return "<span class=\"info-key\">属性值" + value + "在正常范围内</span>";
        }
    }

    var unifyUnit = {
        "celsiusDegrees": {
            "name": "摄氏度",
            "symbol": "℃",
            "type": "temperature",
            "description": "温度单位:摄氏度(℃)",
            "getCharts": function (value) {
                return valueTypeCharts["_default"](value+"℃");
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
    }

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
        }
        return resultMap;
    }

    function normalFunc(func) {
        var resultMap = {};
        mapDeepCopy(func["expands"], resultMap);
        mapShallowCopy(func, resultMap);
        resultMap.getInputs = function () {
            return propertyParser(func.inputs);
        }
        resultMap.getOutput = function () {
            return normalProperty(func.output);
        }
        return resultMap;
    }

    function normalEvent(event) {
        var resultMap = {};
        mapDeepCopy(event["expands"], resultMap);
        mapShallowCopy(event, resultMap);
        resultMap.getParameters = function () {
            return propertyParser(event.parameters);
        }
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
        }
        return resultMap;
    }


    //深拷贝
    var mapDeepCopy = function (source, target) {
        for (var key in source) {
            target[key] = typeof source[key] === 'object' ? mapDeepCopy(source[key], target) : source[key];
        }
        return target;
    }
    //浅拷贝
    var mapShallowCopy = function (source, target) {
        for (var key in source) {
            if (typeof source[key] !== 'object') {
                target[key] = source[key];
            }
        }
        return target;
    }

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