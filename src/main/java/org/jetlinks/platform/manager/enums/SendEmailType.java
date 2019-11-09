package org.jetlinks.platform.manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hswebframework.web.dict.EnumDict;

@Getter
@AllArgsConstructor
public enum SendEmailType implements EnumDict<String> {
    simple("简单类型"),
    html("html类型"),
    html_img("携带图片的html类型"),
    attachment("含附件类型");


    private String text;

    @Override
    public String getValue() {
        return name();
    }
}
