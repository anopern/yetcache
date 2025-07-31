package com.yetcache.agent.interceptor;

import com.yetcache.core.result.Result;

import java.util.*;

/**
 * @author walter.yan
 * @since 2025/7/29
 */
public class CacheInvocationChainRegistry {
    private final Map<StructureBehaviorKey, CacheInvocationChain<? extends CacheInvocationContext, ?, ? extends Result<?>>>
            chainMap = new HashMap<>();

    public void register(StructureBehaviorKey key, CacheInvocationChain<?, ?, ?> chain) {
        chainMap.put(key, chain);
    }

    @SuppressWarnings("unchecked")
    public <C extends CacheInvocationContext, T, R extends Result<T>> CacheInvocationChain<C, T, R>
    getChain(StructureBehaviorKey key) {
        return (CacheInvocationChain<C, T, R>) chainMap.get(key);
    }
}
