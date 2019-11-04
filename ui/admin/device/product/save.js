importResource("/admin/css/common.css");
require(["css!pages/device/product/instance"]);

function createScopeHtml(html) {
    var labelValue = $("<div class=\"mini-col-11 form-component breadth\">");
    labelValue.append("<div class=\"form-item\">");
    labelValue.append("<label class=\"form-label\">取值范围：</label>");
    labelValue.append("<div class=\"input-block component-body \">\n" +
        "<input name=\"min\" emptyText=\"最小值\" style=\"width: 43.5%\" class=\"mini-textbox\">~\n" +
        "<input name=\"max\" emptyText=\"最大值\" style=\"width: 43.5%\" class=\"mini-textbox\"></div>");

    var stepValue = $("<div class=\"mini-col-11 form-component breadth\">");
    stepValue.append("<div class=\"form-item\">");
    stepValue.append("<label class=\"form-label\">步长：</label>");
    stepValue.append("<div class=\"input-block component-body \">\n" +
        "<input name=\"step\" emptyText=\"请输入步长\" style=\"width: 90%\" class=\"mini-textbox\"></div>");

    var unitValue = $("<div class=\"mini-col-11 form-component breadth\">");
    unitValue.append("<div class=\"form-item\">");
    unitValue.append("<label class=\"form-label\">单位：</label>");
    unitValue.append("<div class=\"input-block component-body \">\n" +
        "<input name=\"unit\" id=\"unifyUnit\" showNullItem='true' emptyText=\"请选择单位\" style=\"width: 90%\" valueField=\"id\" allowInput=\"true\" textField=\"text\" class=\"mini-combobox\"></div>");
    return html.append(labelValue).append(stepValue).append(unitValue);
}

