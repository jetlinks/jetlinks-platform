package org.jetlinks.platform.configuration;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.resource.ClientResources;
import lombok.Getter;
import lombok.Setter;
import org.jetlinks.lettuce.LettucePlus;
import org.jetlinks.lettuce.codec.StringKeyCodec;
import org.jetlinks.lettuce.supports.DefaultLettucePlus;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;

@ConfigurationProperties(prefix = "jetlinks.redis")
@Getter
@Setter
public class LettuceRedisProperties {

    private String[] hosts = {"redis://127.0.0.1:6379"};

    private String password;

    private String clientName;

    private String masterId = "mymaster";

    private int database = 0;

    private int threadSize = Runtime.getRuntime().availableProcessors() * 2;

    private int poolSize = Runtime.getRuntime().availableProcessors() * 2;

    private int pubsubPoolSize = Runtime.getRuntime().availableProcessors();

    private Type type = Type.standalone;

    private void initRedisUri(RedisURI.Builder builder) {
        builder.withDatabase(database);
        if (null != password) {
            builder.withPassword(password);
        }
        if (null != clientName) {
            builder.withClientName(clientName);
        }
    }


    public enum Type {
        sentinel {
            @Override
            LettucePlus create(ClientResources resources, RedisCodec<Object, Object> codec, ScheduledExecutorService executorService, LettuceRedisProperties properties) {

                RedisURI.Builder redisURI = RedisURI.builder();
                properties.initRedisUri(redisURI);
                for (String host : properties.hosts) {
                    URI url = URI.create(host);
                    redisURI.withSentinel(url.getHost(), url.getPort());
                    redisURI.withDatabase(properties.database);
                }
                redisURI.withSentinelMasterId(properties.masterId);

                RedisClient client = RedisClient.create(resources, redisURI.build());
                client.setOptions(ClientOptions.builder()
                        .autoReconnect(true)
                        .build());

                DefaultLettucePlus plus = new DefaultLettucePlus(client);
                plus.setExecutorService(executorService);
                plus.setPoolSize(plus.getPoolSize());
                plus.setPubsubSize(plus.getPubsubSize());
                plus.setDefaultCodec(new StringKeyCodec<>(codec));
                plus.init();
                plus.initSentinel(redisURI.build());
                return plus;
            }
        },
        standalone {
            @Override
            LettucePlus create(ClientResources resources, RedisCodec<Object, Object> codec, ScheduledExecutorService executorService, LettuceRedisProperties properties) {
                URI url = URI.create(properties.hosts[0]);

                RedisURI.Builder redisURI = RedisURI.builder();
                redisURI.withHost(url.getHost());
                redisURI.withPort(url.getPort());
                properties.initRedisUri(redisURI);

                RedisClient client = RedisClient.create(resources, redisURI.build());
                client.setOptions(ClientOptions.builder()
                        .autoReconnect(true)
                        .build());
                DefaultLettucePlus plus = new DefaultLettucePlus(client);
                plus.setExecutorService(executorService);
                plus.setPoolSize(properties.poolSize);
                plus.setPubsubSize(properties.pubsubPoolSize);
                plus.setDefaultCodec(new StringKeyCodec<>(codec));
                plus.init();
                plus.initStandalone();
                return plus;
            }
        };

        abstract LettucePlus create(ClientResources resources, RedisCodec<Object, Object> codec, ScheduledExecutorService executorService, LettuceRedisProperties properties);
    }
}
