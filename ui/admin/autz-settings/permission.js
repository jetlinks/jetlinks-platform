importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();

    var dimensionTypeMap = {
        "user": {uri: "admin/user/list.html?selector=1", typeName: "用户", relationApi: "dimension-user"}
    };

    require(["request", "message", "miniui-tools"], function (request, message, tools) {
        var permissionId = request.getParameter("id");
        if (!permissionId) {
            message.showTips("加载权限失败", "danger");
            return;
        }

        var layout = mini.get("layout1");

        var dimensionTypeGrid = mini.get("dimension-type-grid");
        tools.initGrid(dimensionTypeGrid);
        dimensionTypeGrid.setDataField("result");

        var dimensionGrid = mini.get("dimension-grid");
        tools.initGrid(dimensionGrid);


        $("#bind").hide();
        initDimensionType();
        initDimension();

        /***********************维度类型相关*****************************/

        function initDimensionType() {
            dimensionTypeGrid.setUrl(API_BASE_PATH + "dimension-type/all");
            dimensionTypeGrid.load();
            dimensionTypeGrid.on("rowclick", function (e) {
                var row = e.record;
                window.nowSelectedType = row;

                loadDimension();
            });
        }

        /***********************目标维度相关*****************************/
        function loadDimension() {
            if (window.nowSelectedType) {
                $("#bind").show();
                dimensionGrid.loading();
                require(["message"], function (message) {
                    request.createQuery("autz-setting/_query/no-paging")
                        .where("dimensionType", window.nowSelectedType.id)
                        .and("permission", permissionId)
                        .exec(function (response) {
                            dimensionGrid.unmask();
                            if (response.status === 200) {
                                dimensionGrid.setData(response.result);
                            } else {
                                message.showTips("加载维度数据失败:" + response.message);
                            }
                        });
                });
            }
        }

        function initDimension() {
            //操作栏的按钮
            dimensionGrid.getColumn("action").renderer = function (e) {
                var html = [];
                var row = e.record;

                html.push(tools.createActionButton("删除", "icon-remove", function () {
                    require(["message"], function (message) {
                        message.confirm("确定删除该维度设置?", function () {
                            var loading = message.loading("删除中...");
                            request["delete"]("autz-setting/" + row.id, {}, function (res) {
                                loading.close();
                                if (res.status === 200) {
                                    e.sender.removeRow(row);
                                } else {
                                    message.showTips("删除失败:" + res.message);
                                }
                            })
                        });
                    })
                }));

                return html.join("");
            };
            var ifmWin;
            var doReload;
            //点击表格时的操作
            dimensionGrid.on("rowclick", function (e) {
                var row = e.record;
                if (ifmWin && ifmWin.init) {
                    doReload = function () {
                        ifmWin.init(
                            {
                                id: row.id,
                                settingFor: row.dimensionTarget,
                                dimension: row.dimensionTarget,
                                dimensionName: row.dimensionTargetName,
                                dimensionTypeName: window.nowSelectedType.name,
                                permission: permissionId,
                                permissionType: request.getParameter("permissionType") || ''
                            }
                        );
                    };
                    doReload();
                    return;
                }
                var ifm = $("#ifm").attr("src", "/admin/autz-settings/permission-setting.html?priority=10&dimensionType=" + row.dimensionType
                    + "&id=" + row.id
                    + "&settingFor=" + row.dimensionTarget
                    + "&dimension=" + row.dimensionTarget
                    + "&dimensionName=" + row.dimensionTargetName
                    + "&dimensionTypeName=" + window.nowSelectedType.name
                    + "&permission=" + permissionId
                    + "&permissionType=" + (request.getParameter("permissionType") || ''));

                function init() {
                    ifmWin = ifm[0].contentWindow;
                    if (ifmWin.init) {
                        doReload && doReload();
                    } else {
                        ifmWin.onInit = function () {
                            doReload && doReload();
                        }

                    }
                }

                init();
                ifm.on("load", init);
                window.nowSelectedDimension = e.record;
                layout.expandRegion("east");
            });
        }

        $(".bind-dimension").on('click', function () {
            var dimensionType = window.nowSelectedType.id;
            if (dimensionTypeMap[dimensionType]) {
                var dimension = dimensionTypeMap[dimensionType];
                openBindDimensionWindow(dimension.uri, dimension.typeName, dimension.relationApi);
            } else {
                openBindDimensionWindow("admin/dimension/list.html?selector=1", "维度目标");
            }

        });

        function openBindDimensionWindow(uri, typeName, relationApi) {
            tools.openWindow(uri + "&dimensionType=" + window.nowSelectedType.id + "&permission=" + permissionId,
                "选中" + typeName,
                "800",
                "600",
                function (data) {
                    if (data !== 'cancel' && data !== 'close') {

                        //todo 不同维度 关联关系不同
                        if (relationApi) {
                            var dimensionRelation = {};
                            dimensionRelation.dimensionId = window.nowSelectedType.id;
                            dimensionRelation.dimensionName = window.nowSelectedType.name;
                            dimensionRelation.userId = data.id;
                            dimensionRelation.userName = data.username;
                            dimensionRelation.dimensionTypeId = window.nowSelectedType.id;
                            request.post(relationApi, dimensionRelation, function (res) {

                            });
                        }

                        var data = {
                            type: 'user',
                            settingFor: data.id,
                            dimensionTargetName: data.name,
                            dimensionTarget: data.id,
                            dimensionType: window.nowSelectedType.id,
                            dimensionTypeName: window.nowSelectedType.name,
                            permission: permissionId,
                            state: 1
                        };
                        dimensionGrid.loading();
                        request.post("autz-setting", data, function (res) {
                            if (res.status === 200) {
                                dimensionGrid.unmask();
                                data.id = res.result.id;
                            }
                        });


                        dimensionGrid.addRow(data);
                        startEdit(dimensionGrid, data);

                    }
                });
        }
    });

});