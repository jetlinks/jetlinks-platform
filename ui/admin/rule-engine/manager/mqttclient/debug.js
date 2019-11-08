importResource("/admin/css/common.css");


importMiniui(function () {
    mini.parse();
    require(["request", "miniui-tools", "search-box", "message"], function (request, tools, searchBox, message) {
        var id = request.getParameter("id");
        var name = request.getParameter("name");
        var receiveMessage = mini.get("receiveMessage");
        var lastErrMessage = mini.get("lastErrorMessage");
        $(".clientName").text(name);
        //刷新客户端状态
        refreshStatus(false);

        function refreshStatus(tag) {
            request.get("mqtt-client/isAlive/" + id, function (response) {
                if (response.status === 200) {
                    if (tag) {
                        message.showTips("刷新成功");
                    }
                    if (response.result) {
                        $(".clientAlive").text("活跃的");
                        $("#lastErrorPanel").hide();
                        $(".stop-button").show();
                    } else {
                        $("#lastErrorPanel").show();
                        $(".stop-button").hide();
                        $(".clientAlive").text("断开");
                    }
                } else {
                    message.showTips("获取客户端状态失败");
                }
            });
        }

        $(".refresh-button").on("click", function () {
            refreshStatus(true);
        });
        //断开客户端连接
        $(".stop-button").on("click", function () {
            request.post("mqtt-client/disable/" + id, function (response) {
                if (response.status === 200) {
                    message.showTips("客户端已断开");
                    $(".clientAlive").text("停止");
                    $(".stop-button").hide();
                    $("#lastErrorPanel").show();
                } else {
                    message.showTips("停止客户端失败");
                }
            });
        });

        //消息订阅
        function subscribe() {
            var data = getDataAndValidate("sub-basic-info");
            var stream =new EventSource(API_BASE_PATH+"mqtt-client/subscribe/" + id + "/" + data.type+"?topics="+(encodeURIComponent(data.topics))+"&:X_Access_Token="+request.getToken());
            printLog("开始订阅:"+data.topics)
            stream.onmessage=function (ev) {
                printLog(ev.data);
            };
            stream.onerror=function (ev) {
                receiveMessage.setValue("");
                printLog("error");
                stream.close()
            }
        }
        function printLog(data){
            var old = receiveMessage.getValue()||"";
            receiveMessage.setValue(data+"\n"+old)
        }


        $(".sub-commit-button").on("click", function () {
            subscribe();
        });
        $(".sub-reset-button").on("click", function () {
            receiveMessage.setValue("")
        });


        //消息推送
        function publish() {
            var data = getDataAndValidate("push-basic-info");
            request.post("mqtt-client/publish/" + id + "/" + data.type, data, function (response) {
                if (response.status === 200) {
                    if (response.result === true) {
                        message.showTips("消息推送成功");
                    }else {
                        message.showTips("消息推送失败");
                    }
                } else {
                    message.showTips("消息推送失败:" + response.message);
                }
            });
        }

        $(".push-commit-button").on("click", function () {
            publish();
        });
        $(".push-reset-button").on("click", function () {
            mini.get("push_data").setValue("")
        });

        //获取最后一次错误信息
        function loadLastErr() {
            request.get("mqtt-client/lastError/" + id, function (response) {
                if (response.status === 200) {
                    console.log(response);
                    lastErrMessage.setValue(response.result);
                } else {
                    message.showTips("消息推送失败");
                }
            });
        }
//         var myObject =  {
//             "myProp": "myValue",
//             "subObj": {
//                 "prop": "value"
//             }
//         };
// // 格式化
//         var formattedStr = JSON.stringify(myObject, null, 2);

        $(".last-err-button").on("click", function () {
            loadLastErr();
        });

        function getDataAndValidate(formId) {
            var form = new mini.Form("#" + formId);
            form.validate();
            if (form.isValid() === false) {
                return;
            }
            return form.getData();
        }
    });
});
