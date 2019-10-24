define(["jquery"], function ($) {



    function normalProperty(property) {
        var resultMap = mapDeepCopy(property["expands"]);
        resultMap["id"] = property["id"];
        resultMap["name"] = property["name"];
        resultMap.getValueType = function () {
            return property["valueType"];
        }
        return resultMap;
    }

    var mapDeepCopy = function (source) {
        var result = {};
        for (var key in source) {
            result[key] = typeof source[key] === 'object' ? mapDeepCopy(source[key]) : source[key];
        }
        return result;
    }

    return {
        getProperties: function (metadata) {
            var pts = metadata.properties;
            var resultMap = {};
            for (let i = 0; i < pts.length; i++) {
                var property = pts[i];
                resultMap[property['name']] = normalProperty(property);
            }
            resultMap.getProperty = function (name) {
                return resultMap[name];
            }
            return resultMap;
        },
        getFunctions: function(metadata){
            var funcs = metadata.functions;
            var resultMap = {};
            for (let i = 0; i < funcs.length; i++) {
                var func = funcs[i];
                resultMap[func['name']] = normalProperty(func);
            }
            resultMap.getProperty = function (name) {
                return resultMap[name];
            }
            return resultMap;
        }
    }
})

xxx(metadata).getProperty("cpu-use").getValueType().max
xxx(metadata).getProperty("cpu-use").readonly


function functions() {

}

function events() {

}