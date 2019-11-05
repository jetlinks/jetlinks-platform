package org.jetlinks.platform.configuration;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.mqtt.MqttServerOptions;
import lombok.extern.slf4j.Slf4j;
import org.jetlinks.gateway.vertx.mqtt.VertxMqttGatewayServerContext;
import org.jetlinks.platform.gateway.VerticleSupplier;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Configuration
public class DeviceGatewayConfiguration {

    @Bean(destroyMethod = "shutdown")
    public VertxMqttGatewayServerContext vertxMqttGatewayServerContext(){
        return new VertxMqttGatewayServerContext();
    }
    @Bean
    @ConfigurationProperties(prefix = "vertx")
    public VertxOptions vertxOptions() {
        return new VertxOptions();
    }

    @Bean
    @ConfigurationProperties(prefix = "vertx.mqtt")
    public MqttServerOptions mqttServerOptions() {
        return new MqttServerOptions();
    }

    @Bean
    public Vertx vertx(VertxOptions vertxOptions) {
        return Vertx.vertx(vertxOptions);
    }

    @Bean
    public VertxServerInitializer mqttServerInitializer() {
        return new VertxServerInitializer();
    }

    @Slf4j
    public static class VertxServerInitializer implements CommandLineRunner, DisposableBean, Ordered {

        @Autowired
        private List<VerticleSupplier> verticleList;

        @Autowired
        private Vertx vertx;

        @Override
        public void run(String... args) {
            for (VerticleSupplier supplier : verticleList) {
                DeploymentOptions options = new DeploymentOptions();
                options.setHa(true);
                options.setInstances(supplier.getInstances());
                vertx.deployVerticle(supplier, options, e -> {
                    if (!e.succeeded()) {
                        log.error("deploy verticle :{} error", supplier, e.cause());
                    } else {
                        log.debug("deploy verticle :{} success", supplier);
                    }
                });
            }
        }

        @Override
        public void destroy() throws Exception {
            log.info("close vertx");
            CountDownLatch latch = new CountDownLatch(1);
            vertx.close(result -> {
                log.info("close vertx done");
                latch.countDown();
            });
            latch.await(30, TimeUnit.SECONDS);
        }

        @Override
        public int getOrder() {
            return 100;
        }
    }
}
