package com.yetcache.agent.interceptor;

import com.yetcache.agent.agent.StructureBehaviorKey;

import java.util.*;

/**
 * @author walter.yan
 * @since 2025/7/29
 */
public class CacheInvocationChainRegistry {
    private final Map<StructureBehaviorKey, CacheInvocationChain> chainMap = new HashMap<>();

    public void register(StructureBehaviorKey key, CacheInvocationChain chain) {
        chainMap.put(key, chain);
    }

    public CacheInvocationChain getChain(StructureBehaviorKey key) {
        return chainMap.get(key);
    }
}
