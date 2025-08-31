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
        Object key = cmd.getBizKey();
        KvCacheAgentScope agentScope = (KvCacheAgentScope) ctx.getAgentScope();
        TypeRef<?> valueTypeRef = agentScope.getTypeDescriptor().getValueTypeRef();
        BaseCacheResult<?> loadFromCacheStore = loadFromCacheStore(key, agentScope, valueTypeRef);
        if (loadFromCacheStore.code() != BaseResultCode.SUCCESS.code()
                || loadFromCacheStore.hitLevelInfo().hitLevel() != HitLevel.NONE) {
            return loadFromCacheStore;
        }
        CacheResult sourceResult = loadFromSource(key, agentScope, valueTypeRef);
        log.debug("[Yetcache]load data from source, key:{}, result:{}", key, sourceResult);
        return sourceResult;
    }

    private BaseCacheResult<?> loadFromCacheStore(Object bizKey, KvCacheAgentScope agentScope, TypeRef<?> valueTypeRef) {
        KvCacheGetCommand storeGetCmd = KvCacheGetCommand.of(bizKey, valueTypeRef);
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
        String cacheAgentName = agentScope.getCacheAgentName();
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
