package org.jetlinks.platform.micrometer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.elastic.ElasticConfig;
import io.micrometer.elastic.ElasticMeterRegistry;
import org.hswebframework.web.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${management.metrics.export.device-info.index:device_info}")
    private String index;

    @Value("${management.metrics.export.device-info.host:http://localhost:9200}")
    private String host;

    @Value("${management.metrics.export.device-info.host:yyyy-MM-dd}")
    private String indexDateFormat;


    private MeterRegistry createDefaultMeterRegistry() {
        ElasticConfig elasticConfig = new ElasticConfig() {

            public String get(String s) {
                return null;
            }

            @Override
            public String host() {
                return host;
            }

            @Override
            public String index() {
                return index;
            }

            @Override
            public String indexDateFormat() {
                return indexDateFormat;
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
