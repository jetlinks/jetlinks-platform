importResource("/admin/css/common.css");


var productName = '';
importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools"], function (request, tools) {
        var productIdCommbox = mini.get("productId");
        productIdCommbox.setUrl(API_BASE_PATH + "device-product/_query/no-paging");
        productIdCommbox.on('valuechanged', function () {
            productName = productIdCommbox.getText();
        });
        var func = request.post;
        var id = request.getParameter("id");
        var api = "device-instance";
        console.log(id)
        if (id) {
            //loadData(id);
            api += "/" + id;
            func = request.put;
        }
        $(".save-button").on("click", (function () {
            require(["message"], function (message) {
                var data = getDataAndValidate();
                if (!data) return;
                var loading = message.loading("提交中");
                func(api, data, function (response) {
                    loading.close();
                    if (response.status === 200) {
                        message.showTips("保存成功");
                        if (!id) id = response.result;
                    } else {
                        message.showTips("保存失败:" + response.message, "danger");
                        if (response.result)
                            tools.showFormErrors("#basic-info", response.result);
                    }
                })
            });
        }));
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

function getDataAndValidate() {
    var form = new mini.Form("#basic-info");
    form.validate();
    if (form.isValid() === false) {
        return;
    }
    var data = form.getData();
    data.productName = productName;
    return data;
}
