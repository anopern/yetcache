package com.yetcache.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.yetcache.agent.broadcast.BroadcastQueueInitializer;
import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.broadcast.publisher.DefaultRabbitmqCacheBroadcastPublisher;
import com.yetcache.agent.broadcast.receiver.CacheBroadcastReceiver;
import com.yetcache.agent.broadcast.receiver.RabbitMqCacheBroadcastReceiver;
import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandlerRegistry;
import com.yetcache.agent.broadcast.receiver.handler.KvCacheAgentRemoveLocalHandler;
import com.yetcache.agent.core.BehaviorType;
import com.yetcache.agent.core.StructureBehaviorKey;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.port.CacheAgentPortRegistry;
import com.yetcache.agent.governance.plugin.MetricsInterceptor;
import com.yetcache.agent.interceptor.*;
import com.yetcache.agent.regitry.CacheAgentRegistryHub;
import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.codec.JsonValueCodec;
import com.yetcache.core.codec.jackson.JacksonJsonValueCodec;
import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.broadcast.RabbitMqConfig;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author walter.yan
 * @since 2025/8/7
 */
@Configuration
@EnableConfigurationProperties(YetCacheProperties.class)
public class YetcacheAgentConfiguration {
    @Bean
    public CacheAgentRegistryHub cacheAgentRegistryHub() {
        return new CacheAgentRegistryHub();
    }

    @Bean
    public CacheAgentPortRegistry cacheAgentPortRegistry() {
        return new CacheAgentPortRegistry();
    }

    @Bean
    public YetCacheConfigResolver yetCacheConfigResolver(YetCacheProperties props) {
        return new YetCacheConfigResolver(props);
    }

    @Bean
    public String broadcastQueueName(YetCacheProperties yetCacheProperties,
                                     ConnectionFactory springConnectionFactory) throws IOException {
        RabbitMqConfig config = yetCacheProperties.getBroadcast().getRabbitmq();

        // 获取底层 RabbitMQ Connection
        Connection rabbitConnection = springConnectionFactory.createConnection().getDelegate();
        assert rabbitConnection != null;
        Channel channel = rabbitConnection.createChannel();
        return BroadcastQueueInitializer.init(channel, config);
    }

    @Bean
    public CacheBroadcastHandlerRegistry cacheBroadcastHandlerRegistry() {
        return new CacheBroadcastHandlerRegistry();
    }

    @Bean
    public CacheBroadcastPublisher cacheBroadcastSender(RabbitTemplate rabbitTemplate,
                                                        YetCacheProperties yetCacheProperties) {
        RabbitMqConfig config = yetCacheProperties.getBroadcast().getRabbitmq();
        return new DefaultRabbitmqCacheBroadcastPublisher(config, rabbitTemplate);
    }

    @Bean
    public CacheBroadcastReceiver cacheBroadcastReceiver(
            JsonValueCodec jsonValueCodec,
            CacheBroadcastHandlerRegistry handlerRegistry) {
        return new RabbitMqCacheBroadcastReceiver(jsonValueCodec, handlerRegistry);
    }

    @Bean
    public KvCacheAgentRemoveLocalHandler kvCacheAgentRemoveLocalHandler(
            CacheBroadcastHandlerRegistry handlerRegistry,
            CacheAgentPortRegistry cacheAgentPortRegistry) {

        KvCacheAgentRemoveLocalHandler handler = new KvCacheAgentRemoveLocalHandler(cacheAgentPortRegistry);
        handlerRegistry.register(handler);
        return handler;
    }

    @Bean
    public CacheInvocationInterceptorRegistry cacheInvocationInterceptorRegistry() {
        return new CacheInvocationInterceptorRegistry();
    }

    @Bean
    public CacheInvocationChainBuilder cacheInvocationChainBuilder(CacheInvocationInterceptorRegistry interceptorRegistry) {
        return new CacheInvocationChainBuilder(interceptorRegistry);
    }

    @Bean
    public CacheInvocationChainRegistry cacheInvocationChainRegistry() {
        return new CacheInvocationChainRegistry();
    }


    @Bean
    public MetricsInterceptor metricsInterceptor(
            CacheInvocationInterceptorRegistry interceptorRegistry,
            MeterRegistry registry) {
        MetricsInterceptor interceptor = new MetricsInterceptor(registry);
        interceptorRegistry.register(interceptor);
        return interceptor;
    }

    @Bean
    public JsonValueCodec jsonValueCodec(ObjectMapper objectMapper) {
        return new JacksonJsonValueCodec(objectMapper);
    }

    @Bean
    public ApplicationRunner registerDefaultChains(CacheInvocationChainRegistry registry,
                                                   CacheInvocationChainBuilder chainBuilder) {
        return args -> {
            StructureBehaviorKey dhGetSb = StructureBehaviorKey.of(StructureType.HASH, BehaviorType.GET);
            StructureBehaviorKey dhBatchGetSb = StructureBehaviorKey.of(StructureType.HASH, BehaviorType.BATCH_GET);
            CacheInvocationChain dhGetChain = chainBuilder.build(dhGetSb);
            CacheInvocationChain dhBatchGetChain = chainBuilder.build(dhBatchGetSb);
            registry.register(dhGetSb, dhGetChain);
            registry.register(dhBatchGetSb, dhBatchGetChain);
        };
    }
}
