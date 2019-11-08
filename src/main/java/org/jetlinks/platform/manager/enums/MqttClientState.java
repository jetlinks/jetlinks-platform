package org.jetlinks.platform.manager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hswebframework.web.dict.Dict;
import org.hswebframework.web.dict.EnumDict;

@AllArgsConstructor
@Getter
@Dict("device-product-state")
public enum MqttClientState implements EnumDict<Byte> {
    unregistered("启用", (byte) 1),
    registered("禁用", (byte) 0);

    private String text;

    private Byte value;

    public String getName() {
        return name();
    }
}
