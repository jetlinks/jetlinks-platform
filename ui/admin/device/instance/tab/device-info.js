importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message"], function (request, tools, message) {
        var id = request.getParameter("id");
        request.get("device-instance/info/" + id, function (response) {
            var data = response.result;
            for (var key in data) {
                $("._" + key).text(data[key]);
            }
        });


        propertyLoad();

        function propertyLoad() {
            request.get("device-instance/" + id + "/properties", function (response) {
                var html = "";
                console.log(response)
                for (var i = 0; i < response.length; i++) {
                    html += propertyPlate(response[i].propertyName, response[i].formatValue, response[i].property);
                }
                $("#property-content").html(html);
            });
        }

        function propertyPlate(key, value, name) {
            var classes = "_" + name;
            return "<div class=\"mini-col-4 info-div\">" +
                "<span class=\"info-key property-key\">" + key + ":</span>" +
                "<span style='display: none' class=\"property-name\">" + name + "</span>" +
                "<span class=\"info-val " + classes + "\">" + value + "</span></div>";
        }

        // TODO: 2019/10/26 刷新导致属性单位不存在。解决办法，设备上报时候自带单位、后台解析传到前台、前台解析
        $(".refresh-button").on("click", function () {
            var params = [];
            $('.property-name').each(function () {
                params.push($(this).html());
            });
            request.post("device/" + id + "/properties", params, function (response) {
                if (response.status == 200) {
                    for (var key in response[0]) {
                        console.log(response[0][key])
                        $("._" + key).text(response[0][key]);
                    }
                } else {
                    message.showTips(response.message);
                }
            });
        })


    });
});


