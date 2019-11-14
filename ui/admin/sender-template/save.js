importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message", "search-box"],
        function (request, tools, message, SearchBox) {
            var typeData = [
                {id: "sms", text: "短信"},
                {id: "email", text: "邮件"}
            ];

            var type = mini.getbyName("type");
            type.setData(typeData);

            window.tools = tools;

            var id = request.getParameter("id");

            var typeParam = request.getParameter("type");

            if (typeParam && typeParam !== "null") {
                type.setValue(typeParam);
                type.setReadOnly(true);
            }

            $("#email-subject").hide();
            $("#email-content").hide();
            type.on("valueChanged", function (e) {
                if (e.value === "sms") {
                    $("#email-subject").hide();
                    $("#email-content").hide();
                    $("#sms-content").show();
                } else if (e.value === "email") {
                    $("#sms-content").hide();
                    $("#email-subject").show();
                    $("#email-content").show();
                }
            });

            $(".save-button").on("click", (function () {
                require(["message"], function (message) {
                    var data = getDataAndValidate();

                    if (!data) return;
                    var api = "sender-template";
                    var func = request.post;
                    if (id) {
                        api += "/" + id;
                        func = request.put;
                    }
                    var loading = message.loading("提交中");
                    func(api, data, function (response) {
                        loading.close();
                        if (response.status === 200) {
                            message.showTips("保存成功");
                            if (!id) id = response.result.id;
                        } else {
                            message.showTips("保存失败:" + response.message, "danger");
                            if (response.result)
                                tools.showFormErrors("#basic-info", response.result);
                        }
                    })
                });
            }));
            require(["ueditor.config.js", "plugin/ueditor/ueditor.all.min"], function () {
                require(["plugin/ueditor/lang/zh-cn/zh-cn"], function () {
                    editor = UE.getEditor("container");
                    editor.ready(function () {
                        editor.execCommand('serverparam', ':X_Access_Token', request.getToken());
                    });

                    initEditor();
                    if (id) loadData(id);
                });
            });

        });
    window.renderAction = function (e) {
        return tools.createActionButton("删除", "icon-remove", function () {
            e.sender.removeRow(e.record);
        });
    }
});

function loadData(id) {
    require(["request", "message"], function (request, message) {
        var loading = message.loading("加载中...");
        request.get("sender-template/" + id, function (response) {
            loading.hide();
            if (response.status === 200) {
                var data = response.result;
                var form = new mini.Form("#basic-info");
                if (data.type && data.type === "email") {
                    $("#sms-content").hide();
                    $("#email-subject").show();
                    $("#email-content").show();
                    var template = JSON.parse(data.template);
                    setConfig(data.template);
                    if (template.subject) data.subject = template.subject;
                }
                form.setData(data);
            } else {
                message.showTips("加载数据失败", "danger");
            }
        });
    });
}

function getDataAndValidate() {
    var form = new mini.Form("#basic-info");
    form.validate();
    if (form.isValid() === false) {
        return;
    }
    var data = form.getData();
    var template = {};
    var editorData = JSON.parse(getConfig());
    if (editorData.html) {
        template.text = editorData.html;
    }
    if (data.subject) template.subject = data.subject;

    data.template = JSON.stringify(template);
    return data;
}


var chooseWidgets = {}; //当前已经选择列的组件格式为{id:widget}. id为键，组件对象为值。
var editor;
var nowEditId;
var changedEvents = [];


function initEditor() {
    editor.addListener('selectionchange', function () {
        var focusNode = editor.selection.getStart();
        var id = $(focusNode).attr("widget-id");
        // if(id){
        nowEditId = id;
        doConfigChange();
        //}
    });

}

var lstChangeConfig = "";

function doConfigChange() {
    var config = getConfig();
    var configJSON = JSON.stringify(config);

    if (configJSON != lstChangeConfig) {
        lstChangeConfig = configJSON;
        $(changedEvents).each(function () {
            this(config);
        });
    }
}

window.getConfig = function () {
    // 返回整个表单的元数据,json格式
    return JSON.stringify({"html": editor.getContent(), "config": chooseWidgets});
};

window.setConfig = function (text) {
    console.log(editor)
    var cfg = JSON.parse(text);
    editor.ready(function () {
        editor.setContent(cfg.text);
    });
};