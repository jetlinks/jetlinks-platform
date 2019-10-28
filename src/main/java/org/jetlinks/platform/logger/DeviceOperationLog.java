package org.jetlinks.platform.logger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetlinks.platform.manager.enums.DeviceLogType;

import java.util.Date;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceOperationLog {

    private String deviceId;

    private DeviceLogType type;

    private Date createTime;

    private Object content;
}
