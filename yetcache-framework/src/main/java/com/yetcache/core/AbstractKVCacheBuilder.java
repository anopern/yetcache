package com.yetcache.core;

/**
 * @author walter.yan
 * @since 2025/6/19
 */
public abstract class AbstractKVCacheBuilder<T extends AbstractKVCacheBuilder<T>> implements KVCacheBuilder {
    protected KVCacheConfig<?, ?> cacheConfig;
}
