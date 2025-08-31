package com.yetcache.agent.agent.kv;

import com.yetcache.agent.agent.*;
import com.yetcache.agent.agent.kv.port.*;
import com.yetcache.agent.broadcast.CacheInvalidateCommand;
import com.yetcache.agent.broadcast.InstanceIdProvider;
import com.yetcache.agent.broadcast.publisher.CacheInvalidateMessagePublisher;
import com.yetcache.agent.agent.kv.interceptor.KvCacheAgentGetInvocationCommand;
import com.yetcache.agent.agent.kv.loader.KvCacheLoader;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.CacheTtl;
import com.yetcache.core.cache.kv.command.KvCachePutCommand;
import com.yetcache.core.cache.kv.command.KvCacheRemoveCommand;
import com.yetcache.core.cache.kv.DefaultMultiLevelKvCache;
import com.yetcache.core.codec.JsonValueCodec;
import com.yetcache.core.codec.TypeDescriptor;
import com.yetcache.core.codec.TypeRefRegistry;
import com.yetcache.core.config.CacheLevel;
import com.yetcache.core.config.kv.KvCacheConfig;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.core.result.CacheResult;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.util.TtlRandomizer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.concurrent.CompletableFuture;

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
                            KvCacheLoader cacheLoader,
                            CacheInvalidateMessagePublisher broadcastPublisher,
                            CacheInvocationChainRegistry chainRegistry,
                            TypeRefRegistry typeRefRegistry,
                            TypeDescriptor typeDescriptor,
                            JsonValueCodec jsonValueCodec,
                            CacheAgentPortRegistry agentPortRegistry) {

        DefaultMultiLevelKvCache multiLevelCache = new DefaultMultiLevelKvCache(cacheAgentName, config, redissonClient, keyConverter,
                jsonValueCodec);

        KvCacheAgentRemovePort removePort = new DefaultKvCacheAgentRemovePort(this);
        KvCacheAgentPutPort putPort = new DefaultKvCacheAgentPutPort(this);
        agentPortRegistry.register(cacheAgentName, BehaviorType.REMOVE, removePort);
        agentPortRegistry.register(cacheAgentName, BehaviorType.PUT, putPort);
        this.scope = new KvCacheAgentScope(cacheAgentName, multiLevelCache, config, keyConverter, cacheLoader,
                broadcastPublisher, removePort, putPort, typeDescriptor);

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
        ChainKey chainKey = ChainKey.of(StructureType.KV, BehaviorType.GET, scope.getCacheAgentName());
        CacheInvocationCommand cmd = KvCacheAgentGetInvocationCommand.of(scope.getCacheAgentName(), bizKey);
        return (BaseCacheResult<T>) singleInvoke(chainKey, cmd);
    }

    @Override
    public <K, T> BaseCacheResult<Void> put(K bizKey, T value) {
        log.debug("[Yetcache]BaseKvCacheAgent put data to multiLevelCache, bizKey: {}, value: {}", bizKey, value);
        CacheTtl ttl = CacheTtl.builder()
                .localLogicSecs(getLocalLogicTtlSecs())
                .remoteLogicSecs(getRemoteLogicTtlSecs())
                .remotePhysicalSecs(getRemotePhysicalTtlSecs())
                .build();
        KvCachePutCommand cmd = KvCachePutCommand.of(bizKey, value, ttl);
        BaseCacheResult<Void> putResult = scope.getMultiLevelCache().put(cmd);
        CompletableFuture.runAsync(() -> {
            @SuppressWarnings("unchecked")
            CacheInvalidateCommand removeCmd = CacheInvalidateCommand.builder()
                    .structureType(StructureType.KV.name())
                    .cacheAgentName(scope.getCacheAgentName())
                    .key(scope.getKeyConverter().convert(cmd.getBizKey()))
                    .instanceId(InstanceIdProvider.getInstanceId())
                    .publishAt(System.currentTimeMillis())
                    .build();
            log.debug("[Yetcache]BaseKvCacheAgent put method push remove command: {}", removeCmd);
            scope.getBroadcastPublisher().publish(removeCmd);
        });

        return putResult;
    }

    private Long getLocalLogicTtlSecs() {
        return TtlRandomizer.randomizeSecs(scope.getConfig().getLocal().getLogicTtlSecs(),
                scope.getConfig().getLocal().getTtlRandomPct());
    }

    private Long getRemoteLogicTtlSecs() {
        return TtlRandomizer.randomizeSecs(scope.getConfig().getRemote().getLogicTtlSecs(),
                scope.getConfig().getRemote().getTtlRandomPct());
    }

    private Long getRemotePhysicalTtlSecs() {
        return TtlRandomizer.randomizeSecs(scope.getConfig().getRemote().getPhysicalTtlSecs(),
                scope.getConfig().getRemote().getTtlRandomPct());
    }


    @Override
    public <K> BaseCacheResult<Void> remove(K bizKey) {
        return remove(bizKey, CacheAgentRemoveOptions.of(CacheLevel.BOTH));
    }

    @Override
    public <K> BaseCacheResult<Void> remove(K bizKey, CacheAgentRemoveOptions opts) {
        if (null == opts) {
            opts = CacheAgentRemoveOptions.of(CacheLevel.BOTH);
        }
        KvCacheRemoveCommand cmd = KvCacheRemoveCommand.of(bizKey, opts.getCacheLevel());
        scope.getMultiLevelCache().remove(cmd);
        return BaseCacheResult.success(scope.getCacheAgentName());
    }

    protected <K> BaseCacheResult<Void> removeLocal(K bizKey) {
        KvCacheRemoveCommand cmd = KvCacheRemoveCommand.ofLocal(bizKey);
        scope.getMultiLevelCache().remove(cmd);
        return BaseCacheResult.success(scope.getCacheAgentName());
    }

    private CacheResult singleInvoke(ChainKey chainKey, CacheInvocationCommand command) {
        CacheInvocationContext ctx = new CacheInvocationContext(command, scope);
        try {
            CacheInvocationChain chain = chainRegistry.getChain(chainKey);
            return chain.proceed(ctx);
        } catch (Throwable e) {
            log.error("[Yetcache]BaseKvCacheAgent singleInvoke failed", e);
            return BaseCacheResult.fail(scope.getCacheAgentName(), e);
        }
    }

    @Override
    public String cacheAgentName() {
        return this.scope.getCacheAgentName();
    }
}
