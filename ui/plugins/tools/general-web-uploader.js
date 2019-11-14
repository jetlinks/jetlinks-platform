define(["jquery", "plugin/webuploader/webuploader.min", "storejs"], function ($, WebUploader, storejs) {

    function initWebUploader(responseCall, buttonId) {
        if (!buttonId) {
            buttonId = "upload-button";
        }
        var uploader = WebUploader.create({

            // swf文件路径
            swf: '../plugin/webuploader/Uploader.swf',

            // 文件接收服务端。
            server: window.API_BASE_PATH + 'file/static',

            // 选择文件的按钮。可选。
            // 内部根据当前运行是创建，可能是input元素，也可能是flash.
            pick: {
                id: '#' + buttonId,
                multiple: false
            },
            multiple: false,
            auto: true,
            threads: 1,
            duplicate: false,
            extensions: 'jar',
            // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
            resize: false
        });

        uploader.on('fileQueued', function (file) {
            uploader.upload();
        });

        uploader.on("uploadBeforeSend", function (e, param, headers) {
            var token = storejs.get("X-Access-Token");
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

        window.setTimeout(function () {
            $($(".webuploader-container").children()[1]).css("height", "32")
        }, 1000)

    }

    return {
        initWebUploader:initWebUploader
    }
})