package org.jetlinks.platform.manager.logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetlinks.platform.manager.enums.DeviceLogType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceOperationLog {

    private String id;

    private String deviceId;

    private DeviceLogType type;

    @JSONField(format = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date createTime;

    private Object content;

    public Map<String, Object> toSimpleMap() {

        Map<String, Object> result = (Map) JSON.toJSON(this);
        result.put("type", type.getValue());
        if (getContent() instanceof Map){
            result.put("content", JSON.toJSONString(getContent()));
        }else {
            result.put("content", getContent());
        }
        return result;
    }
}
