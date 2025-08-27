package com.yetcache.agent.core.structure.kv;

import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.port.CacheAgentPortRegistry;
import com.yetcache.agent.core.port.DefaultKvCacheAgentRemovePort;
import com.yetcache.agent.core.port.KvCacheAgentRemovePort;
import com.yetcache.agent.core.structure.kv.get.KvCacheAgentGetInvocationCommand;
import com.yetcache.agent.core.structure.kv.loader.KvCacheLoader;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.command.kv.KvCacheRemoveCommand;
import com.yetcache.core.cache.kv.MultiTierKvCache;
import com.yetcache.core.codec.JsonValueCodec;
import com.yetcache.core.codec.TypeDescriptor;
import com.yetcache.core.codec.TypeRefRegistry;
import com.yetcache.core.config.kv.KvCacheConfig;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.core.result.CacheResult;
import com.yetcache.core.support.key.KeyConverter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Slf4j
@Getter
public class BaseKvCacheAgent implements KvCacheAgent {
    private final KvCacheAgentScope scope;
    private final CacheInvocationChainRegistry chainRegistry;

    public BaseKvCacheAgent(String cacheAgentName,
                            KvCacheConfig config,
                            RedissonClient redissonClient,
                            KeyConverter keyConverter,
                            KvCacheLoader<?, ?> cacheLoader,
                            CacheBroadcastPublisher broadcastPublisher,
                            CacheInvocationChainRegistry chainRegistry,
                            TypeRefRegistry typeRefRegistry,
                            TypeDescriptor typeDescriptor,
                            JsonValueCodec jsonValueCodec,
                            CacheAgentPortRegistry agentPortRegistry) {

        MultiTierKvCache multiLevelCache = new MultiTierKvCache(cacheAgentName, config, redissonClient, keyConverter,
                jsonValueCodec);

        KvCacheAgentRemovePort removePort = new DefaultKvCacheAgentRemovePort(this);
        agentPortRegistry.register(cacheAgentName, BehaviorType.REMOVE, removePort);
        this.scope = new KvCacheAgentScope(cacheAgentName, multiLevelCache, config, keyConverter, cacheLoader,
                broadcastPublisher, removePort, typeDescriptor);

        String typeId = TypeDescriptor.toTypeId(typeDescriptor.getValueTypeRef());
        if (null == typeRefRegistry.get(typeId)) {
            typeRefRegistry.register(TypeDescriptor.toTypeId(typeDescriptor.getValueTypeRef()),
                    typeDescriptor.getValueTypeRef());
        }

        this.chainRegistry = chainRegistry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, T> BaseCacheResult<T> get(K bizKey) {
        StructureBehaviorKey structureBehaviorKey = StructureBehaviorKey.of(StructureType.KV, BehaviorType.GET);
        CacheInvocationCommand cmd = KvCacheAgentGetInvocationCommand.of(scope.getCacheAgentName(), bizKey);
        return (BaseCacheResult<T>) singleInvoke(structureBehaviorKey, cmd);
    }

    @Override
    public <K> BaseCacheResult<Void> remove(K bizKey) {
        KvCacheRemoveCommand cmd = KvCacheRemoveCommand.of(bizKey);
        scope.getMultiLevelCache().remove(cmd);
        return BaseCacheResult.success(scope.getCacheAgentName());
    }

    protected <K> BaseCacheResult<Void> removeLocal(K bizKey) {
        KvCacheRemoveCommand cmd = KvCacheRemoveCommand.ofLocal(bizKey);
        scope.getMultiLevelCache().remove(cmd);
        return BaseCacheResult.success(scope.getCacheAgentName());
    }

    private CacheResult singleInvoke(StructureBehaviorKey sbKey, CacheInvocationCommand command) {
        CacheInvocationContext ctx = new CacheInvocationContext(command, scope);
        try {
            CacheInvocationChain chain = chainRegistry.getChain(sbKey);
            CacheResult rawResult = chain.proceed(ctx);
            return BaseCacheResult.singleHit(scope.getCacheAgentName(), rawResult.value(),
                    rawResult.hitLevelInfo().hitLevel());
        } catch (Throwable e) {
            return BaseCacheResult.fail(scope.getCacheAgentName(), e);
        }
    }

    private CacheResult batchInvoke(StructureBehaviorKey structureBehaviorKey, CacheInvocationCommand command) {
        CacheInvocationContext ctx = new CacheInvocationContext(command, scope);
        try {
            CacheInvocationChain chain = chainRegistry.getChain(structureBehaviorKey);
            CacheResult rawResult = chain.proceed(ctx);
            return BaseCacheResult.batchHit(scope.getCacheAgentName(), rawResult.value(), rawResult.hitLevelInfo());
        } catch (Throwable e) {
            return BaseCacheResult.fail(scope.getCacheAgentName(), e);
        }
    }

    @Override
    public String cacheAgentName() {
        return this.scope.getCacheAgentName();
    }
}
