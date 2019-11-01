importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools"], function (request, tools) {
        var id = request.getParameter("id");
        var productId = request.getParameter("productId");
        request.get("device-instance/" + id, function (response) {
            var data = response.result;
            $(".deviceName").text(data.name);
            $(".deviceId").text(data.id);
            $(".productName").text(data.productName);
            $(".state").text(data.state.text);
        })
        var tabs = mini.get("tabs");
        var tabsData = [
            {title: "设备信息", url: "/admin/device/instance/tab/device-info.html?id=" + id /*refreshOnClick: true*/},
            {title: "运行状态", url: "/admin/device/instance/tab/run-info.html?id=" + id + "&productId=" + productId},
            {title: "设备日志", url: "/admin/device/instance/tab/device-log.html?id=" + id},
            {title: "在线调试", url: "/admin/device/instance/tab/device-debug.html?id=" + id}
        ]
        tabs.setTabs(tabsData);


    });
});


