package org.jetlinks.platform.manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hswebframework.web.dict.EnumDict;

@AllArgsConstructor
@Getter
public enum EsDataType implements EnumDict<String> {

    DEVICE_OPERATION("设备操作日志", "device_operation", "_doc");

    private String text;

    private String index;

    private String type;

    @Override
    public String getValue() {
        return name();
    }


}
