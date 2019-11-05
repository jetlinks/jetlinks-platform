importResource("/admin/css/common.css");

importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "message"], function (request, tools, message) {
        var id = request.getParameter("id");
        request.get("device-instance/info/" + id, {}, function (response) {
            var data = response.result;
            for (var key in data) {
                $("._" + key).text(data[key]);
            }
            handleSecurityProperties(data.security)
        });

        function handleSecurityProperties(security) {
            if (security.omos === 'true') {
                $("._omosText").text("是");
                new mini.Form("#security-info").setData(security);
                mini.getbyName("deviceKey").setReadOnly(false);
                mini.getbyName("deviceSecret").setReadOnly(false);
            } else {
                $("._omosText").text("否");
                new mini.Form("#security-info").setData(security);
                $(".security-button-save").css("background","#e5e5e5");
                $(".security-button-save").attr("disabled","disabled");
                $(".security-button-reset").css("background","#e5e5e5");
                $(".security-button-reset").css("color","#fff");
                $(".security-button-reset").attr("disabled","disabled");
            }
        }

        // function setSecurityDisabledStyle(disabled) {
        //     if (disabled){
        //
        //     } else {
        //         $(".security-button-save").css("background","#1890ff");
        //         $(".security-button-save").attr("disabled","false");
        //         $(".security-button-reset").css("background","#fff");
        //         $(".security-button-reset").css("color","#626262");
        //         $(".security-button-reset").attr("disabled","false");
        //     }
        // }

        function getSecurityDataAndValidate() {
            var form = new mini.Form("#security-info");
            form.validate();
            if (form.isValid() === false) {
                return;
            }
            var data = form.getData();
            data.omos = 'false';
            return data;
        }

        $(".security-button-save").on('click', function () {
            request.patch("device-instance", {"security": getSecurityDataAndValidate(), "id": id}, function (response) {
                if (response.status === 200){
                    message.showTips("保存成功");
                }
            })
        });
        $(".security-button-reset").on('click', function () {
            request.post("device-instance/reset/security/" + id, {}, function (response) {
                if (response.status === 200){
                    var security = response.result;
                    security["omos"] = 'true';
                    handleSecurityProperties(security)
                    message.showTips("重置成功");
                }
            })
        });


        propertyLoad();

        function propertyLoad() {
            request.get("device-instance/" + id + "/properties", function (response) {
                var html = "";
                var properties = response.result;
                for (var i = 0; i < properties.length; i++) {
                    html += propertyPlate(properties[i].propertyName, properties[i].formatValue, properties[i].property);
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
                    message.showTips("刷新成功");
                } else {
                    message.showTips(response.message);
                }
            });
        })


    });
});


