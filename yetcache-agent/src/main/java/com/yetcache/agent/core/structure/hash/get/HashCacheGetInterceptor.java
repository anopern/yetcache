package com.yetcache.agent.core.structure.hash.get;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.hash.HashAgentScope;
import com.yetcache.agent.core.structure.hash.HashCacheLoadCommand;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.command.hash.HashCacheGetCommand;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.codec.TypeRef;
import com.yetcache.core.result.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Slf4j
public class HashCacheGetInterceptor implements CacheInterceptor {
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public String id() {
        return "hash-cache-get-interceptor";
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
    public boolean supportStructureBehaviorKey(StructureBehaviorKey sbKey) {
        return StructureType.HASH.equals(sbKey.getStructureType())
                && BehaviorType.GET.equals(sbKey.getBehaviorType());
    }

    @Override
    public CacheResult invoke(CacheInvocationContext ctx, ChainRunner runner) throws Throwable {
        HashCacheAgentGetInvocationCommand cmd = (HashCacheAgentGetInvocationCommand) ctx.getCommand();
        Object bizKey = cmd.getBizKey();
        Object bizField = cmd.getBizField();
        HashAgentScope agentScope = (HashAgentScope) ctx.getAgentScope();
        String componentName = agentScope.getComponentName();
        try {
            TypeRef<?> valueTypeRef = agentScope.getTypeDescriptor().getValueTypeRef();
            BaseCacheResult<?> fromStore = getFromStore(bizKey, bizField, agentScope, valueTypeRef);
            if (fromStore.isSuccess()) {
                if (fromStore.hitLevelInfo().hitLevel() == HitLevel.NONE) {
                    return loadFromSource(bizKey, bizField, agentScope, valueTypeRef);
                } else {
                    return BaseCacheResult.singleHit(componentName, fromStore.value(), fromStore.hitLevelInfo());
                }
            }
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            return BaseCacheResult.fail(componentName, e);
        }
        return runner.proceed(ctx);
    }

    private BaseCacheResult<?> getFromStore(Object bizKey, Object bizField, HashAgentScope agentScope,
                                            TypeRef<?> valueTypeRef) {
        HashCacheGetCommand storeGetCmd = new HashCacheGetCommand(bizKey, bizField, valueTypeRef);
        CacheResult storeResult = agentScope.getMultiLevelCache().get(storeGetCmd);
        if (storeResult.code() == BaseResultCode.SUCCESS.code()
                && HitLevel.NONE != storeResult.hitLevelInfo().hitLevel()) {
            CacheValueHolder<?> holder = (CacheValueHolder<?>) storeResult.value();
            if (holder.isNotLogicExpired()) {
                return BaseCacheResult.singleHit(agentScope.getComponentName(), holder, storeResult.hitLevelInfo());
            }
        }
        return BaseCacheResult.miss(agentScope.getComponentName());
    }

    private BaseCacheResult<?> loadFromSource(Object bizKey, Object bizField, HashAgentScope agentScope,
                                              TypeRef<?> valueTypeRef) {
        String componentName = agentScope.getComponentName();
        try {
            lock.lock();
            BaseCacheResult<?> storeResult = getFromStore(bizKey, bizField, agentScope, valueTypeRef);
            if (storeResult.isSuccess() && storeResult.hitLevelInfo().hitLevel() != HitLevel.NONE) {
                return storeResult;
            }
            HashCacheLoadCommand<?, ?> loadCmd = new HashCacheLoadCommand<>(bizKey, bizField);
            // 回源加载数据
            CacheResult loadResult = agentScope.getCacheLoader().load(loadCmd);
            if (loadResult.isSuccess() && null == loadResult.value()) {
                return BaseCacheResult.miss(componentName);
            }
            Map<Object, Object> bizFieldValueNap = Collections.singletonMap(bizField, loadResult.value());
            // 封装为缓存值并写入缓存

            agentScope.getHashCacheFillPort().fillAndBroadcast(bizKey, bizFieldValueNap);

            return BaseCacheResult.singleHit(componentName, CacheValueHolder.wrap(loadResult.value(), 0),
                    HitLevel.SOURCE);
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            return BaseCacheResult.fail(componentName, e);
        } finally {
            lock.unlock();
        }
    }
}
