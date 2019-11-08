package org.jetlinks.platform.manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hswebframework.web.dict.Dict;
import org.hswebframework.web.dict.EnumDict;

@AllArgsConstructor
@Getter
@Dict("mqtt-client-state")
public enum MqttClientState implements EnumDict<Byte> {
    enabled("启用", (byte) 1),
    disabled("禁用", (byte) 0);

    private String text;

    private Byte value;

}