var dataValueType = {
    "int": "int32 (整数型)", "float": "float (单精度浮点型)", "double": "double (双精度浮点型)",
    "enum": "enum (枚举型)", "boolean": "bool (布尔型)", "string": "text (字符串)",
    "date": "date (时间型)", "object": "object (结构体)", "array": "array (数组)"
};
var dataType = [
    {
        text: "int32 (整数型)", id: "int",
        createEditor: function (module, html) {
            createScopeHtml(html);
        }
    },
    {
        text: "float (单精度浮点型)", id: "float",
        createEditor: function (module, html) {
            createScopeHtml(html);
        }
    },
    {
        text: "double (双精度浮点型)", id: "double",
        createEditor: function (module, html) {
            createScopeHtml(html);
        }
    },
    {
        text: "enum (枚举型)", id: "enum",
        createEditor: function (module, html) {
            var boolValue = $("<div class=\"mini-col-11 form-component breadth\">");
            boolValue.append("<div class=\"form-item\">");
            boolValue.append("<label class=\"form-label\">枚举项：</label>");
            boolValue.append("<div class=\"input-block component-body\">\n" +
                "                <div class=\"parameter-input " + module + "\" id=\"enum-0-" + module + "\">\n" +
                "                     <input required name=\"value\" emptyText=\"编号如：0\" style=\"width: 43.5%\" class=\"mini-textbox\">~\n" +
                "                     <input required name=\"key\" emptyText=\"对该枚举项的描述\" style=\"width: 43.5%\" class=\"mini-textbox\">\n" +
                "                 </div>" +
                "                 <div class=\"enum-" + module + "\"></div>\n" +
                "             <div class=\"add-parameter\"><a class=\"text-button add-enum-config-" + module + "\" href=\"javascript:void(0);\">+添加枚举项</a></div>\n" +
                "</div>");
            html.append(boolValue);
        }
    },
    {
        text: "bool (布尔型)", id: "boolean",
        createEditor: function (module, html) {
            var boolValue = $("<div class=\"mini-col-11 form-component breadth\">");
            boolValue.append("<div class=\"form-item\">");
            boolValue.append("<label class=\"form-label\">布尔值：</label>");
            boolValue.append("<div class=\"input-block component-body \">\n" +
                "true-<input required borderStyle=\"border:0\" readOnly=\"true\" value='是' style=\"width: 36%\" class=\"mini-textbox\">~\n" +
                "false-<input required borderStyle=\"border:0\" readOnly=\"true\" value='否' style=\"width: 36%\" class=\"mini-textbox\"></div>");

            html.append(boolValue);
        }
    },
    {
        text: "text (字符串)", id: "string",
        createEditor: function (module, html) {
            var textValue = $("<div class=\"mini-col-11 form-component breadth\">");
            textValue.append("<div class=\"form-item\">");
            textValue.append("<label class=\"form-label\">数据长度：</label>");
            textValue.append("<div class=\"input-block component-body \">\n" +
                "<input required name=\"length\" value='2048' style=\"width: 90%\" class=\"mini-textbox\"><span class=\"byte-span\">字节</span></div>");
            html.append(textValue);
        }
    },
    {
        text: "date (时间型)", id: "date",
        createEditor: function (module, html) {
            var textValue = $("<div class=\"mini-col-11 form-component breadth\">");
            textValue.append("<div class=\"form-item\">");
            textValue.append("<label class=\"form-label\">时间格式：</label>");
            textValue.append("<div class=\"input-block component-body \">\n" +
                "<input style=\"width: 90%;\" name=\"dateFormat\" class=\"mini-textbox\" emptyText=\"不输入默认为:String类型的UTC时间戳 (毫秒)\"/></div>");
            html.append(textValue);
        }
    },
    {
        text: "object (结构体)", id: "object",
        createEditor: function (module, html) {
            var addClass = module + "-object-struct";
            var textValue = $("<div class=\"mini-col-11 form-component breadth\">");
            textValue.append("<div class=\"form-item\">");
            textValue.append("<label class=\"form-label\">JSON对象：</label>");
            textValue.append("<div class=\"input-block component-body \">\n" +
                "<div class=\"" + addClass + "\" id=\"object-struct\"></div>\n" +
                "<div class=\"add-parameter\"><a class=\"text-button add-object-struct\" href=\"javascript:void(0);\">+添加参数</a></div></div>");
            html.append(textValue);
        }
    },
    {
        text: "array (数组)", id: "array",
        createEditor: function (module, html) {
            var textValue = $("<div class=\"mini-col-11 form-component breadth\">");
            textValue.append("<div class=\"form-item\">");
            textValue.append("<label class=\"form-label\">元素类型：</label>");
            textValue.append("<div class=\"input-block component-body \">\n" +
                "<input required name=\"elementType\" style=\"width: 90%\" value=\"1\"\n" +
                "data=\"[{text:'int32',id:'1'},{text:'float',id:'2'},{text:'double',id:'3'},{text:'text',id:'4'},{text:'struct',id:'5'}]\"\n" +
                "textField=\"text\" valueField=\"id\" class=\"mini-radiobuttonlist\"/></div>");

            var elementValue = $("<div class=\"mini-col-11 form-component breadth\">");
            elementValue.append("<div class=\"form-item\">");
            elementValue.append("<label class=\"form-label\">元素个数：</label>");
            elementValue.append("<div class=\"input-block component-body \">\n" +
                "<input required name='elementNumber' style=\"width: 90%;\" class=\"mini-textbox\" value=\"10\"/></div>");
            html.append(textValue).append(elementValue);
        }
    }];
var eventLevel = {"ordinary": "普通", "warn": "警告", "urgent": "紧急"};

