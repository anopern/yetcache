package com.yetcache.agent.interceptor;

import com.yetcache.agent.agent.ChainKey;

import java.util.*;

/**
 * @author walter.yan
 * @since 2025/7/29
 */
public class CacheInvocationChainRegistry {
    private final Map<ChainKey, CacheInvocationChain> chainMap = new HashMap<>();

    public void register(ChainKey key, CacheInvocationChain chain) {
        chainMap.put(key, chain);
    }

    public CacheInvocationChain getChain(ChainKey chainKey) {
        return chainMap.get(chainKey);
    }
}
