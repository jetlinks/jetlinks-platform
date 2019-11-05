package org.jetlinks.platform.manager.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hswebframework.web.bean.FastBeanCopier;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.springframework.util.StringUtils;

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

    private Object deviceKey;


    private Object deviceSecret;

    private String deriveMetadata;

    //一机一密
    private Object omos;

    private Map<String, Object> security;

    public static DeviceInfo of(DeviceInstanceEntity instance,
                                DeviceProductEntity product) {
        DeviceInfo deviceInfo = FastBeanCopier.copy(instance, new DeviceInfo());
        deviceInfo.setMessageProtocol(product.getMessageProtocol());
        deviceInfo.setTransportProtocol(product.getTransportProtocol());
        deviceInfo.setDeviceType(product.getDeviceType().getText());
        deviceInfo.setClassification(product.getClassifiedId());
        if (instance.getSecurity() == null || instance.getSecurity().size() == 0) {
            deviceInfo.setSecurity(product.getSecurity());
        }
        if (StringUtils.isEmpty(instance.getDeriveMetadata())){
            deviceInfo.setDeriveMetadata(product.getMetadata());
        }
        return deviceInfo;
    }
}
