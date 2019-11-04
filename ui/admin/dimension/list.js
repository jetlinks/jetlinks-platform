importResource("/admin/css/common.css");

require(["authorize"], function (authorize) {
    authorize.parse(document.body);
    window.authorize = authorize;
    importMiniui(function () {
        mini.parse();
        require(["miniui-tools", "request"], function (tools, request) {
            window.tools = tools;
            var dimensionType = request.getParameter("dimensionType");

            var typeGrid = window.typeGrid = mini.get("type-grid");
            var dimensionGrid = window.grid = mini.get("dimension-grid");
            var userGrid = window.userGrid = mini.get("user-grid");
            tools.initGrid(typeGrid);
            typeGrid.setUrl(API_BASE_PATH + "dimension-type/_query/no-paging");
            typeGrid.setDataField("result");


            var dimensionSplitter = mini.get("dt-splitter");
            var userSplitter = mini.get("du-splitter");

            initDimensionType();
            initDimension();
            initDimensionUser();

            /***********************维度类型相关*****************************/
            function initDimensionType() {
                userSplitter.collapsePane(2);
                typeGrid.on("rowclick", function (e) {
                    window.nowSelectedType = e.record;

                    loadDimension();
                });
            }

            $(".add-type-button").on("click", function () {
                tools.openWindow("admin/dimension/type-save.html", "维度类型编辑", "500", "300", function () {
                    typeGrid.reload();
                });
            });
            //设置维度类型操作列
            typeGrid.getColumn("action").renderer = function (e) {
                var html = [];
                var row = e.record;
                if (authorize.hasPermission("dimension", "save")) {
                    html.push(tools.createActionButton("删除", "icon-remove", function () {
                        require(["message"], function (message) {
                            message.confirm("确定删除该维度类型?", function () {
                                var loading = message.loading("删除中...");
                                request["delete"]("dimension-type/" + row.id, {}, function (res) {
                                    loading.close();
                                    if (res.status === 200) {
                                        typeGrid.reload();
                                        message.showTips("删除成功");
                                    } else {
                                        message.showTips("删除失败:" + res.message);
                                    }
                                })
                            });
                        })
                    }))
                }
                return html.join("");
            };

            /***********************目标维度相关*****************************/

            function search() {
                if (dimensionType) {
                    tools.searchGrid("#search-box", dimensionGrid, request.encodeQueryParam({typeId: dimensionType}));
                } else {
                    tools.searchGrid("#search-box", dimensionGrid);
                }
            }

            function loadDimension() {
                if (window.nowSelectedType) {
                    dimensionGrid.loading();
                    require(["message"], function (message) {
                        request.createQuery("dimension/_query/no-paging")
                            .where("typeId", window.nowSelectedType.id)
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

            $(".search-button").click(search);
            tools.bindOnEnter("#search-box", search);
            search();

            // $(".save-all-button").on("click", function () {
            //     require(["message"], function (message) {
            //         message.confirm("确认保存全部维度数据", function () {
            //             grid.loading("保存中...");
            //             request.patch("dimension/batch", grid.getData(), function (response) {
            //                 if (response.status == 200) {
            //                     message.showTips("保存成功");
            //                     grid.reload();
            //                 } else {
            //                     message.showTips("保存失败:" + response.message);
            //                 }
            //             });
            //         });
            //     })
            // });
            function initDimension() {
                console.log(dimensionGrid)
                dimensionGrid.getColumn("action").renderer = function (e) {
                    var html = [];
                    var row = e.record;
                    if (authorize.hasPermission("dimension", "save")) {
                        html.push(tools.createActionButton("添加子维度", "icon-add", function () {
                            var sortIndex = row.sortIndex ? (row.sortIndex + "0" + (row.chidren ? row.chidren.length + 1 : 1)) : 1;
                            dimensionGrid.addNode({sortIndex: sortIndex}, row.chidren ? row.chidren.length : 0, row);
                        }));
                    }

                    if (request.getParameter("selector") === '1') {
                        html.push(
                            tools.createActionButton("选中", "icon-ok", function () {
                                require(["message"], function (message) {
                                    message.loading("绑定中..")
                                    request.get("autz-setting/_query/no-paging",
                                        request.encodeQueryParam({
                                            permission: request.getParameter("permission"),
                                            dimensionTarget: row.id
                                        }), function (res) {
                                            message.loading().hide();
                                            if (res.status === 200) {
                                                console.log(res.result.length)
                                                if (res.result.length > 0) {
                                                    message.showTips("该维度已绑定..", "danger");
                                                } else {
                                                    tools.closeWindow(row);
                                                }
                                            }
                                        });
                                });
                            })
                        );
                    } else {
                        html.push(
                            tools.createActionButton("维度赋权", "icon-find", function () {
                                tools.openWindow("/admin/autz-settings/permission-setting.html?priority=40&merge=true"
                                    + "&settingFor=" + row.id
                                    + "&dimension=" + row.id
                                    + "&dimensionName=" + row.name
                                    + "&dimensionType=" + row.typeId
                                    + "&dimensionTypeName=" + window.nowSelectedType.name,//todo 维度类型加载
                                    "维度赋权-" + row.name, "800", "600", function () {
                                    });
                            })
                        );
                    }
                    if (row._state == "added" || row._state == "modified") {
                        html.push(tools.createActionButton("保存", "icon-save", function () {
                            var api = "dimension/";
                            require(["request", "message"], function (request, message) {
                                var loading = message.loading("保存中...");
                                request.patch(api, row, function (res) {
                                    loading.hide();
                                    if (res.status === 200) {
                                        request.get(api + res.result, function (data) {
                                            dimensionGrid.updateNode(row, data.result);
                                            dimensionGrid.acceptRecord(row);
                                            message.showTips("保存成功!");
                                        });
                                    } else {
                                        message.showTips("保存失败:" + res.message, "danger");
                                    }
                                })
                            });
                        }));
                    }
                    html.push(tools.createActionButton("删除", "icon-remove", function () {
                        if (row._state === "added") {
                            e.sender.removeNode(row);
                        } else {
                            require(["message"], function (message) {
                                message.confirm("确定删除该维度?", function () {
                                    var loading = message.loading("删除中...");
                                    request["delete"]("dimension/" + row.id, {}, function (res) {
                                        loading.close();
                                        if (res.status === 200) {
                                            e.sender.removeNode(row);
                                            message.showTips("删除成功");
                                        } else {
                                            message.showTips("删除失败:" + res.message);
                                        }
                                    })
                                });
                            })
                        }
                    }));
                    return html.join("");
                };

                dimensionGrid.on("nodeclick", function (e) {
                    window.nowSelectedDimension = e.record;

                    loadUser();
                });
            }


            /***********************用户相关*****************************/

            function loadUser() {
                if (window.nowSelectedDimension) {
                    userSplitter.expandPane(2);
                    userGrid.loading();
                    require(["message"], function (message) {
                        request.createQuery("dimension-user/_query/no-paging")
                            .where("dimensionId", window.nowSelectedDimension.id)
                            .exec(function (response) {
                                userGrid.unmask();
                                if (response.status === 200) {
                                    userGrid.setData(response.result);
                                } else {
                                    message.showTips("加载维度用户数据失败:" + response.message);
                                }
                            });
                    });
                }
            }

            function initDimensionUser() {
                console.log(userGrid)
                userGrid.getColumn("action").renderer = function (e) {
                    var html = [];
                    var row = e.record;
                    if (authorize.hasPermission("dimension", "delete")) {
                        html.push(tools.createActionButton("删除", "icon-remove", function () {
                            require(["message"], function (message) {
                                message.confirm("确定删除该绑定关系?", function () {
                                    var loading = message.loading("删除中...");
                                    request["delete"]("dimension-user/" + row.id, {}, function (res) {
                                        loading.close();
                                        if (res.status === 200) {
                                            loadUser();
                                            message.showTips("删除成功");
                                        } else {
                                            message.showTips("删除失败:" + res.message);
                                        }
                                    })
                                });
                            })
                        }))
                    }
                    return html.join("");
                }
            }

            $(".bind-user").on("click", function () {
                tools.openWindow("admin/dimension/user/list.html?selector=1&dimensionTypeId="
                    + window.nowSelectedDimension.typeId
                    + "&dimensionName=" + window.nowSelectedDimension.name
                    + "&dimensionId=" + window.nowSelectedDimension.id,
                    "选择用户",
                    "800",
                    "600",
                    function (data) {
                        if (data !== 'cancel' && data !== 'close') {

                            if (data instanceof Array) {
                                $(data).each(function (i) {
                                    if (this.username) data[i].userName = this.username;

                                });
                            } else {

                                if (data.username) data.userName = data.username;
                            }

                            loadUser();
                        }
                    });
            })
        });
    });
});
