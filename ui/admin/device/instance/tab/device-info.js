importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools",], function (request, tools) {
        var id = request.getParameter("id");
        request.get("device-instance/info/" + id, function (response) {
            console.log(response)
            var data = response.result;
            for (var key in data) {
                $("._" + key).text(data[key]);
            }
        })
    });
});


