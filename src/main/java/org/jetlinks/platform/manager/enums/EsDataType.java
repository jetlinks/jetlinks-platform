package org.jetlinks.platform.manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hswebframework.web.dict.EnumDict;

@AllArgsConstructor
@Getter
public enum EsDataType implements EnumDict<String> {

    DEVICE_OPERATION("设备操作日志", "device_operation", "_doc"),
    EXECUTE_LOG_INDEX("规则执行日志", "execute_log_index", "_doc"),
    EXECUTE_EVENT_LOG_INDEX("规则事件日志", "execute_event_log_index", "_doc");

    private String text;

    private String index;

    private String type;

    @Override
    public String getValue() {
        return name();
    }


}
