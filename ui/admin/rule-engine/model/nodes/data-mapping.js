define(["miniui-tools"], function (tools) {
    var win;

    window._data_mapping_types = [
        "string", "int", "date"
    ].map(function (value) {
        return {id: value}
    });

    function init(panel, model) {

        panel.find(".edit-mappings")
            .unbind("click")
            .on("click", function () {
                loadWindow(function () {
                    win.show();
                    var grid = mini.get("mappings-grid");
                    if (model.config) {
                        grid.setData(model.config.mappings);
                    }
                    grid.getColumn('action').renderer = function (e) {
                        return tools.createActionButton("删除", "icon-remove", function () {
                            grid.removeRow(e.record);
                        })
                    };

                    $('.save-mappings').unbind("click")
                        .on("click", function () {
                            var conf = model.config ||( model.config = {});
                            conf.mappings = grid.getData();
                            win.hide();
                        })
                });
            })
    }

    function loadWindow(call) {
        if (win) {
            call(win);
        } else {
            require(["text!admin/rule-engine/model/nodes/edit-data-mapping.html"], function (html) {
                $(document.body).append(html);
                mini.parse();
                win = mini.get("edit-data-mapping");
                call(win);
            })
        }
    }

    function onSave(panel, model, data) {
    }

    return {
        debugSupport:true,
        init: init,
        onSave: onSave
    }
})