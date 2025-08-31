package com.yetcache.agent.agent.kv.interceptor;

import com.yetcache.agent.agent.BehaviorType;
import com.yetcache.agent.agent.StructureBehaviorKey;
import com.yetcache.agent.agent.StructureType;
import com.yetcache.agent.agent.kv.KvCacheAgentScope;
import com.yetcache.agent.agent.kv.loader.KvCacheLoadCommand;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.kv.command.KvCacheGetCommand;
import com.yetcache.core.support.CacheValueHolder;
import com.yetcache.core.codec.TypeRef;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.core.result.BaseResultCode;
import com.yetcache.core.result.CacheResult;
import com.yetcache.core.result.HitLevel;
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
    public boolean supports(InterceptorSupportCriteria criteria) {
        StructureBehaviorKey sbKey = criteria.getSbKey();
        return StructureType.KV.equals(sbKey.getStructureType())
                && BehaviorType.GET.equals(sbKey.getBehaviorType());
    }

    @Override
    public CacheResult invoke(CacheInvocationContext ctx, ChainRunner runner) throws Throwable {
        KvCacheAgentGetInvocationCommand cmd = (KvCacheAgentGetInvocationCommand) ctx.getCommand();
        Object key = cmd.getBizKey();
        KvCacheAgentScope agentScope = (KvCacheAgentScope) ctx.getAgentScope();
        String cacheAgentName = agentScope.getCacheAgentName();
        try {
            TypeRef<?> valueTypeRef = agentScope.getTypeDescriptor().getValueTypeRef();
            BaseCacheResult<?> fromStore = loadFromCacheStore(key, agentScope, valueTypeRef);
            if (fromStore.isSuccess()) {
                if (fromStore.hitLevelInfo().hitLevel() == HitLevel.NONE) {
                    CacheResult sourceResult = loadFromSource(key, agentScope, valueTypeRef);
                    log.debug("[Yetcache]load data from source, key:{}, result:{}", key, sourceResult);
                    return sourceResult;
                } else {
                    return BaseCacheResult.singleHit(cacheAgentName, fromStore.value(), fromStore.hitLevelInfo());
                }
            } else {
                return BaseCacheResult.fail(cacheAgentName, fromStore.errorInfo());
            }
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}", cacheAgentName, key, e);
            return BaseCacheResult.fail(cacheAgentName, e);
        }
    }

    private BaseCacheResult<?> loadFromCacheStore(Object bizKey, KvCacheAgentScope agentScope, TypeRef<?> valueTypeRef) {
        KvCacheGetCommand storeGetCmd = KvCacheGetCommand.of(bizKey, valueTypeRef);
        CacheResult storeResult = agentScope.getMultiLevelCache().get(storeGetCmd);
        if (storeResult.code() == BaseResultCode.SUCCESS.code()
                && HitLevel.NONE != storeResult.hitLevelInfo().hitLevel()) {
            CacheValueHolder<?> holder = (CacheValueHolder<?>) storeResult.value();
            if (holder.isNotLogicExpired()) {
                return BaseCacheResult.singleHit(agentScope.getCacheAgentName(), holder, storeResult.hitLevelInfo());
            }
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
            CacheResult loadResult = agentScope.getCacheLoader().load(loadCmd);
            if (!loadResult.isSuccess()) {
                return BaseCacheResult.fail(cacheAgentName, loadResult.errorInfo());
            }
            if (loadResult.isSuccess() && null == loadResult.value()) {
                // 没有查询到数据，miss了
                return BaseCacheResult.miss(cacheAgentName);
            }
            return BaseCacheResult.singleHit(cacheAgentName, CacheValueHolder.wrap(loadResult.value(), 0),
                    HitLevel.SOURCE);
        } catch (Exception e) {
            log.warn("[Yetcache]cache load failed, agent = {}, key = {}", cacheAgentName, bizKey, e);
            return BaseCacheResult.fail(cacheAgentName, e);
        } finally {
            lock.unlock();
        }
    }
}
