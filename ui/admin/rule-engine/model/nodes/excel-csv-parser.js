define([], function () {
    
    return {
        init: function () {
            var fileType = mini.getbyName('config.fileType');

            fileType.on("valuechanged",function () {
                if (fileType.getValue() === 'CSV'){
                    $(".csvDelimiter").show();
                }else {
                    $(".csvDelimiter").hide();
                }
            });
        },
        debugSupport: true
    }
});