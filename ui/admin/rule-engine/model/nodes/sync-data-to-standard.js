define(["request"], function (request) {

    return {
        init: function () {
            let data = [];
            request.get("data-standards-service/data-standards/model/no-paging", function (e) {
                if (e.status === 200) {
                    $(e.result).each(function () {
                        data.push({"text": this.name,"id":this.id})
                    });
                    mini.getbyName('config.standardId').setData(data);
                }
            });
        },
        debugSupport: true
    }
});