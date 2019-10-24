package org.jetlinks.platform.manager.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hswebframework.web.bean.FastBeanCopier;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceInfo {

    //设备实例名称
    private String name;

    //说明
    private String describe;

    //产品名称
    private String productName;

    private String classification;

    private String deviceType;

    //传输协议
    private String transportProtocol;

    //消息协议
    private String messageProtocol;

    private String deviceKey;


    private String deviceSecret;

    //一机一密
    private String yjym;


    private String version;

    private String sn;

    public static DeviceInfo of(DeviceInstanceEntity instance, DeviceProductEntity product) {
        DeviceInfo deviceInfo = FastBeanCopier.copy(instance, new DeviceInfo());
        deviceInfo.setMessageProtocol(product.getMessageProtocol());
        deviceInfo.setTransportProtocol(product.getTransportProtocol());
        deviceInfo.setDeviceType(product.getDeviceType().getText());
        return deviceInfo;
    }
}
