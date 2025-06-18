package com.yetcache.core;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public class EmbeddedCacheConfig<K, V> extends CacheConfig<K, V> {
    private int limit = CacheConstants.DEFAULT_LOCAL_LIMIT;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
