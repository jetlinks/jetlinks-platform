define(["jquery"], function ($) {

    function normalProperty(property) {
        var resultMap = {};
        mapDeepCopy(property["expands"], resultMap);
        mapShallowCopy(property, resultMap);
        resultMap.getValueType = function () {
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
            resultMap[property['name']] = normalProperty(property);
        }
        resultMap.getProperty = function (name) {
            return resultMap[name];
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