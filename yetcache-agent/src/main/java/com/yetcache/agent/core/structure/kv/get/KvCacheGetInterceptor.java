package com.yetcache.agent.core.structure.kv.get;

import com.yetcache.agent.core.BehaviorType;
import com.yetcache.agent.core.StructureBehaviorKey;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.kv.KvCacheAgentScope;
import com.yetcache.agent.core.structure.kv.loader.KvCacheLoadCommand;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.command.kv.KvCacheGetCommand;
import com.yetcache.core.cache.support.CacheValueHolder;
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
    public boolean supports(StructureBehaviorKey sbKey) {
        return StructureType.KV.equals(sbKey.getStructureType())
                && BehaviorType.GET.equals(sbKey.getBehaviorType());
    }

    @Override
    public CacheResult invoke(CacheInvocationContext ctx, ChainRunner runner) throws Throwable {
        KvCacheAgentGetInvocationCommand cmd = (KvCacheAgentGetInvocationCommand) ctx.getCommand();
        Object bizKey = cmd.getBizKey();
        KvCacheAgentScope agentScope = (KvCacheAgentScope) ctx.getAgentScope();
        String cacheAgentName = agentScope.getCacheAgentName();
        try {
            TypeRef<?> valueTypeRef = agentScope.getTypeDescriptor().getValueTypeRef();
            BaseCacheResult<?> fromStore = getFromStore(bizKey, agentScope, valueTypeRef);
            if (fromStore.isSuccess()) {
                if (fromStore.hitLevelInfo().hitLevel() == HitLevel.NONE) {
                    return loadFromSource(bizKey, agentScope, valueTypeRef);
                } else {
                    return BaseCacheResult.singleHit(cacheAgentName, fromStore.value(), fromStore.hitLevelInfo());
                }
            }
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}", cacheAgentName, bizKey, e);
            return BaseCacheResult.fail(cacheAgentName, e);
        }
        return runner.proceed(ctx);
    }

    private BaseCacheResult<?> getFromStore(Object bizKey, KvCacheAgentScope agentScope, TypeRef<?> valueTypeRef) {
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

    private BaseCacheResult<?> loadFromSource(Object bizKey, KvCacheAgentScope agentScope, TypeRef<?> valueTypeRef) {
        String cacheAgentName = agentScope.getCacheAgentName();
        try {
            lock.lock();
            BaseCacheResult<?> storeResult = getFromStore(bizKey, agentScope, valueTypeRef);
            if (storeResult.isSuccess() && storeResult.hitLevelInfo().hitLevel() != HitLevel.NONE) {
                return storeResult;
            }
            KvCacheLoadCommand loadCmd = new KvCacheLoadCommand<>(bizKey);
            // 回源加载数据
            CacheResult loadResult = agentScope.getCacheLoader().load(loadCmd);
            if (loadResult.isSuccess() && null == loadResult.value()) {
                return BaseCacheResult.miss(cacheAgentName);
            }
            agentScope.getCachePutPort().put(bizKey, loadResult.value());
            return BaseCacheResult.singleHit(cacheAgentName, CacheValueHolder.wrap(loadResult.value(), 0),
                    HitLevel.SOURCE);
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}", cacheAgentName, bizKey, e);
            return BaseCacheResult.fail(cacheAgentName, e);
        } finally {
            lock.unlock();
        }
    }
}
