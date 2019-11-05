importResource("/admin/css/common.css");


importMiniui(function () {

    mini.parse();

    require(["css!pages/form/designer-drag/defaults"]);

    require(["request", "message", "miniui-tools", "search-box"], function (request, message, tools, SearchBox) {
        new SearchBox({
            container: $("#search-box-log"),
            onSearch: searchLog,
            initSize: 2
        }).init();

        new SearchBox({
            container: $("#search-box-event"),
            onSearch: searchEvent,
            initSize: 2
        }).init();

        var loading = message.loading("加载中");
        var id = request.getParameter("id");

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
        var allNodes = [];
        var nodeNameMap = {};
        var startWith = mini.getbyName("startWith");
        var endWith = mini.getbyName("endWith");
        var eventLogDetail = mini.getbyName("event-log-detail");
        var executeParam = mini.get("executeParam");

        var logGrid = window.grid = mini.get("log-grid");
        var eventGrid = window.grid = mini.get("event-grid");
        tools.initGrid(logGrid);
        tools.initGrid(eventGrid);
        logGrid.setUrl(API_BASE_PATH + "logger-execute/");
        eventGrid.setUrl(API_BASE_PATH + "logger-execute-event/");
        eventGrid.hideColumn(3);

        window.logCreateTimeRenderer = function (e) {
            return formatDate(e.record.createTime);
        }
        window.eventCreateTimeRenderer = function (e) {
            return formatDate(e.record.createTime);
        }

        window.logNodeIdRenderer = function (e) {
            return nodeNameMap[e.record.nodeId];
        }
        window.eventNodeIdRenderer = function (e) {
            return nodeNameMap[e.record.nodeId];
        }

        var nodeId;
        function searchLog() {
            if (!nodeId)  nodeId = "";
            searchGrid("#search-box-log", logGrid, {"instanceId": id,"nodeId": nodeId});
        }
        function searchEvent() {
            if (!nodeId)  {
                nodeId = "";
            };
            searchGrid("#search-box-event", eventGrid, {"instanceId": id,"nodeId": nodeId});
        }


        function searchGrid(formEL, grid, defaultParam) {
            require(["request"], function (request) {
                var param = new mini.Form(formEL).getData(true, false);
                if (defaultParam) {
                    for (var field in defaultParam) {
                        param[field] = defaultParam[field];
                    }
                }
                param = dateToTimestamp(param);
                param = request.encodeQueryParam(param);
                grid.load(param);
            });
        }
        
        var dateToTimestamp = function (param) {
            if (param["createTime$LTE"] !== null && param["createTime$LTE"] !== undefined && param["createTime$LTE"] !== ""){
                param["createTime$LTE"] = new Date(param["createTime$LTE"]).getTime();
            }
            if (param["createTime$GTE"] !== null && param["createTime$GTE"] !== undefined && param["createTime$GTE"] !== ""){
                param["createTime$GTE"] = new Date(param["createTime$GTE"]).getTime();
            }
            return param;
        }


        function formatDate(time) {
            var date = new Date(time);
            return mini.formatDate(date, "yyyy-MM-dd HH:mm:ss");
        }


        function initNodeNameMap(allNodes){
            for (var i = 0; i < allNodes.length; i++){
                nodeNameMap[allNodes[i]["id"]] = allNodes[i]["label"];
            }
        }

        function initEditor(e) {
            editor = e;
            if (id) {
                var loading = message.loading("加载中...");
                request.get("rule-engine/instance/" + id, function (e) {
                    loading.hide();
                    if (e.status === 200) {
                        ruleInfo.id = id;
                        ruleInfo.name = e.result.name;
                        ruleInfo.description = e.result.description;
                        new mini.Form("#info-panel").setData(ruleInfo);
                        if (e.result.modelType === 'antv.g6') {
                            var meta = JSON.parse(e.result.modelMeta);
                            if (meta) {
                                ruleInfo.meta = meta;
                                ruleInfo.runMode = meta.runMode;
                            }
                            allNodes = ruleInfo.meta.nodes;
                            initNodeNameMap(allNodes);
                            editor.setData(meta);
                        }
                        startWith.setData(allNodes);
                        endWith.setData(allNodes);
                    } else {
                        message.alert("加载模型失败:" + e.message);
                    }

                })
            }
            searchLog();
            searchEvent();
            // 初始化节点
            editor.onClick = function (e) {
                if (!e.item) {
                    //点击了空白
                    nodeId = undefined;
                }
            };

            editor.onAfterItemSelected = function (e) {
                var item = e.item;
                if (item.isEdge) {
                    //线
                    // console.log("选中了线", e);
                    nodeId = undefined;

                } else if (item.isNode) {
                    //节点
                    if (startWith.isFocus) {
                        startWith.setValue(item.id);
                        startWith.isFocus = false;
                    }
                    if (endWith.isFocus) {
                        endWith.setValue(item.id);
                        endWith.isFocus = false;
                    }
                    nodeId = item.id;
                    searchEvent();
                    searchLog();
                }
            }
        }

        startWith.on("focus", function () {
            startWith.isFocus = true;
        });
        endWith.on("focus", function () {
            endWith.isFocus = true;
        });

        startWith.on("blur", function () {
            startWith.isFocus = false;
        });
        endWith.on("blur", function () {
            endWith.isFocus = false;
        });


        $(".select-execute").on('click',function () {
            $(".clear-error").hide();
            mini.get('run-window').show();
            startWith.setValue(null);
            endWith.setValue(null);
            startWith.setReadOnly(false);
            executeParam.setValue(JSON.stringify({
                "data":{},
                "attributes":{}
            }))
        });

        $(".confirm-execute").on("click", function () {

            var startWithNode = startWith.getValue();
            var endWithNode = endWith.getValue();

            if (!startWithNode) {
                message.showTips("请选中启动节点");
                startWith.focus();
                startWith.isFocus = true;
                return;
            }
            if (!endWithNode) {
                message.showTips("请选中启动节点");
                endWith.focus();
                startWith.endWith = true;
                return;
            }


            var resp = mini.get("response");
            resp.setValue("执行中...");
            request.post("rule-engine/instance/" + id + "/_execute/" + startWithNode + "/" + endWithNode, executeParam.getValue(), function (response) {
                if (response.status === 200) {
                    resp.setValue(JSON.stringify(response.result));
                } else {
                    resp.setValue("失败:" + response.message);
                }
            })
        });

        designer.attr("src", "../designer/index.html#/./rule-engine?readOnly=true");
        function retry(nodeId, data){
            mini.get('run-window').show();
            startWith.setValue(nodeId);
            startWith.setReadOnly(true);
            endWith.setValue(null);
            executeParam.setValue(data);
            $(".clear-error").show();
        }

        $(".clear-error").on('click',function () {
            var data = executeParam.getValue();
            var obj = JSON.parse(data);
            delete obj.attributes["error_type"];
            delete obj.attributes["error_message"];
            delete obj.attributes["error_stack"];
            executeParam.setValue(JSON.stringify(obj));
            message.showTips("已清除");
        });
        grid.getColumn("action").renderer = function (e) {
            var row = e.record;
            var html = [
                tools.createActionButton("查看详情","icon-node",function (e) {
                    eventLogDetail.setValue(row.ruleData);
                    mini.get("event-log-detail-window").show();
                })
            ];
            if(row.event === 'NODE_EXECUTE_FAIL'){
                html.push(tools.createActionButton("重试","icon-arrow-undo",function (e) {
                    retry(row.nodeId, row.ruleData);
                }));
            }
            return html.join("");
        };
    });

});