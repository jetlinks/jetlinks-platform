define(["request"], function (request) {
    
    return {
        init: function () {
            request.get("datasource", function (e) {
                if (e.status === 200) {
                    $(e.result).each(function () {
                        this.text = this.name + (this.id ? "(" + this.id + ")" : "");
                    });
                    mini.getbyName('config.dataSourceId').setData(e.result);
                }
            });
        },
        debugSupport: true
    }
});