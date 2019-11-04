importResource("/admin/css/common.css");


var productName = '';
importMiniui(function () {
    mini.parse();

    hide();

    var typeData = [
        {id: "jar", text: "jar"},
        {id: "other", text: "其他"}];
    var typeBox = mini.getbyName("type");
    var provider = mini.getbyName("provider");
    var location = mini.getbyName("location");
    typeBox.setData(typeData);
    typeBox.on("valuechanged", function (e) {
        if (e.value === "jar") {
            show();
        } else {
            hide();
        }
    });

    function show() {
        $(".provider").show();
        $(".location").show();
    }

    function hide() {
        $(".provider").hide();
        $(".location").hide();
    }

    require(["request", "miniui-tools", "plugin/webuploader/webuploader.min", "storejs"],
        function (request, tools, WebUploader, storejs) {

            var func = request.post;
            var id = request.getParameter("id");
            var type = request.getParameter("type")
            var api = "protocol";
            if (id) {
                loadData(id);
                api += "/" + id;
                func = request.put;
            }
            if (type && type === "jar") show();
            $(".save-button").on("click", (function () {
                require(["message"], function (message) {
                    var data = getDataAndValidate();
                    if (!id) {
                        data.state = 0;
                    }
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

            initWebUploader(WebUploader, storejs, function (file, response) {
                require(["message"], function (message) {
                    var loading = message.loading("导入中...");
                    if (response.status === 200 && response.result) {
                        loading.close();
                        var fileUrl = response.result;
                        location.setValue(fileUrl);
                    } else {
                        message.showTips("服务器繁忙..");
                    }
                });
            });

            function loadData(id) {
                require(["message"], function (message) {
                    var loading = message.loading("加载中...");
                    request.get("protocol/" + id, function (response) {
                        loading.hide();
                        if (response.status === 200) {
                            var data = response.result;
                            if (type==="jar"){
                                data.provider = data.configuration.provider;
                                data.location = data.configuration.location;
                            }

                            new mini.Form("#basic-info").setData(response.result);
                        } else {
                            message.showTips("加载数据失败", "danger");
                        }
                    });
                });
            }

        });
});


function getDataAndValidate() {
    var form = new mini.Form("#basic-info");
    form.validate();
    if (form.isValid() === false) {
        return;
    }
    var data = form.getData();
    if (data.provider && data.location) {
        var configuration = {};
        configuration.provider = data.provider;
        configuration.location = data.location;
        data.configuration = configuration;
    }
    return data;
}

function initWebUploader(WebUploader, storejs, responseCall) {
    var uploader = WebUploader.create({

        // swf文件路径
        swf: '../plugin/webuploader/Uploader.swf',

        // 文件接收服务端。
        server: API_BASE_PATH + 'file/static',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#upload-button',
        multiple: false,
        auto: true,
        threads: 1,
        duplicate: true,
        extensions: 'xlsx,csv,jar',
        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false
    });

    uploader.on('fileQueued', function (file) {

        uploader.upload();
    });


    uploader.on("uploadBeforeSend", function (e, param, headers) {
        let token = storejs.get("X-Access-Token");
        if (token) {
            headers['X-Access-Token'] = token;
        }
    });
    uploader.on('uploadSuccess', function (file, response) {
        responseCall(file, response);
    });

    uploader.on('uploadError', function (file) {

    });

    uploader.on('uploadComplete', function (file) {

    });

}