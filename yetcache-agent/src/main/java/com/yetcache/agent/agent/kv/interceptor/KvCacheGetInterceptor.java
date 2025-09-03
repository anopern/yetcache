package com.yetcache.agent.agent.kv.interceptor;

import com.yetcache.agent.agent.BehaviorType;
import com.yetcache.agent.agent.ChainKey;
import com.yetcache.agent.agent.StructureType;
import com.yetcache.agent.agent.kv.KvCacheAgentScope;
import com.yetcache.agent.agent.kv.loader.KvCacheLoadCommand;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.kv.command.KvCacheGetCommand;
import com.yetcache.core.result.*;
import com.yetcache.core.support.CacheValueHolder;
import com.yetcache.core.codec.TypeRef;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Slf4j
public class KvCacheGetInterceptor implements CacheInterceptor {
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public String id() {
        return "kv-cache-get-interceptor";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean supports(ChainKey chainKey) {
        return StructureType.KV.equals(chainKey.getStructureType())
                && BehaviorType.GET.equals(chainKey.getBehaviorType());
    }

    @Override
    public CacheResult invoke(CacheInvocationContext ctx, ChainRunner runner) throws Throwable {
        KvCacheAgentGetInvocationCommand cmd = (KvCacheAgentGetInvocationCommand) ctx.getCommand();
        Object bizKey = cmd.getBizKey();
        KvCacheAgentScope agentScope = (KvCacheAgentScope) ctx.getAgentScope();
        TypeRef<?> valueTypeRef = agentScope.getTypeDescriptor().getValueTypeRef();
        BaseCacheResult<?> cacheStoreResult = loadFromCacheStore(bizKey, agentScope, valueTypeRef);
        if (cacheStoreResult.code() == BaseResultCode.SUCCESS.code()) {
            if (cacheStoreResult.hitLevelInfo().hit() && cacheStoreResult.freshnessInfo().isFresh()) {
                CacheValueHolder<?> valueHolder = (CacheValueHolder<?>) cacheStoreResult.value();
                // 如果成功且新鲜，则直接返回
                return BaseCacheResult.singleHit(agentScope.getCacheAgentName(), valueHolder.getValue(),
                        cacheStoreResult.hitLevelInfo(), cacheStoreResult.freshnessInfo());
            } else {
                // 否则尝试从源加载数据
                CacheResult sourceResult = loadFromSource(bizKey, agentScope, valueTypeRef);
                if (sourceResult.code() == BaseResultCode.SUCCESS.code()) {
                    // 如果加载成功，则直接返回，不需要在这儿PUT
                    return BaseCacheResult.singleHit(agentScope.getCacheAgentName(), sourceResult.value(),
                            HitLevel.SOURCE);
                }
                // 加载源失败了，出现异常，则直接返回
                return BaseCacheResult.fail(agentScope.getCacheAgentName(), sourceResult.errorInfo());
            }
        }
        // 缓存加载失败了，出现异常，则直接返回
        return BaseCacheResult.fail(agentScope.getCacheAgentName(), cacheStoreResult.errorInfo());
    }

    @SuppressWarnings("unchecked")
    private BaseCacheResult<?> loadFromCacheStore(Object bizKey, KvCacheAgentScope agentScope, TypeRef<?> valueTypeRef) {
        String key = agentScope.getKeyConverter().convert(bizKey);
        KvCacheGetCommand storeGetCmd = KvCacheGetCommand.of(key, valueTypeRef);
        BaseCacheResult<?> storeResult = agentScope.getMultiLevelCache().get(storeGetCmd);
        if (storeResult.code() == BaseResultCode.SUCCESS.code()
                && HitLevel.NONE != storeResult.hitLevelInfo().hitLevel()
                && Freshness.FRESH == storeResult.freshnessInfo().getFreshness()) {
            return storeResult;
        }
        return BaseCacheResult.miss(agentScope.getCacheAgentName());
    }

    @SuppressWarnings("unchecked")
    private BaseCacheResult<?> loadFromSource(Object bizKey, KvCacheAgentScope agentScope, TypeRef<?> valueTypeRef) {
        try {
            lock.lock();
            // 再尝试一遍从CacheStore查询，非执行线程可能卡lock这儿了
            BaseCacheResult<?> storeResult = loadFromCacheStore(bizKey, agentScope, valueTypeRef);
            if (storeResult.isSuccess() && storeResult.hitLevelInfo().hitLevel() != HitLevel.NONE) {
                return storeResult;
            }
            KvCacheLoadCommand loadCmd = new KvCacheLoadCommand<>(bizKey);
            // 回源加载数据
            return agentScope.getCacheLoader().load(loadCmd);
        } finally {
            lock.unlock();
        }
    }
}
