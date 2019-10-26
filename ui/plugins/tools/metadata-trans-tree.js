define(["jquery"], function ($) {


    function propertyTransTreeNodeMap(parentId, property, i) {
        var result = [];
        result.push({
            "name": property.name,
            "uid": property.id,
            "id": property.id,
            "parentId": parentId
        });
        result.push({
            "name": "读取",
            "uid": "read" + i,
            "id": "read",
            "parentId": property.id
        });
        if (!property.expands || !property.expands.readonly) {
            result.push({
                "name": "修改",
                "uid": "update" + i,
                "id": "update",
                "parentId": property.id
            });
        }
        return result;
    }

    function functionTransTreeNodeMap(parentId, func) {
        var result = [];
        result.push({
            "name": func.name,
            "uid": func.id,
            "id": func.id,
            "parentId": parentId
        });
        return result;
    }

    function transObj(o) {
        if (typeof o === 'object') {
            return o;
        }
        return JSON.parse(o);
    }

    return {
        getTreeNode: function (metadata) {
            var funcs = transObj(metadata).functions;
            var pts = transObj(metadata).properties;
            var result = [
                {
                    "name":"属性",
                    "uid":"propertyType",
                    "id": "propertyType",
                    "parentId": -1
                },
                {
                    "name":"功能",
                    "uid":"functionType",
                    "id": "functionType",
                    "parentId": -1
                },
            ];
            if (funcs) {
                for (let i = 0; i < funcs.length; i++) {
                    result = result.concat(functionTransTreeNodeMap("functionType", funcs[i]))
                }
            }
            if (pts) {
                for (let i = 0; i < pts.length; i++) {
                    result = result.concat(propertyTransTreeNodeMap("propertyType", pts[i], i))
                }
            }

            return result;
        },
        "getSendJson": function () {
            return {
                "property": {
                    "id": "",
                    "operateType": "read"
                }
            }
        }
    }
});