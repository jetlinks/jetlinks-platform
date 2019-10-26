package org.jetlinks.platform.manager.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hswebframework.web.bean.FastBeanCopier;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.jetlinks.platform.manager.entity.DeviceProductEntity;
import org.jetlinks.platform.manager.entity.DevicePropertiesEntity;

import java.util.List;
import java.util.Optional;

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
    private String omos;

    public static DeviceInfo of(DeviceInstanceEntity instance,
                                DeviceProductEntity product) {
        DeviceInfo deviceInfo = FastBeanCopier.copy(instance, new DeviceInfo());
        deviceInfo.setMessageProtocol(product.getMessageProtocol());
        deviceInfo.setTransportProtocol(product.getTransportProtocol());
        deviceInfo.setDeviceType(product.getDeviceType().getText());
        if (instance.getSecurity() != null) {
            deviceInfo.setOmos(instance.getSecurity().get("omos").toString());
            deviceInfo.setDeviceKey(instance.getSecurity().get("deviceKey").toString());
            deviceInfo.setDeviceSecret(instance.getSecurity().get("deviceSecret").toString());
        }

        return deviceInfo;
    }

    public static void main(String[] args) {
        String test = "{\n" +
                "    \"properties\":[\n" +
                "        {\n" +
                "            \"id\":\"currentTemperature\",\n" +
                "            \"name\":\"当前温度\",\n" +
                "            \"expands\":{\n" +
                "                \"readonly\":true\n" +
                "            },\n" +
                "            \"valueType\":{\n" +
                "                \"type\":\"double\",\n" +
                "                \"unit\":\"celsiusDegrees\",\n" +
                "                \"max\":100,\n" +
                "                \"min\":1\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\":\"cpuUsage\",\n" +
                "            \"name\":\"cpu使用率\",\n" +
                "            \"readonly\":true,\n" +
                "            \"valueType\":{\n" +
                "                \"type\":\"double\",\n" +
                "                \"unit\":\"percent\"\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"functions\":[\n" +
                "        {\n" +
                "            \"id\":\"playVoice\",\n" +
                "            \"name\":\"播放声音\",\n" +
                "            \"async\":false,\n" +
                "            \"inputs\":[\n" +
                "                {\n" +
                "                    \"id\":\"text\",\n" +
                "                    \"name\":\"文字内容\",\n" +
                "                    \"valueType\":{\n" +
                "                        \"type\":\"string\"\n" +
                "                    }\n" +
                "                }\n" +
                "            ],\n" +
                "            \"output\":{\n" +
                "                \"id\":\"success\",\n" +
                "                \"name\":\"是否成功\",\n" +
                "                \"valueType\":{\n" +
                "                    \"type\":\"boolean\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"events\":[\n" +
                "        {\n" +
                "            \"id\":\"temp_sensor\",\n" +
                "            \"name\":\"温度传感器\",\n" +
                "            \"parameters\":[\n" +
                "                {\n" +
                "                    \"id\":\"temperature\",\n" +
                "                    \"name\":\"温度\",\n" +
                "                    \"valueType\":{\n" +
                "                        \"type\":\"double\"\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\":\"get_time\",\n" +
                "                    \"name\":\"采集时间\",\n" +
                "                    \"valueType\":{\n" +
                "                        \"type\":\"timestamp\"\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        System.out.println(test);
    }
}