importMiniui(function () {

    mini.parse();
    require(["request", "miniui-tools", "message", "search-box"], function (request, tools, message, SearchBox) {
        window.tools = tools;

        new mini.Form("#product-info").getField("id").setReadOnly(false);

        var func = request.post;
        var dataId = request.getParameter("id");
        var api = "device-product";
        if (dataId) {
            loadData(dataId);
            api += "/" + dataId;
            func = request.put;
        }

        var attributeInfoList = [];
        var structInfoList = [];
        var functionInputInfoList = [];
        var functionOutputInfoList = [];
        var functionDataList = [];
        var eventInfoOutputList = [];
        var eventDataList = [];
        var category = new mini.Form("attributeConfigCategory");

        var attributeConfig = mini.get("attributeConfig");
        attributeConfig.setButtons([{html: '<a class="button-primary add-attribute" style="background: #1890ff;color: #fff;">新建</a>'}]);
        var functionConfig = mini.get("functionConfig");
        functionConfig.setButtons([{html: '<a class="button-primary add-function" style="background: #1890ff;color: #fff;">新建</a>'}]);
        var eventConfig = mini.get("eventConfig");
        eventConfig.setButtons([{html: '<a class="button-primary add-event" style="background: #1890ff;color: #fff;">新建</a>'}]);

        $(".cancel-button").on("click", window.CloseOwnerWindow);

        $(".add-attribute").click(function () {
            structInfoList = [];
            addAttribute("");
        });

        request.get("protocol/supports", function (response) {
            if (response.status === 200) {
                var messageProtocol = mini.getByName("messageProtocol");
                var data = [];
                response.result.forEach(function (val) {
                    console.log(val);
                    data.push({"id": val.id, "name": val.name + "(" + val.id + ")"})
                });
                messageProtocol.setData(data);
            }
        });

        function addAttribute(data) {
            mini.get("attributeEditor").show();
            var form = new mini.Form("#attribute-info");
            form.setData({"readOnly": "false", "report": "true"});
            form.getField("id").setReadOnly(false);
            var html = $(".attribute-config");
            html.html("");
            if (data !== "")
                form.getField("dataType").setValue(data.attributeInfoList.dataType);

            form.getField("dataType").on("valueChanged", function (e) {
                var selected = e.selected;
                var html = $(".attribute-config");
                html.html("");
                if (selected && selected.createEditor) {
                    selected.createEditor("attribute", html);
                }
                mini.parse();
                if (selected.id === "int" || selected.id === "float" || selected.id === "double") {
                    gainUnit();
                }
                addEnum("attribute");
                $(".add-object-struct").click(function () {
                    addParameter("attribute-object-struct", structInfoList, "");
                });
            });

            if (data !== "") {
                form.getField("dataType").doValueChanged();

                window.setTimeout(function () {
                    var val = data.attributeInfoList;
                    form.setData(data);
                    form.getField("id").setReadOnly(true);
                    form.getField("dataType").setValue(val.dataType);
                    form.getField("readOnly").setValue(val.expands.readOnly);
                    form.getField("report").setValue(val.expands.report);

                    var valData;
                    if (val.dataType === "enum") {
                        valData = val.valueType.elements;
                    } else if (val.dataType === "object") {
                        valData = val.valueType.properties;
                    } else {
                        valData = val.valueType;
                    }
                    setConfigData(val.dataType, valData, "attribute", structInfoList, "attributeConfigCategory");
                }, 100)
            }

            $(".attribute-save-button").unbind("click").on("click", function () {
                var attributeInfo = tools.getFormData("#attribute-info", true);
                attributeOperation();//渲染删除和编辑方法
                var attrInfoList = {
                    "id": attributeInfo.id,
                    "name": attributeInfo.name,
                    "dataType": dataValueType[attributeInfo.dataType],
                    "readOnly": attributeInfo.readOnly === "true" ? "是" : "否",
                    "description": attributeInfo.description,
                    "attributeInfoList": {
                        "id": attributeInfo.id,
                        "name": attributeInfo.name,
                        "dataType": attributeInfo.dataType,
                        "valueType": getConfigData(attributeInfo.dataType, "attributeConfigCategory", structInfoList, "attribute"),//根据dataType和id获取变化位置的值
                        "expands": {"readOnly": attributeInfo.readOnly, "report": attributeInfo.report},
                        "description": attributeInfo.description
                    }
                };
                var indexOf = attributeInfoList.some(item => {
                    return item.id == attributeInfo.id;
                });
                if (indexOf) {
                    attributeInfoList.splice(attributeInfoList.findIndex(item => {
                        return item.id == attributeInfo.id;
                    }), 1, attrInfoList);
                } else {
                    attributeInfoList.push(attrInfoList);
                }
                mini.get("attribute-list").setData(attributeInfoList);
                mini.get("attributeEditor").hide();
            })
        }

        $(".add-function").click(function () {
            addFunction("");
        });

        function setConfigData(dataType, val, place, list, form) {
            if (dataType === "enum") {
                val.forEach(function (val, e) {
                    if (e === 0) {
                        new mini.Form("#enum-0-" + place).setData(val);
                    } else {
                        addEnumFrame(place, val);
                    }
                })
            } else if (dataType === "object") {
                list = [];
                val.forEach(function (val) {
                    var parameterId = "i" + new Date().getTime();
                    list.push(val);
                    addParameterHtml(place + "-object-struct", parameterId, list, val);
                });
            } else {
                var structConf = new mini.Form("#" + form);
                structConf.setData(val);
            }
        }

        function getConfigData(dataType, obtainId, list, place) {
            var valueType = {};
            if (dataType === "enum") {
                valueType["elements"] = category.getValue(place);
            } else if (dataType === "object") {
                valueType["properties"] = list;
            } else if (dataType === "boolean") {
                valueType["trueText"] = "是";
                valueType["falseText"] = "否";
                valueType["trueValue"] = "true";
                valueType["falseValue"] = "false";
            } else {
                var formDataInfo = tools.getFormData("#" + obtainId, true);
                $.each(formDataInfo, function (key, val) {
                    valueType[key] = val;
                });
            }
            valueType["type"] = dataType;
            return valueType;
        }

        function addFunction(data) {
            mini.get("functionEditor").show();
            //清空数组
            functionInputInfoList = [];
            functionOutputInfoList = [];

            var form = new mini.Form("#function-info");
            form.setData({"isAsync": "false"});
            $(".functions-info-input").html("");
            $(".function-output-config").html("");

            if (data !== "")
                form.getField("dataType").setValue(data.functionDataList.dataType);

            $(".add-function-input").click(function () {
                addEnum("input");
                addParameter("functions-info-input", functionInputInfoList, "");
            });

            form.getField("dataType").on("valueChanged", function (e) {
                var selected = e.selected;
                var html = $(".function-output-config");
                html.html("");
                if (selected && selected.createEditor) {
                    selected.createEditor("output", html);
                }
                mini.parse();
                if (selected.id === "int" || selected.id === "float" || selected.id === "double") {
                    gainUnit();
                }
                addEnum("output");
                $(".add-object-struct").click(function () {
                    if (functionOutputInfoList.length === 0) {
                        addParameter("output-object-struct", functionOutputInfoList, "");
                    } else {
                        message.showTips("输出参数只能存在一个,无法继续添加", "danger");
                    }
                });
            });

            if (data) {
                form.getField("dataType").doValueChanged();
                window.setTimeout(function () {
                    form.getField("id").setReadOnly(true);
                    form.setData(data);
                    var val = data.functionDataList;
                    form.getField("isAsync").setValue(val.isAsync);
                    form.getField("dataType").setValue(val.dataType);

                    if (val.inputs) {
                        val.inputs.forEach(function (val) {
                            var parameterId = "i" + new Date().getTime();
                            functionInputInfoList.push(val);
                            addParameterHtml("functions-info-input", parameterId, functionInputInfoList, val);
                        });
                    }
                    if (val.outputs) {
                        if (val.dataType === "enum") {
                            val.outputs.elements.forEach(function (i, e) {
                                if (e === 0) {
                                    new mini.Form("#enum-0-output").setData(i);
                                } else {
                                    addEnumFrame("output", i);
                                }
                            })
                        } else if (val.dataType === "object") {
                            eventInfoOutputList = [];
                            var parameterId = "i" + new Date().getTime();
                            functionOutputInfoList.push(val.outputs);
                            addParameterHtml("output-object-struct", parameterId, functionOutputInfoList, val.outputs);
                        } else {
                            var structConf = new mini.Form("#functionOutputConfig");
                            structConf.setData(val.outputs);
                        }
                    }
                }, 100);
            } else {
                form.getField("id").setReadOnly(false);
            }

            $(".function-save-button").unbind("click").on("click", function () {
                var functionInfo = tools.getFormData("#function-info", true);
                functionOperation();
                //var inputs = getConfigData(functionInfo.inputDataType, "functionInputConfig", functionInputInfoList, "input");
                var outputs = getConfigData(functionInfo.dataType, "functionOutputConfig", functionOutputInfoList, "output");
                if (functionInfo.dataType === "object") {
                    outputs = functionOutputInfoList[0];
                }
                var functionList = {
                    "id": functionInfo.id,
                    "name": functionInfo.name,
                    "description": functionInfo.description,
                    "functionDataList": {
                        "id": functionInfo.id,
                        "name": functionInfo.name,
                        "inputs": functionInputInfoList,
                        "outputs": outputs,
                        "dataType": functionInfo.dataType,
                        "isAsync": functionInfo.isAsync,
                        "description": functionInfo.description
                    }
                };
                var indexOf = functionDataList.some(item => {
                    return item.id == functionList.id;
                });
                if (indexOf) {
                    functionDataList.splice(functionDataList.findIndex(item => {
                        return item.id == functionList.id;
                    }), 1, functionList);
                } else {
                    functionDataList.push(functionList);
                }
                mini.get("function-list").setData(functionDataList);
                mini.get("functionEditor").hide();
            })
        }

        $(".add-event").click(function () {
            eventInfoOutputList = [];
            addEvent();
        });

        function addEvent(data) {
            eventInfoOutputList = [];
            mini.get("eventEditor").show();
            var form = new mini.Form("#event-info");
            form.setData({"eventType": "reportData", "level": "warn"});
            $(".event-info-output").html("");
            if (data)
                form.getField("dataType").setValue(data.eventDataList.dataType);
            form.getField("dataType").on("valueChanged", function (e) {
                var selected = e.selected;
                var html = $(".event-info-output");
                html.html("");
                if (selected && selected.createEditor) {
                    selected.createEditor("event", html);
                }
                mini.parse();
                if (selected.id === "int" || selected.id === "float" || selected.id === "double") {
                    gainUnit();
                }
                addEnum("event");
                $(".add-object-struct").click(function () {
                    addParameter("event-object-struct", eventInfoOutputList, "");
                });
            });

            if (data) {
                form.getField("dataType").doValueChanged();

                window.setTimeout(function () {
                    form.getField("id").setReadOnly(true);
                    form.setData(data);
                    var val = data.eventDataList;
                    form.getField("eventType").setValue(val.expands.eventType);
                    form.getField("level").setValue(val.expands.level);
                    form.getField("dataType").setValue(val.dataType);

                    if (val.inputs) {
                        val.inputs.forEach(function (val) {
                            var parameterId = "i" + new Date().getTime();
                            functionInputInfoList.push(val);
                            addParameterHtml("functions-info-input", parameterId, functionInputInfoList, val);
                        });
                    }
                    if (val.outputs) {
                        var valData;
                        if (val.dataType === "enum") {
                            valData = val.valueType.elements;
                        } else if (val.dataType === "object") {
                            valData = val.valueType.properties;
                        } else {
                            valData = val.valueType;
                        }
                        setConfigData(val.dataType, valData, "event", eventInfoOutputList, "eventInfoOutput");
                    }
                }, 100)
            } else {
                form.getField("id").setReadOnly(false);
            }

            $(".event-save-button").unbind("click").on("click", function () {
                var eventInfo = tools.getFormData("#event-info", true);
                eventOperation();
                var outputs = getConfigData(eventInfo.dataType, "eventInfoOutput", eventInfoOutputList, "event");
                var eventValList = {
                    "id": eventInfo.id,
                    "name": eventInfo.name,
                    "level": eventLevel[eventInfo.level],
                    "description": eventInfo.description,
                    "eventDataList": {
                        "id": eventInfo.id,
                        "name": eventInfo.name,
                        "dataType": eventInfo.dataType,
                        "valueType": outputs,
                        "expands": {"level": eventInfo.level, "eventType": eventInfo.eventType},
                        "description": eventInfo.description
                    }
                };

                var indexOf = eventDataList.some(item => {
                    return item.id == eventInfo.id;
                });
                if (indexOf) {
                    eventDataList.splice(eventDataList.findIndex(item => {
                        return item.id == eventInfo.id;
                    }), 1, eventValList);
                } else {
                    eventDataList.push(eventValList);
                }
                mini.get("event-list").setData(eventDataList);
                mini.get("eventEditor").hide();
            })
        }

        $(".save-button").click(function () {
            var attributeData = [];
            var functionData = [];
            var eventData = [];

            var productInfo = tools.getFormData("#product-info", true);

            if (dataId) {
                productInfo.id = dataId;
            } else {
                dataId = productInfo.id;
                productInfo.state = 0;
                productInfo.createTime = new Date().getTime();
            }
            if (!productInfo) {
                message.showTips("保存失败:请检查型号基本信息", "danger");
                return false;
            }
            productInfo.security = tools.getFormData("#security-info", true);

            $(mini.get("attribute-list").getData()).each(function () {
                attributeData.push(this.attributeInfoList);
            });
            $(mini.get("function-list").getData()).each(function () {
                functionData.push(this.functionDataList);
            });
            $(mini.get("event-list").getData()).each(function () {
                eventData.push(this.eventDataList);
            });
            productInfo.metadata = JSON.stringify({"properties": attributeData, "functions": functionData, "events": eventData});

            var loading = message.loading("提交中");
            func(api, productInfo, function (response) {
                loading.close();
                if (response.status === 200) {
                    message.showTips("保存成功");
                    if (!id) id = response.result.id;
                } else {
                    message.showTips("保存失败:" + response.message, "danger");
                    if (response.result)
                        tools.showFormErrors("#product-info", response.result);
                }
            });
        });

        function addEnum(place) {
            $(".add-enum-config-" + place).unbind("click").on("click", function () {
                addEnumFrame(place);
            });
            category.getValue = function (place) {
                var list = [];
                $("." + place).each(function () {
                    var id = $(this).attr("id");
                    list.push(new mini.Form("#" + id).getData());
                });
                return list;
            };
        }

        function addEnumFrame(place, data) {
            var enumId = "e" + new Date().getTime();
            $(".enum-" + place).append("<div class=\"parameter-input " + place + "\" id=\"" + enumId + "\">\n" +
                "<input required name=\"value\" emptyText=\"编号如：0\" style=\"width: 43.5%\" class=\"mini-textbox\">~\n" +
                "<input required name=\"key\" emptyText=\"对该枚举项的描述\" style=\"width: 43.5%\" class=\"mini-textbox\">\n" +
                "<a class=\"text-button\" id=\"del" + enumId + "\" href=\"javascript:void(0);\">删除</a>\n" +
                "</div>\n");
            mini.parse();
            if (data) {
                new mini.Form("#" + enumId).setData(data);
            }
            $("#del" + enumId).on("click", function () {
                $("#" + enumId).remove();
            });
        }

        function addParameter(position, list, data) {
            mini.get("structEditor").show();
            var form = new mini.Form("#parameter-info");
            form.setData("");
            var html = $(".struct-config");
            html.html("");
            mini.parse();
            if (data !== "") {
                form.getField("dataType").setValue(data.dataType);
            } else {
                form.getField("id").setReadOnly(false);
            }
            form.getField("dataType").on("valueChanged", function (e) {
                var selected = e.selected;
                var html = $(".struct-config");
                html.html("");
                if (selected && selected.createEditor) {
                    if (selected.id === "object") {
                        var boolValue = $("<div class=\"mini-col-11 form-component breadth\">");
                        boolValue.append("<div class=\"form-item\">");
                        boolValue.append("<label class=\"form-label\"></label>");
                        boolValue.append("<div class=\"input-block component-body \">\n" +
                            "<input required borderStyle=\"border:0\" readOnly=\"true\" value='暂不支持此数据类型' style=\"width: 90%\" class=\"mini-textbox\">");
                        html.append(boolValue);
                        mini.parse();
                        return false;
                    } else {
                        selected.createEditor("parameter", html);
                    }
                }
                mini.parse();
                if (selected.id === "int" || selected.id === "float" || selected.id === "double") {
                    gainUnit();
                }
                addEnum("parameter");
            });
            if (data !== "") {
                form.getField("dataType").doValueChanged();
                window.setTimeout(function () {
                    form.setData(data);
                    form.getField("id").setReadOnly(true);
                    if (data.dataType === "enum") {
                        data.valueType.elements.forEach(function (val, e) {
                            if (e === 0) {
                                new mini.Form("#enum-0-parameter").setData(val);
                            } else {
                                addEnumFrame("parameter", val);
                            }
                        })
                    } else {
                        var structConf = new mini.Form("#struct-config");
                        structConf.setData(data.valueType);
                    }
                }, 100);
            }

            $(".struct-save-button").unbind("click").on("click", function () {
                    addParameterInfo(position, list);
                }
            )
        }

        function gainUnit() {
            var unifyUnit = mini.get("unifyUnit");
            request.get("../device-product/getUnifyUnit", function (response) {
                if (response.status === 200) {
                    unifyUnit.setData(response.result);
                }
            });
        }

        function addParameterInfo(position, list) {
            var parameterInfo = tools.getFormData("#parameter-info", true);
            var valueType = {};
            if (parameterInfo.dataType === "enum") {
                valueType["elements"] = category.getValue("parameter");
            } else if (parameterInfo.dataType === "object") {
                message.showTips("暂不支持此数据类型请,重新选择数据类型", "danger");
                return false;
            } else if (parameterInfo.dataType === "boolean") {
                valueType["trueText"] = "是";
                valueType["falseText"] = "否";
                valueType["trueValue"] = "true";
                valueType["falseValue"] = "false";
            } else {
                var formDataInfo = tools.getFormData("#struct-config", true);
                $.each(formDataInfo, function (key, val) {
                    valueType[key] = val;
                });
            }
            valueType["type"] = parameterInfo.dataType;

            var parameter = {};
            parameter["id"] = parameterInfo.id;
            parameter["name"] = parameterInfo.name;
            parameter["dataType"] = parameterInfo.dataType;
            parameter["valueType"] = valueType;
            parameter["description"] = parameterInfo.description;
            var indexOf = list.some(item => {
                return item.id == parameterInfo.id;
            });
            if (indexOf) {
                list.splice(list.findIndex(item => {
                    return item.id == parameterInfo.id;
                }), 1, parameter);
                var id = $("#parameter-info").attr("name");
                mini.get("#data-" + id).setValue("参数名称：" + parameterInfo.name + "(" + parameterInfo.id + ")");
                bindDelOrUp(position, id, list, parameter);
            } else {
                var parameterId = "i" + new Date().getTime();
                list.push(parameter);
                addParameterHtml(position, parameterId, list, parameter);
            }
        }

        function addParameterHtml(position, parameterId, list, parameter) {
            var val = parameter.name + "：" + parameter.id + "(" + parameter.dataType + ")";
            $("." + position).append("<div class=\"parameter-input\" id=\"" + parameterId + "\">\n" +
                "    <input required id=\"data-" + parameterId + "\" name=\"" + parameterId + "\" readonly=\"true\" style=\"width: 72%\" class=\"mini-textbox\" value=\"" + val + "\">\n" +
                "    <a class=\"text-button\" id=\"del" + parameterId + "\" href=\"javascript:void(0);\">删除</a>\n" +
                "    <a class=\"text-button\" id=\"up" + parameterId + "\" href=\"javascript:void(0);\">编辑</a>\n" +
                "</div>\n");
            mini.parse();
            bindDelOrUp(position, parameterId, list, parameter);
        }

        function bindDelOrUp(position, parameterId, list, parameterInfo) {
            $("#del" + parameterId).unbind("click").on("click", function () {
                var subscript = list.some(item => {
                    return item.id == parameterInfo.id;
                });
                if (subscript) {
                    var indexOf = list.findIndex(item => {
                        return item.id == parameterInfo.id;
                    });
                    list.splice(indexOf, 1);
                    $("#" + parameterId).remove();
                }
            });
            $("#up" + parameterId).unbind("click").on("click", function () {
                addParameter(position, list, parameterInfo);
                $("#parameter-info").attr("name", parameterId);
            });
            mini.get("structEditor").hide();
        }

        function loadData(id) {
            request.get("device-product/" + id, function (response) {
                if (response.status === 200) {
                    var data = response.result;
                    var form = new mini.Form("#product-info");
                    form.getField("id").setReadOnly(true);
                    form.setData(data);
                    mini.getByName("deviceType").setValue(data.deviceType.value);
                    new mini.Form("#security-info").setData(data.security);
                    var metadata = JSON.parse(data.metadata);

                    $(metadata.properties).each(function () {
                        setAttributeInfo(this);
                    });
                    $(metadata.functions).each(function () {
                        setFunctionsInfo(this);
                    });
                    $(metadata.events).each(function () {
                        setEventInfo(this);
                    });
                } else {
                    message.showTips("加载失败:" + response.message, "danger");
                }
            })
        }

        function setAttributeInfo(conf) {
            attributeOperation();
            attributeInfoList.push({
                "id": conf.id,
                "name": conf.name,
                "dataType": dataValueType[conf.dataType],
                "readOnly": conf.readOnly === "true" ? "是" : "否",
                "description": conf.description,
                "attributeInfoList": {
                    "id": conf.id,
                    "name": conf.name,
                    "valueType": conf.valueType,
                    "dataType": conf.dataType,
                    "expands": conf.expands,
                    "description": conf.description
                }
            });
            mini.get("attribute-list").setData(attributeInfoList);
        }

        function setFunctionsInfo(conf) {
            functionOperation();
            functionDataList.push({
                "id": conf.id,
                "name": conf.name,
                "description": conf.description,
                "functionDataList": {
                    "id": conf.id,
                    "name": conf.name,
                    "inputs": conf.inputs,
                    "outputs": conf.outputs,
                    "dataType": conf.dataType,
                    "isAsync": conf.isAsync,
                    "description": conf.description
                }
            });
            mini.get("function-list").setData(functionDataList);
        }

        function setEventInfo(conf) {
            eventOperation();
            eventDataList.push({
                "id": conf.id,
                "name": conf.name,
                "level": eventLevel[conf.expands.level],
                "description": conf.description,
                "eventDataList": {
                    "id": conf.id,
                    "name": conf.name,
                    "valueType": conf.valueType,
                    "dataType": conf.dataType,
                    "expands": conf.expands,
                    "description": conf.description
                }
            });
            mini.get("event-list").setData(eventDataList);
        }

        function attributeOperation() {
            mini.get("attribute-list").getColumn("attributeList").renderer = function (e) {
                var html = [];
                html.push(tools.createActionButton("编辑", "icon-edit", function () {
                    addAttribute(e.record);
                }));
                html.push(tools.createActionButton("删除", "icon-remove", function () {
                    e.sender.removeRow(e.record);
                }));
                return html.join("");
            }
        }

        function functionOperation() {
            mini.get("function-list").getColumn("functionList").renderer = function (e) {
                var html = [];
                html.push(tools.createActionButton("编辑", "icon-edit", function () {
                    addFunction(e.record);
                }));
                html.push(tools.createActionButton("删除", "icon-remove", function () {
                    e.sender.removeRow(e.record);
                }));
                return html.join("");
            };
            /*mini.get("function-list").getColumn("inputs").renderer = function (e) {
                var html = [];
                if (e.record.functionDataList.inputs.length > 0) {
                    html.push(tools.createActionLink("输入参数", "查看", function () {

                    }));
                }
                return html.join("");
            };
            mini.get("function-list").getColumn("outputs").renderer = function (e) {
                var html = [];
                if (e.record.functionDataList.outputs) {
                    html.push(tools.createActionLink("输出参数", "查看", function () {

                    }));
                }
                return html.join("");
            };*/
        }

        function eventOperation() {
            mini.get("event-list").getColumn("eventList").renderer = function (e) {
                var html = [];
                html.push(tools.createActionButton("编辑", "icon-edit", function () {
                    addEvent(e.record);
                }));
                html.push(tools.createActionButton("删除", "icon-remove", function () {
                    e.sender.removeRow(e.record);
                }));
                return html.join("");
            };

            /*mini.get("event-list").getColumn("outputs").renderer = function (e) {
                var html = [];
                /!*if (e.record.eventDataList > 0) {
                    html.push(tools.createActionLink("输出参数", "查看", function () {

                    }));
                }*!/
                return html.join("");
            };*/
        }
    });
});