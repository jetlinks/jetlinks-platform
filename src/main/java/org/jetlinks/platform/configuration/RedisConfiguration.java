package org.jetlinks.platform.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LettuceRedisProperties.class)
@AutoConfigureAfter(ExecutorConfiguration.class)
public class RedisConfiguration {


//    @Bean
//    public LettucePlus lettucePlus(ScheduledExecutorService executorService,
//                                   LettuceRedisProperties properties) {
//
//        int threadSize = properties.getThreadSize();
//
//        Class<? extends EventExecutorGroup> groupClass = NioEventLoopGroup.class;
//
//        if (EpollProvider.isAvailable()) {
//            groupClass = EpollEventLoopGroup.class;
//        } else if (KqueueProvider.isAvailable()) {
//            groupClass = KQueueEventLoopGroup.class;
//        }
//        EventExecutorGroup eventExecutors = DefaultEventLoopGroupProvider.createEventLoopGroup(groupClass, properties.getThreadSize());
//
//        DefaultClientResources resources = DefaultClientResources.builder()
//                .ioThreadPoolSize(threadSize)
//                .eventLoopGroupProvider(new DefaultEventLoopGroupProvider(threadSize))
//                .eventExecutorGroup(eventExecutors)
//                .computationThreadPoolSize(threadSize)
//                .build();
//
//        FSTConfiguration def = FSTConfiguration.createDefaultConfiguration();
//        def.setClassLoader(this.getClass().getClassLoader());
//        def.setForceSerializable(true);
//        RedisCodec<Object, Object> codec = new FstCodec<>(def);
//
//        return properties.getType().create(resources, codec, executorService, properties);
//    }

}
