package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.api.crud.entity.GenericEntity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Table(name = "dev_properties")
@Getter
@Setter
public class DevicePropertiesEntity extends GenericEntity<String> {

    @Override
    @GeneratedValue(generator = "snow_flake")
    public String getId() {
        return super.getId();
    }

    @Column(name = "device_id",length = 32)
    private String deviceId;

    @Column(name = "property")
    private String property;

    @Column(name = "property_name")
    private String propertyName;

    @Column(name = "string_value")
    private String stringValue;

    @Column(name = "number_value")
    private BigDecimal numberValue;

    @Column(name = "format_value")
    private String formatValue;

    @Column(name="update_time")
    private Date updateTime;

    //未做任何处理的属性字符串类型值
    @Column(name="value")
    private String value;


    public Map<String,Object> toMap(){
        Map<String,Object> data = copyTo(HashMap.class);
        data.put("updateTime",updateTime.getTime());

        return data;
    }
}
