define(["message"], function (message) {
    var win;


    function init(panel, model) {

        model.config = model.config || {};
        if(!mini.getbyName('config.lang').getValue()){
            mini.getbyName('config.lang').setValue("javascript")
        }
        panel.find(".edit-script")
            .unbind("click")
            .on("click", function () {
                var loading = message.loading("加载中...");
                loadScriptEditor(function (editor) {
                    var lang = mini.getbyName('config.lang').getValue() || 'javascript';
                    editor.init(lang, model.config.script || '');
                    win.show();
                    loading.hide();
                    $(".save-script").unbind("click")
                        .on("click", function () {
                            model.config.script = editor.getScript();
                            win.hide()
                        })
                });

            });


    }

    function loadScriptEditor(call) {

        loadWindow(function () {
            require(['script-editor'], function (builder) {
                builder.createEditor("script-editor", function (editor) {
                    call(editor);
                })
            });
        })
    }

    function loadWindow(call) {
        if (win) {
            call(win);
        } else {
            require(["text!admin/rule-engine/model/nodes/edit-script.html"], function (html) {
                $(document.body).append(html);
                mini.parse();
                win = mini.get("edit-script-window");
                call(win);
            })
        }
    }


    return {
        init: init,
        debugSupport: true
    }
});