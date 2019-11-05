importResource("/admin/css/common.css");


var defaultPassword = Math.random() + "";

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools"], function (request, tools) {
        var roleGrid = mini.get("datagrid");
        tools.initGrid(roleGrid);
        roleGrid.setUrl(request.basePath + "dimension/_query/no-paging");
        roleGrid.setDataField("result");
        roleGrid.load();
        var api = "user";
        var func = request.patch;
        var id = request.getParameter("id");

        roleGrid.on("load", function () {
            if (id) {
                loadData(id);
            }
        });
        $(".save-button").on("click", (function () {
            require(["message"], function (message) {
                var data = getDataAndValidate();
                console.log(data)
                if (!data) return;
                var loading = message.loading("提交中");
                func(api, data, function (response) {
                    loading.close();
                    if (response.status === 200) {

                        if (!id) id = response.result;
                        // var dimensionUser = getDimensionUserData(id, data.username);
                        //
                        // request.post("dimension-user/_batch", dimensionUser, function (res) {//todo 重复提交问题
                        //     if (res.status === 200) {
                        //         message.showTips("保存成功");
                        //     } else {
                        //         message.showTips("绑定维度失败："+ res.message, "danger");
                        //     }
                        // });
                    } else {
                        message.showTips("保存失败:" + response.message, "danger");
                        if (response.result)
                            tools.showFormErrors("#basic-info", response.result);
                    }
                })
            });
        }));

        function getDataAndValidate() {
            var form = new mini.Form("#basic-info");
            form.validate();
            if (form.isValid() === false) {
                return;
            }
            var data = form.getData();
            if (data.password === defaultPassword) {
                delete data.password;
            }
            if (id) data.id = id;
            return data;
        }

        function getDimensionUserData(id, userName) {

            var dimensions = [];
            $(mini.get("datagrid").getSelecteds()).each(function () {
                this.userId = id;
                this.userName = userName;
                this.dimensionName = this.name;
                this.dimensionId = this.id;
                this.dimensionTypeId = this.typeId;
                dimensions.push(this);
            });
            return dimensions;
        }
    });
});

function loadData(id) {
    require(["request", "message"], function (request, message) {
        var loading = message.loading("加载中...");
        request.get("user/" + id, function (response) {
            loading.hide();
            if (response.status === 200) {
                response.result.password = defaultPassword;
                new mini.Form("#basic-info").setData(response.result);
                var roleGrid = mini.get("datagrid");
                $(response.result.roles).each(function (i, roleId) {
                    var rows = [];
                    roleGrid.findRow(function (row) {
                        if (row.id === roleId) rows.push(row);
                    });
                    roleGrid.selects(rows);
                });
            } else {
                message.showTips("加载数据失败", "danger");
            }
        });
    });
}
