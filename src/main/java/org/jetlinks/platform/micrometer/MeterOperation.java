package org.jetlinks.platform.micrometer;

import io.micrometer.core.instrument.Gauge;
import org.jetlinks.platform.events.GaugePropertyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Component
public class MeterOperation {

    @Autowired
    private MeterRegistryCenter meterRegistry;

    @EventListener
    public void record(GaugePropertyEvent event) {
        Gauge.builder("设备属性上报", Double.valueOf(event.getPropertyValue().toString()), Number::doubleValue)
                .tag("deviceId", event.getDeviceId())
                .tag("property", event.getPropertyName())
                .register(meterRegistry.getDefaultMeterRegister());
    }
}
