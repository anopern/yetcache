package com.yetcache.example.cache.cnfig;

import com.yetcache.agent.agent.CacheAgentRegistryHub;
import com.yetcache.agent.broadcast.publisher.CacheInvalidateMessagePublisher;
import com.yetcache.agent.agent.CacheAgentPortRegistry;
import com.yetcache.agent.agent.kv.BaseKvCacheAgent;
import com.yetcache.agent.interceptor.CacheInvocationChainRegistry;
import com.yetcache.core.codec.JsonValueCodec;
import com.yetcache.core.codec.TypeDescriptor;
import com.yetcache.core.codec.TypeRef;
import com.yetcache.core.codec.TypeRefRegistry;
import com.yetcache.core.config.kv.KvCacheConfig;
import com.yetcache.core.support.key.LongKeyConverter;
import com.yetcache.example.config.CacheAgentNames;
import com.yetcache.example.domain.entity.User;
import com.yetcache.example.service.loader.IdKeyUserCacheLoader;
import com.yetcache.starter.YetCacheConfigResolver;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
@Configuration
public class UserCacheAgentConfiguration {

    @Qualifier("userIdKeyConverter")
    @Bean
    public LongKeyConverter userIdKeyConverter(YetCacheConfigResolver configResolver) {
        KvCacheConfig config = configResolver.resolveKv(CacheAgentNames.ID_KEY_USER_CACHE);
        return new LongKeyConverter(config.getSpec().getKeyPrefix(), config.getSpec().getUseHashTag());
    }

    @Qualifier("idKeyUserCacheAgent")
    @Bean
    public BaseKvCacheAgent idKeyUserCacheAgent(YetCacheConfigResolver configResolver,
                                                IdKeyUserCacheLoader cacheLoader,
                                                RedissonClient redissonClient,
                                                LongKeyConverter keyConverter,
                                                CacheInvalidateMessagePublisher broadcastPublisher,
                                                CacheInvocationChainRegistry chainRegistry,
                                                TypeRefRegistry typeRefRegistry,
                                                JsonValueCodec jsonValueCodec,
                                                CacheAgentPortRegistry agentPortRegistry,
                                                CacheAgentRegistryHub agentRegistryHub) {
        final String cacheAgentName = CacheAgentNames.ID_KEY_USER_CACHE;
        KvCacheConfig config = configResolver.resolveKv(cacheAgentName);
        TypeDescriptor typeDescriptor = TypeDescriptor.of(new TypeRef<User>() {
        });

        BaseKvCacheAgent agent = new BaseKvCacheAgent(
                cacheAgentName,
                config,
                redissonClient,
                keyConverter,
                cacheLoader,
                broadcastPublisher,
                chainRegistry,
                typeRefRegistry,
                typeDescriptor,
                jsonValueCodec,
                agentPortRegistry
        );
        agentRegistryHub.register(agent);
        return agent;
    }
}
