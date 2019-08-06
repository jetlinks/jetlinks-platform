package org.jetlinks.platform.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetlinks.gateway.session.DeviceSession;

@Getter
@AllArgsConstructor
public class DeviceDisconnectedEvent {
    private DeviceSession session;

}
