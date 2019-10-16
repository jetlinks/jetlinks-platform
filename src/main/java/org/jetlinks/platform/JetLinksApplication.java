package org.jetlinks.platform;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.authorization.basic.configuration.EnableAopAuthorize;
import org.hswebframework.web.crud.annotation.EnableEasyormRepository;
import org.jetlinks.core.device.DeviceInfo;
import org.jetlinks.core.device.DeviceOperation;
import org.jetlinks.core.device.DeviceProductInfo;
import org.jetlinks.core.device.DeviceProductOperation;
import org.jetlinks.core.device.registry.DeviceRegistry;
import org.jetlinks.platform.configuration.DeviceGatewayConfiguration;
import org.jetlinks.platform.configuration.JetLinksConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;


@SpringBootApplication(scanBasePackages = "org.jetlinks.platform",exclude = DataSourceAutoConfiguration.class)
//@EnableCaching
//@EnableAsync
@EnableEasyormRepository("org.jetlinks.platform.manager.entity")
@Import(JetLinksConfiguration.class)
@EnableAopAuthorize
public class JetLinksApplication {

    public static void main(String[] args) {
        SpringApplication.run(JetLinksApplication.class,args);
    }

    @Profile({"dev"})
    @Component
    @ConfigurationProperties(prefix = "jetlinks.simulator")
    @Slf4j
    public static class RegistryDevice implements CommandLineRunner {

        @Autowired
        private DeviceRegistry registry;

        @Getter
        @Setter
        private long initStartWith = 0;

        @Getter
        @Setter
        private long initDeviceNumber = 10000;

        @Override
        public void run(String... strings) {
            DeviceProductInfo productInfo = new DeviceProductInfo();
            productInfo.setProtocol("jet-links");
            productInfo.setName("测试型号");
            productInfo.setId("test");
            DeviceProductOperation productOperation = registry.getProduct(productInfo.getId());
            productOperation.update(productInfo);
            productOperation.updateMetadata("{\n" +
                    "  \"id\": \"test-device\",\n" +
                    "  \"name\": \"测试设备\",\n" +
                    "  \"properties\": [\n" +
                    "    {\n" +
                    "      \"id\": \"name\",\n" +
                    "      \"name\": \"名称\",\n" +
                    "      \"valueType\": {\n" +
                    "        \"type\": \"string\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"id\": \"model\",\n" +
                    "      \"name\": \"型号\",\n" +
                    "      \"valueType\": {\n" +
                    "        \"type\": \"string\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"functions\": [\n" +
                    "    {\n" +
                    "      \"id\": \"playVoice\",\n" +
                    "      \"name\": \"播放声音\",\n" +
                    "      \"inputs\": [\n" +
                    "        {\n" +
                    "          \"id\": \"content\",\n" +
                    "          \"name\": \"内容\",\n" +
                    "          \"valueType\": {\n" +
                    "            \"type\": \"string\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"id\": \"times\",\n" +
                    "          \"name\": \"播放次数\",\n" +
                    "          \"valueType\": {\n" +
                    "            \"type\": \"int\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"id\": \"setColor\",\n" +
                    "      \"name\": \"灯光颜色\",\n" +
                    "      \"inputs\": [\n" +
                    "        {\n" +
                    "          \"id\": \"colorRgb\",\n" +
                    "          \"name\": \"颜色RGB值\",\n" +
                    "          \"valueType\": {\n" +
                    "            \"type\": \"string\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"events\": [\n" +
                    "    {\n" +
                    "      \"id\": \"temperature\",\n" +
                    "      \"name\": \"温度\",\n" +
                    "      \"parameters\": [\n" +
                    "        {\n" +
                    "          \"id\": \"temperature\",\n" +
                    "          \"valueType\": {\n" +
                    "            \"type\": \"int\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}");
            new Thread(() -> {
                long sum = initStartWith + initDeviceNumber;
                AtomicLong counter = new AtomicLong();
                Flux.<DeviceInfo>create(fluxSink -> {
                    //自动注册模拟设备
                    for (long i = initStartWith; i < sum; i++) {
                        DeviceInfo deviceInfo = new DeviceInfo();
                        deviceInfo.setId("test" + i);
                        deviceInfo.setProtocol("jet-links");
                        deviceInfo.setName("test");
                        deviceInfo.setProductId(productInfo.getId());

                        fluxSink.next(deviceInfo);
                    }
                    fluxSink.complete();
                }).buffer(1000)
                        .subscribeOn(Schedulers.parallel())
                        .subscribe(list -> CompletableFuture.runAsync(() -> {
                            for (DeviceInfo deviceInfo : list) {
                                DeviceOperation operation = registry.registry(deviceInfo);
                                Map<String, Object> all = new HashMap<>();
                                all.put("secureId", "test");
                                all.put("secureKey", "test");

                                operation.putAll(all);
                                counter.incrementAndGet();
                            }
                            log.info("batch registry device :{}", counter.get());
                        }));


                //注册20个子设备绑定到test0
                for (int i = 0; i < 20; i++) {
                    DeviceInfo deviceInfo = new DeviceInfo();
                    deviceInfo.setId("child" + i);
                    deviceInfo.setProtocol("jet-links");
                    deviceInfo.setName("test-child");
                    deviceInfo.setProductId(productInfo.getId());
                    deviceInfo.setParentDeviceId("test0");
                    registry.registry(deviceInfo);
                }
            }).start();

        }
    }

}
