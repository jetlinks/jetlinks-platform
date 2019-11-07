importResource("/admin/css/common.css");


importMiniui(function () {

    mini.parse();

    require(["css!pages/form/designer-drag/defaults"]);

    require(["request", "message", "storejs", "miniui-tools", "./nodes.js"], function (request, message, storejs, tools, nodes) {
        var items = nodes.items;
        var loading = message.loading("加载中");
        var id = request.getParameter("id");
        var copyTag = request.getParameter("copyTag");

        var ruleInfo = {runMode: "CLUSTER", "name": "新建模型"};

        var designer = $("#designer");
        var designerWindow;
        var editor;
        $(designer).on('load', function () {
            designerWindow = designer[0].contentWindow;
            if (designerWindow.editor) {
                initEditor(designerWindow.editor);
            } else {
                designerWindow.onRuleEditorInited = initEditor;
            }
            loading.hide();
        });

        var splitter = mini.get("main-splitter");

        var debugSessionId = storejs.get("ruleEngineDebugSessionId");
        var contexts = [];


        //splitter.collapsePane(2);
        var currentEditNode = "";


        function initRuleEditForm() {

            if (currentEditNode === "_rule") {
                return;
            }
            doSave();
            currentEditNode = "_rule";

            require(["text!./tpl/rule-info.html"], function (html) {
                $("#info-panel").html(html);

                mini.parse();

                if (id && !copyTag) {
                    mini.get('modelId').setEnabled(false)
                } else {
                    mini.get("modelId").focus();
                }
                ruleInfo.runMode = ruleInfo.runMode || 'CLUSTER';
                new mini.Form("#rule-info-form").setData(ruleInfo);
                var schedulingRule = ruleInfo.schedulingRule;

                doSave = function () {
                    var data = tools.getFormData("#rule-info-form", true);
                    if (data) {
                        ruleInfo = data;
                    }
                    ruleInfo.schedulingRule = schedulingRule;
                };
                onSaveSchedulingConf = function (rule) {
                    schedulingRule = rule;
                };
                loadSchedulingConf(schedulingRule);

                $(".cancel-edit")
                    .unbind("click")
                    .on("click", function () {
                        tools.closeWindow()
                    });
                $(".save-rule")
                    .unbind("click")
                    .on("click", function () {
                        doSave();
                        var meta = editor.getData();
                        if (!id && !ruleInfo.id) {
                            return;
                        }
                        meta.id = ruleInfo.id;
                        meta.name = ruleInfo.name;
                        meta.description = ruleInfo.description;
                        meta.runMode = ruleInfo.runMode;
                        meta.schedulingRule = ruleInfo.schedulingRule;
                        var model = {
                            id: meta.id,
                            name: meta.name,
                            description: meta.description,

                            modelMeta: JSON.stringify(meta),
                            modelType: "antv.g6"
                        };
                        var func = request.patch;
                        var preMessage = "保存";
                        if (copyTag) {
                            func = request.post;
                            preMessage = "复制";
                        }
                        func("rule-engine/model", model, function (e) {
                            if (e.status === 200) {
                                message.showTips(preMessage + "成功");
                                id = e.result;
                                mini.get("modelId").setEnabled(false);
                            } else {
                                message.showTips(preMessage + "失败:" + e.message, "danger")
                            }
                        });

                    })
            })
        }

        var doSave = function () {

        };

        function initLineEditForm(node) {
            var model = (node.item.model);
            if (currentEditNode === model.id) {
                return;
            }
            doSave();
            currentEditNode = model.id;
            var panel = $("#info-panel");
            panel.html("");

            require(["text!./tpl/line.html"],
                function (_html) {
                    var html = $(_html);

                    panel.html(html);

                    mini.parse();

                    $(".test-condition")
                        .unbind("click")
                        .on("click", function () {
                            doSave();
                            doInDebug(function (sessionId) {
                                mini.get('debug-param-window').show();
                                $(".confirm-debug")
                                    .unbind("click")
                                    .on("click", function () {
                                        var data = mini.get("debugParam").getValue() || '{}';
                                        mini.get('debug-param-window').hide();
                                        request.post("rule-engine/debug/" + sessionId + "/condition", {
                                            condition: model.condition,
                                            data: data ? JSON.parse(data) : {}
                                        }, function (resp) {
                                            if (resp.status !== 200) {
                                                printLog("error", resp.message)
                                            }else{
                                                printLog("info", "测试通过")
                                            }
                                        })
                                    });

                            })
                        });

                    mini.getbyName("_type")
                        .on("valueChanged", function (e) {
                            if (e.value === 'event') {
                                $(".event-comp").show();
                            } else {
                                $(".event-comp").hide()
                            }
                        });
                    var form = new mini.Form("#rule-line-form");

                    model._type = model._type || 'link';

                    form.setData(model);
                    mini.getbyName("_type").doValueChanged()

                    doSave = function () {
                        var data = form.getData();
                        model.label = data.label;
                        if (data.color) {
                            model.color = data.color;
                        }
                        model._type = data._type;
                        if (model._type === 'event') {
                            model.isEvent = true;
                        } else {
                            delete model.isEvent;
                        }
                        model.type = data.event;
                        model.description = data.description;
                        model.event = data.event;
                        model.script = data.script;
                        model.shape = data.shape;
                        if (data.script) {
                            model.condition = {
                                type: "script",
                                configuration: {
                                    lang: "js",
                                    script: data.script
                                }
                            };
                        } else {
                            delete model.condition;
                        }
                        if (editor.updateItem) {
                            editor.updateItem(node.item, model)
                        }
                        // node.modelCache=model;
                    }
                })


        }

        function initNodeEditForm(node) {

            var model = (node.item.model);
            if (currentEditNode === model.id) {
                return;
            }
            doSave();
            currentEditNode = model.id;

            var panel = $("#info-panel");
            panel.html("");
            model.executorName = model.executorName||model.label;
            require(["text!admin/rule-engine/model/nodes/" + model.executor + '.html', "text!./tpl/node-basic.html", "./nodes/" + model.executor + '.js'],
                function (_html, basic, e) {
                    var nodeTemplate = $(_html);
                    var basicTemplate = $(basic);

                    var html = $("<div class=\"mini-fit dynamic-form rule-info\">");
                    html.append(
                        "<div class=\"mini-col-12 form-component\" style=\"text-align: center\">\n" +
                        "<span class='node-title' style=\"font-size: 18px\"></span>\n" +
                        "</div>"
                    );
                    var id = "id_" + Math.round(Math.random() * 10000);
                    html.attr("id", id);
                    html.find(".node-title").text(nodeTemplate.attr("title"));
                    html.append(basicTemplate.html())
                        .append(nodeTemplate.html());


                    if (e.debugSupport) {
                        html.append(
                            "<div class=\"mini-col-12 form-component\" style=\"text-align: center\">\n" +
                            "<div style='height: 30px'><br>" +
                            "<a class='mini-button' onclick='window.doDebugNode'>运行</a>&nbsp;&nbsp;" +
                            "<span class='stop-node'><a class='mini-button' plain='true' onclick='window.doStopDebug'>停止</a></span>" +
                            "</div>\n" +
                            "</div>"
                        );
                        //正在运行中
                        if (contexts.indexOf(model.id) === -1 || !e.debugStopSupport) {
                            html.find(".stop-node").hide()
                        }

                        window.doStopDebug = function () {
                            if (e.stopDebug) {
                                e.stopDebug(function () {
                                    stopNodeDebug(model, function () {
                                        panel.find(".stop-node").hide()
                                    })
                                })
                            } else {
                                stopNodeDebug(model, function () {
                                    panel.find(".stop-node").hide()
                                })
                            }
                        };
                        window.doDebugNode = function () {
                            if (e.startDebug) {
                                e.startDebug(function (data) {
                                    doSave();
                                    startNodeDebug(model, data, function () {
                                        if (e.debugStopSupport) {
                                            panel.find(".stop-node").show()
                                        }
                                    });

                                })
                            } else {
                                doSave();
                                mini.get('debug-param-window').show();
                                $(".confirm-debug")
                                    .unbind("click")
                                    .on("click", function () {
                                        var data = mini.get("debugParam").getValue() || '{}';
                                        mini.get('debug-param-window').hide();
                                        startNodeDebug(model, data, function () {
                                            if (e.debugStopSupport) {
                                                panel.find(".stop-node").show()
                                            }
                                        });
                                    });

                            }
                        };
                    }

                    panel.html(html);


                    mini.parse();

                    onSaveSchedulingConf = function (rule) {
                        model.schedulingRule = rule;
                    };
                    loadSchedulingConf(model.schedulingRule || ruleInfo.schedulingRule);

                    var form = new mini.Form("#" + id);
                    if (!model.nodeId) {
                        model.nodeId = model.id
                    }
                    form.setData(model);

                    e.init && e.init(panel, model);

                    doSave = function () {
                        var data = form.getData();
                        model.label = data.label;
                        model.color = data.color;
                        model.config = model.config || {};
                        for (var i in data.config) {
                            model.config[i] = data.config[i];
                        }

                        model.size = data.size;
                        e.onSave && e.onSave(panel, model, data);

                        editor.updateItem(node.item, model)
                        // node.modelCache=model;
                    }
                })

        }

        function stopNodeDebug(nodeModel, call) {
            printLog("info", "停止执行节点:", nodeModel.label);

            doInDebug(function (debugId) {
                request['delete']("rule-engine/debug/" + debugId + "/" + nodeModel.id, function (res) {

                    if (res.status !== 200) {
                        printLog("error", "停止失败", res.message);
                    } else {
                        if (call) {
                            call();
                        }
                    }

                })
            });

        }

        function startNodeDebug(nodeModel, data, call) {
            doInDebug(function (debugId) {
                printLog("info", "执行节点\t:\t", nodeModel.label, data || '', "...");
                request.post("rule-engine/debug/" + debugId, {
                    id: nodeModel.nodeId,
                    nodeId: nodeModel.nodeId,
                    executor: nodeModel.executor,
                    configuration: nodeModel.config
                }, function (startResponse) {
                    window.setTimeout(function () {
                        if (startResponse.status === 200) {
                            contexts.push(startResponse.result);
                            request.post("rule-engine/debug/" + debugId + "/" + startResponse.result, data, function (response) {
                                if (response.status !== 200) {
                                    printLog("error", response.message)
                                } else {
                                    if (call) {
                                        call();
                                    }
                                }
                            })
                        } else {
                            printLog("error", startResponse.message)
                        }
                    }, 500); //延迟500ms,在集群时,可能存在节点配置同步不及时的问题

                })
            });
        }

        var timeout;

        var polling = false;

        function timingPollLog() {
            if (polling) {
                return;
            }
            polling = true;
            var es = new EventSource(window.API_BASE_PATH + "rule-engine/debug/" + debugSessionId + "/logs/?:X_Access_Token="+request.getToken());
            es.onmessage = function (ev) {
                var log = JSON.parse(ev.data);
                if (log.type === 'log') {
                    printLog(log.message.level, log.type, "\t:\t", log.message.message);
                } else {
                    printLog("info", log.type, "\t:\t", log.message)
                }
            };

            es.onerror = function (ev) {
                console.error(ev)
               // printLog("error", "error", "\t:\t", ev.message);
                closeSession();
                es.close();
                polling=false;
            }
        }

        var len = 0;

        window.closeDebugSession = closeSession;

        function closeSession() {

            if (debugSessionId) {
                window.clearTimeout(timeout);
                contexts = [];
                request['delete']("rule-engine/debug/" + debugSessionId, function (response) {
                    if (response.status === 200) {
                        printLog("info", "关闭会话:", debugSessionId);
                    } else {
                        printLog("error", "关闭会话失败:", response.message)
                    }
                    debugSessionId = null;
                    storejs.remove('ruleEngineDebugSessionId');
                });
            }
        }

        function printLog() {
            var args = [].slice.call(arguments);
            var level = args[0];

            var message = args.slice(1)
                .map(function (e) {
                    if (typeof e === "object") {
                        try {
                            e = JSON.stringify(e);
                        } catch (e) {

                        }
                    }
                    return e;
                }).join(" ");
            var panel = $("#log-panel");
            if (len++ > 1000) {
                panel.children().first().remove();
            }
            panel
                .append($("<pre class='logger'>&gt; ").addClass("logger-" + level).text(message))
                .scrollTop(999999)

        }

        if (debugSessionId) {
            printLog("info", "当前会话:", debugSessionId);
            request.get("rule-engine/debug/" + debugSessionId + "/contexts", function (response) {
                if (response.status === 200) {
                    contexts = response.result;
                }
            });
            timingPollLog();
        }

        $("#log-panel").bind("contextmenu", function (e) {
            var menu = mini.get("contextMenu");
            menu.showAtPos(e.pageX, e.pageY);
            return false;
        });

        function doInDebug(call) {
            if (debugSessionId) {
                call(debugSessionId);
            } else {
                request.post("rule-engine/debug", {}, function (response) {
                    if (response.status === 200) {
                        printLog("info", "开启新会话:", debugSessionId = response.result);
                        storejs.set("ruleEngineDebugSessionId", response.result);
                        call(response.result);
                        timingPollLog();
                    } else {
                        message.showTips("开启debug失败");
                    }
                })
            }
        }

        function initEditor(e) {

            editor = e;

            editor.setItemData && editor.setItemData(items);
            if (id) {
                var loading = message.loading("加载中...");
                request.get("rule-engine/model/" + id, function (e) {
                    loading.hide();
                    if (e.status === 200) {
                        ruleInfo.id = id;
                        ruleInfo.name = e.result.name;
                        ruleInfo.description = e.result.description;

                        if (e.result.modelType === 'antv.g6') {
                            var meta = JSON.parse(e.result.modelMeta);
                            if (meta) {
                                ruleInfo.meta = meta;
                                ruleInfo.runMode = meta.runMode;
                            }
                            ruleInfo.schedulingRule = meta.schedulingRule;
                            editor.setData(meta);
                        }
                        initRuleEditForm()
                    } else {
                        message.alert("加载模型失败:" + e.message);
                    }

                })
            } else {
                initRuleEditForm();

            }
            // 初始化节点
            editor.onClick = function (e) {
                if (!e.item) {
                    //点击了空白
                    initRuleEditForm();
                }
            };
            editor.onAfterChange = function (e) {
                if (e.action === 'add') {
                    // console.log('新增了')
                }
            };

            editor.onAfterItemSelected = function (e) {
                var item = e.item;
                if (item.isEdge) {
                    //线
                    // console.log("选中了线", e);
                    initLineEditForm(e);
                } else if (item.isNode) {
                    //节点
                    initNodeEditForm(e)
                }
            }
        }

        var fixedWorker = mini.get('fixed-worker');
        var tagMatch = mini.get('tag-match');
        var schedulingGrid = mini.get('scheduling-grid');

        request.get("rule-engine/workers", function (response) {
            if (response.status === 200) {
                mini.get('fixed-worker-conf').setData(response.result);
            }
        });
        schedulingGrid.getColumn("action").renderer = function (e) {
            return tools.createActionButton("删除", "icon-remove", function () {
                e.sender.removeRow(e.record);
            })
        };

        var getSchedulingConf = function () {
            return null;
        };
        var onSaveSchedulingConf = function (cfg) {
        };
        var loadSchedulingConf = function (cfg) {
            if (!cfg) {
                cfg = {};
            }
            $(".scheduling-conf").hide();
            mini.get('fixed-worker-conf').setValue();
            schedulingGrid.setData([]);
            tagMatch.deselectAll();
            fixedWorker.deselectAll();
            if (cfg.type === 'fixed-worker') {
                mini.get('fixed-worker-conf').setValue(cfg.configuration.workers);
                fixedWorker.select(0);
                fixedWorker.doValueChanged();
            } else if (cfg.type === 'tag-mach') {
                schedulingGrid.setData(cfg.configuration.rules);
                tagMatch.select(0);
                tagMatch.doValueChanged();
            }
        };

        $(".save-scheduling").on('click', function () {
            onSaveSchedulingConf(getSchedulingConf());
            mini.get("scheduling-window").hide();
        });

        fixedWorker.on("valuechanged", function (e) {
            tagMatch.deselectAll();
            $(".scheduling-conf").hide();
            $(".fixed-worker").show();
            mini.parse();
            getSchedulingConf = function () {
                return {
                    type: "fixed-worker",
                    configuration: {
                        workers: mini.get('fixed-worker-conf').getValue()
                    }
                }
            }
        });
        tagMatch.on("valuechanged", function (e) {
            fixedWorker.deselectAll();
            $(".scheduling-conf").hide();
            $(".tag-match").show();
            mini.parse();
            getSchedulingConf = function () {
                return {
                    type: "tag-mach",
                    configuration: {
                        rules: schedulingGrid.getData()
                    }
                }
            }
        });

        designer.attr("src", "../designer/index.html#/./rule-engine");


    });

});


window._scheduling_operator = [
    {
        id: "="
    },
    {
        id: "!="
    },
    {
        id: "*?*"
    }
];