package org.jetlinks.platform.manager.enums;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hswebframework.web.dict.EnumDict;

@AllArgsConstructor
@Getter
public enum DeviceLogType implements EnumDict<String> {
    event("事件上报"),
    readProperty("属性读取"),
    writeProperty("属性修改"),
    reportProperty("属性上报"),
    call("调用"),
    reply("回复"),
    offline("下线"),
    online("上线"),
    other("其它");

    @JSONField(serialize = false)
    private String text;

    @Override
    public String getValue() {
        return name();
    }

//    @Override
//    public Object getWriteJSONObject() {
//        return getValue();
//    }
}
