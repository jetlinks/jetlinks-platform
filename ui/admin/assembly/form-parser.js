(function () {
    var FormParser = function (config) {
        this.data = config;
        this.components = config.components;
        this.textType = config.textType;
        this.size = config.size;
        this.html = config.html;
        this.css = config.css;
        this.events = {};
        this.parameters = {};
    };

    FormParser.prototype.createSearchBox = function (data, size) {
        var me = this;
        var m = $("<div>");
        m.addClass("mini-col-" + size)
            .addClass("form-component");
        var c = $("<div class=\"form-item brick\">");
        if (me.formText) {
            c.addClass("form-text");
        }
        var label = $("<label class=\"form-label\">");
        label.text(data.name);
        c.append(label).append();
        return m.append(c);
    };

    FormParser.prototype.createInput = function (type, data, size, mateData) {
        var me = this;
        var m = me.createSearchBox(data, size);
        var inputContainer = $("<div class=\"input-block component-body\">");
        var input = $("<input style='width: 100%;height: 100%'>");
        input.attr("name", data.property);
        input.addClass("mini-" + type);
        if (mateData) {
            if (type === "datepicker") {
                input.attr("format", mateData);
                if (mateData.indexOf("ss") !== -1)
                    input.attr("showTime", "true");
            } else {
                input.attr("textField", "text");
                input.attr("valueField", "value");
                input.attr("data", JSON.stringify(mateData));
            }
        }
        m.append(inputContainer.append(input));
        return m;
    };

    FormParser.prototype.render = function (el, size) {
        var me = this;
        var data = this.data.properties;
        var search = $("<div id=\"security-info\" class=\"mini-clearfix search-box\">");
        data.forEach(function (val) {
            if (val.type.id === "string") {
                var inputType;
                if (val.type.length > 256) {
                    inputType = "textarea";
                } else {
                    inputType = "textbox";
                }
                search.append(me.createInput(inputType, val, size));
            } else if (val.type.id === "int") {

            } else if (val.type.id === "float") {

            } else if (val.type.id === "double") {

            } else if (val.type.id === "enum") {
                search.append(me.createInput("combobox", val, size, val.type.elements));
            } else if (val.type.id === "boolean") {
                var list = [{"text": val.type.trueText, "value": val.type.trueValue}, {"text": val.type.falseText, "value": val.type.falseValue}];
                search.append(me.createInput("radiobuttonlist", val, size, list));
            } else if (val.type.id === "date") {
                search.append(me.createInput("datepicker", val, size, val.type.formatter));
            } else if (val.type.id === "object") {

            } else if (val.type.id === "array") {

            } else if (val.type.id === "file") {

            } else if (val.type.id === "password") {
                search.append(me.createInput("password", val, size));
            }
        });
        $(el).html("").append(search);
    };

    if (window.define) {
        define(["css!pages/form/designer-drag/defaults", "pages/form/designer-drag/components"], function () {
            return FormParser;
        })
    } else {
        window.FormParser = FormParser;
    }
})();
