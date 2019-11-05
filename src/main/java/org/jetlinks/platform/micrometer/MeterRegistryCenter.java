package org.jetlinks.platform.micrometer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.elastic.ElasticConfig;
import io.micrometer.elastic.ElasticMeterRegistry;
import org.hswebframework.web.exception.NotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Component
public class MeterRegistryCenter {

    private Map<String, MeterRegistry> meterRegistryMap = new HashMap<>();


    private MeterRegistry createDefaultMeterRegistry() {
        ElasticConfig elasticConfig = new ElasticConfig() {

            public String get(String s) {
                return null;
            }

            @Override
            public String index() {
                return "device_info";
            }

            @Override
            public String indexDateFormat() {
                return "yyyy-MM-dd";
            }
        };
        return ElasticMeterRegistry.builder(elasticConfig).build();
    }

    public MeterRegistry getDefaultMeterRegister() {
        return getMeterRegister("default");
    }


    public MeterRegistry getMeterRegister(String key) {
        return Optional.ofNullable(meterRegistryMap.get(key))
                .orElseThrow(() -> new NotFoundException("计量注册表不存在"));
    }

    private void registry(String key, MeterRegistry meterRegistry) {
        meterRegistryMap.put(key, meterRegistry);
    }

    @PostConstruct
    public void init() {
        registry("default", createDefaultMeterRegistry());
    }
}
