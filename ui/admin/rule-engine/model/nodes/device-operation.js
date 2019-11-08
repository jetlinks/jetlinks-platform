define([], function () {
    
    return {
        init: function (panel, model) {

        },
        getParamTemplate:function(model){
            return "{\n" +
                "" +
                "payload:{\n" +
                "" +
                "\n}" +
                "\n}";
        },
        debugSupport: true,

        debugStopSupport:true
    }
});