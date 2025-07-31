package com.yetcache.agent.builder;

import com.yetcache.agent.core.structure.dynamichash.DynamicHashAgentScope;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheAgent;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheLoader;
import com.yetcache.core.cache.dynamichash.DefaultMultiTierDynamicHashCache;
import com.yetcache.core.cache.dynamichash.MultiTierDynamicHashCache;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
public class PlatformAgentBuilder {

    private final InterceptorRegistry interceptorRegistry;

    public PlatformAgentBuilder(CacheInterceptorRegistry registry) {
        this.interceptorRegistry = registry;
    }

    public <K, F, V> DynamicHashCacheAgent<K, F, V> buildAgent(DynamicHashCacheConfig config,
                                                               DynamicHashCacheLoader<K, F, V> loader) {
        DynamicHashAgentScope scope = new DynamicHashAgentScope(config, interceptorRegistry);
        MultiTierDynamicHashCache<K, F, V> cache = new DefaultMultiTierDynamicHashCache<>(config, loader);
        return new DefaultDynamicHashCacheAgent<>(scope, cache);
    }
}
