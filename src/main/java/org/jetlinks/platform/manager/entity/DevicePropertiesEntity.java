package org.jetlinks.platform.manager.entity;

import lombok.Getter;
import lombok.Setter;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Table(name = "dev_properties")
@Getter
@Setter
public class DevicePropertiesEntity extends SimpleGenericEntity<String> {

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "property")
    private String property;

    @Column(name = "property_name")
    private String propertyName;

    @Column(name = "string_value")
    private String stringValue;

    @Column(name = "number_value")
    private Number numberValue;

    @Column(name = "format_value")
    private String formatValue;

    @Column(name="update_time")
    private Date updateTime;


    public Map<String,Object> toMap(){
        Map<String,Object> data = copyTo(new HashMap<>());
        data.put("updateTime",updateTime.getTime());

        return data;
    }
}
