importResource("/admin/css/common.css");
require(["css!pages/device/product/instance"]);

function createScopeHtml(html) {
    var labelValue = $("<div class=\"mini-col-11 form-component breadth\">");
    labelValue.append("<div class=\"form-item\">");
    labelValue.append("<label class=\"form-label\">取值范围：</label>");
    labelValue.append("<div class=\"input-block component-body \">\n" +
        "<input required name=\"min\" emptyText=\"最小值\" style=\"width: 43.5%\" class=\"mini-textbox\">~\n" +
        "<input required name=\"max\" emptyText=\"最大值\" style=\"width: 43.5%\" class=\"mini-textbox\"></div>");

    var stepValue = $("<div class=\"mini-col-11 form-component breadth\">");
    stepValue.append("<div class=\"form-item\">");
    stepValue.append("<label class=\"form-label\">步长：</label>");
    stepValue.append("<div class=\"input-block component-body \">\n" +
        "<input required name=\"step\" emptyText=\"请输入步长\" style=\"width: 90%\" class=\"mini-textbox\"></div>");

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
        createEditor: function (html) {
            createScopeHtml(html);
        }
    },
    {
        text: "float (单精度浮点型)", id: "float",
        createEditor: function (html) {
            createScopeHtml(html);
        }
    },
    {
        text: "double (双精度浮点型)", id: "double",
        createEditor: function (html) {
            createScopeHtml(html);
        }
    },
    {
        text: "enum (枚举型)", id: "enum",
        createEditor: function (html) {
            var boolValue = $("<div class=\"mini-col-11 form-component breadth\">");
            boolValue.append("<div class=\"form-item\">");
            boolValue.append("<label class=\"form-label\">枚举项：</label>");
            boolValue.append("<div class=\"input-block component-body\">\n" +
                "                <div class=\"parameter-input program\" id=\"enum-0\">\n" +
                "                     <input required name=\"value\" emptyText=\"编号如：0\" style=\"width: 43.5%\" class=\"mini-textbox\">~\n" +
                "                     <input required name=\"key\" emptyText=\"对该枚举项的描述\" style=\"width: 43.5%\" class=\"mini-textbox\">\n" +
                "                 </div><div class='add-enum'></div>\n" +
                "             <div class=\"add-parameter\"><a class=\"text-button add-attribute-enum\" href=\"javascript:void(0);\">+添加枚举项</a></div>\n" +
                "</div>");
            html.append(boolValue);
        }
    },
    {
        text: "bool (布尔型)", id: "boolean",
        createEditor: function (html) {
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
        createEditor: function (html) {
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
        createEditor: function (html) {
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
        createEditor: function (html) {
            var textValue = $("<div class=\"mini-col-11 form-component breadth\">");
            textValue.append("<div class=\"form-item\">");
            textValue.append("<label class=\"form-label\">JSON对象：</label>");
            textValue.append("<div class=\"input-block component-body \">\n" +
                "<div class=\"attribute-info\" id=\"attribute-info\"></div>\n" +
                "<div class=\"add-parameter\"><a class=\"text-button add-attribute-struct\" href=\"javascript:void(0);\">+添加参数</a></div></div>");
            html.append(textValue);
        }
    },
    {
        text: "array (数组)", id: "array",
        createEditor: function (html) {
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
            structInfoList.splice(0, structInfoList.length);
            addAttribute("");
        });

        request.get("protocol/supports", function (response) {
            if (response.status === 200) {
                var messageProtocol = mini.getByName("messageProtocol");
                messageProtocol.setData(response.result);
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
                    selected.createEditor(html);
                }
                mini.parse();
                if (selected.id === "int" || selected.id === "float" || selected.id === "double") {
                    gainUnit();
                }
                addEnum();
                $(".add-attribute-struct").click(function () {
                    addParameter("attribute-info", structInfoList, "");
                });
            });

            if (data !== "") {
                form.getField("dataType").doValueChanged();

                window.setTimeout(function () {
                    var valType = data.attributeInfoList;
                    form.setData(data);
                    form.getField("id").setReadOnly(true);
                    form.getField("dataType").setValue(valType.dataType);
                    form.getField("readOnly").setValue(valType.expands.readOnly);
                    form.getField("report").setValue(valType.expands.report);

                    if (valType.dataType === "enum") {
                        valType.valueType.elements.forEach(function (val, e) {
                            if (e === 0) {
                                new mini.Form("#enum-0").setData(val);
                            } else {
                                addEnumFrame(val);
                            }
                        })
                    } else if (valType.dataType === "object") {
                        structInfoList = [];
                        valType.valueType.properties.forEach(function (val) {
                            var parameterId = "i" + new Date().getTime();
                            structInfoList.push(val);
                            addParameterHtml("attribute-info", parameterId, structInfoList, val);
                        });
                    } else {
                        var structConf = new mini.Form("#attributeConfigCategory");
                        structConf.setData(valType.valueType);
                    }
                }, 100)
            }

            $(".attribute-save-button").unbind("click").on("click", function () {
                var attributeInfo = tools.getFormData("#attribute-info", true);
                attributeOperation();
                var valueType = {};
                if (attributeInfo.dataType === "enum") {
                    valueType["elements"] = category.getValue();
                } else if (attributeInfo.dataType === "object") {
                    valueType["properties"] = structInfoList;
                } else if (attributeInfo.dataType === "boolean") {
                    valueType["trueText"] = "是";
                    valueType["falseText"] = "否";
                    valueType["trueValue"] = "true";
                    valueType["falseValue"] = "false";
                } else {
                    var formDataInfo = tools.getFormData("#attributeConfigCategory", true);
                    $.each(formDataInfo, function (key, val) {
                        valueType[key] = val;
                    });
                }
                valueType["type"] = attributeInfo.dataType;

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
                        "valueType": valueType,
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
            addFunction();
        });

        function addFunction(data) {
            mini.get("functionEditor").show();
            //清空数组
            functionInputInfoList = [];
            functionOutputInfoList = [];

            var form = new mini.Form("#function-info");
            form.setData({"isAsync": "false"});
            $(".functions-info-input").html("");
            $(".functions-info-output").html("");

            if (data) {
                form.getField("id").setReadOnly(true);
                form.setData(data);
                if (data.outputs) {
                    data.inputs.forEach(function (val) {
                        var parameterId = "i" + new Date().getTime();
                        functionInputInfoList.push(val);
                        addParameterHtml("functions-info-input", parameterId, functionInputInfoList, val);
                    });
                }
                if (data.outputs) {
                    var parameterId = "i" + new Date().getTime();
                    functionOutputInfoList.push(data.outputs);
                    addParameterHtml("functions-info-output", parameterId, functionOutputInfoList, data.outputs);
                }
            } else {
                form.getField("id").setReadOnly(false);
            }

            $(".add-function-input").click(function () {
                addEnum();
                addParameter("functions-info-input", functionInputInfoList, "");
            });

            $(".add-function-output").click(function () {
                if (functionOutputInfoList.length === 0) {
                    addEnum();
                    addParameter("functions-info-output", functionOutputInfoList, "");
                } else {
                    message.showTips("输出参数只能存在一个,无法继续添加", "danger");
                }
            });

            $(".function-save-button").unbind("click").on("click", function () {
                var functionInfo = tools.getFormData("#function-info", true);
                functionOperation();
                var functionList = {
                    "id": functionInfo.id,
                    "name": functionInfo.name,
                    "description": functionInfo.description,
                    "functionDataList": {
                        "id": functionInfo.id,
                        "name": functionInfo.name,
                        "inputs": functionInputInfoList,
                        "outputs": functionOutputInfoList[0],
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
            eventInfoOutputList.splice(0, eventInfoOutputList.length);
            addEvent();
        });

        function addEvent(data) {
            eventInfoOutputList = [];
            mini.get("eventEditor").show();
            var form = new mini.Form("#event-info");
            form.setData({"eventType": "reportData", "level": "warn"});
            $(".event-info-output").html("");
            if (data) {
                form.getField("id").setReadOnly(true);
                form.setData(data);
                if (data.parameters) {
                    data.parameters.forEach(function (val) {
                        var parameterId = "i" + new Date().getTime();
                        eventInfoOutputList.push(val);
                        addParameterHtml("event-info-output", parameterId, eventInfoOutputList, val);
                    });
                }
            } else {
                form.getField("id").setReadOnly(false);
            }

            $(".add-event-output").click(function () {
                addEnum();
                addParameter("event-info-output", eventInfoOutputList, "");
            });

            $(".event-save-button").unbind("click").on("click", function () {
                var eventInfo = tools.getFormData("#event-info", true);
                eventOperation();
                var eventValList = {
                    "id": eventInfo.id,
                    "name": eventInfo.name,
                    "level": eventLevel[eventInfo.level],
                    "description": eventInfo.description,
                    "eventDataList": {
                        "id": eventInfo.id,
                        "name": eventInfo.name,
                        "parameters": eventInfoOutputList,
                        "level": eventInfo.level,
                        "eventType": eventInfo.eventType,
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
                productInfo.status = 0;
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

        function addEnum() {
            $(".add-attribute-enum").unbind("click").on("click", function () {
                addEnumFrame();
            });
            category.getValue = function () {
                var list = [];
                $(".program").each(function () {
                    var id = $(this).attr("id");
                    list.push(new mini.Form("#" + id).getData());
                });
                return list;
            };
        }

        function addEnumFrame(data) {
            var enumId = "e" + new Date().getTime();
            $(".add-enum").append("<div class=\"parameter-input program\" id=\"" + enumId + "\">\n" +
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
                        selected.createEditor(html);
                    }
                }
                mini.parse();
                if (selected.id === "int" || selected.id === "float" || selected.id === "double") {
                    gainUnit();
                }
                addEnum();
            });
            if (data !== "") {
                form.getField("dataType").doValueChanged();
                window.setTimeout(function () {
                    form.setData(data);
                    form.getField("id").setReadOnly(true);
                    if (data.dataType === "enum") {
                        data.valueType.elements.forEach(function (val, e) {
                            if (e === 0) {
                                new mini.Form("#enum-0").setData(val);
                            } else {
                                addEnumFrame(val);
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
                valueType["elements"] = category.getValue();
            } else if (parameterInfo.dataType === "object") {
                /*$(structInfoList).each(function () {
                    valueType["properties"] = this;
                });*/
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

            parameterInfo["valueType"] = valueType;

            var indexOf = list.some(item => {
                return item.id == parameterInfo.id;
            });
            if (indexOf) {
                list.splice(list.findIndex(item => {
                    return item.id == parameterInfo.id;
                }), 1, parameterInfo);
                var id = $("#parameter-info").attr("name");
                mini.get("#data-" + id).setValue("参数名称：" + parameterInfo.name + "(" + parameterInfo.id + ")");
                bindDelOrUp(position, id, list, parameterInfo);
            } else {
                var parameterId = "i" + new Date().getTime();
                list.push(parameterInfo);
                addParameterHtml(position, parameterId, list, parameterInfo);
            }
        }

        function addParameterHtml(position, parameterId, list, parameter) {
            $("." + position).append("<div class=\"parameter-input\" id=\"" + parameterId + "\">\n" +
                "    <input required id=\"data-" + parameterId + "\" name=\"" + parameterId + "\" readonly=\"true\" style=\"width: 72%\" class=\"mini-textbox\" value=\"参数名称：" + parameter.name + "(" + parameter.id + ")\">\n" +
                "    <a class=\"text-button\" id=\"del" + parameterId + "\" href=\"javascript:void(0);\">删除</a>\n" +
                "    <a class=\"text-button\" id=\"up" + parameterId + "\" href=\"javascript:void(0);\">编辑</a>\n" +
                "</div>\n");
            mini.parse();
            bindDelOrUp(position, parameterId, list, parameter);
        }

        function bindDelOrUp(position, parameterId, list, parameterInfo) {
            $("#del" + parameterId).unbind("click").on("click", function () {
                var subscript = list.some(item => {
                    return item.id == parameterInfo.id || item.name == parameterInfo.name;
                });
                if (subscript) {
                    var indexOf = list.findIndex(item => {
                        return item.id == parameterInfo.id || item.name == parameterInfo.name;
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
                "level": eventLevel[conf.level],
                "description": conf.description,
                "eventDataList": {
                    "id": conf.id,
                    "name": conf.name,
                    "parameters": conf.parameters,
                    "level": conf.level,
                    "eventType": conf.eventType,
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
                    addFunction(e.record.functionDataList);
                }));
                html.push(tools.createActionButton("删除", "icon-remove", function () {
                    e.sender.removeRow(e.record);
                }));
                return html.join("");
            };
            mini.get("function-list").getColumn("inputs").renderer = function (e) {
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
            };
        }

        function eventOperation() {
            mini.get("event-list").getColumn("eventList").renderer = function (e) {
                var html = [];
                html.push(tools.createActionButton("编辑", "icon-edit", function () {
                    addEvent(e.record.eventDataList);
                }));
                html.push(tools.createActionButton("删除", "icon-remove", function () {
                    e.sender.removeRow(e.record);
                }));
                return html.join("");
            };

            mini.get("event-list").getColumn("outputs").renderer = function (e) {
                var html = [];
                if (e.record.eventDataList.parameters.length > 0) {
                    html.push(tools.createActionLink("输出参数", "查看", function () {

                    }));
                }
                return html.join("");
            };
        }
    });
});