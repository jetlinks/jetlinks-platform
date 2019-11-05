package org.jetlinks.platform.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.Message;

/**
 * @author zhouhao
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public class DeviceMessageEvent<M extends Message> {
//    private DeviceSession session;

    private M message;
}
