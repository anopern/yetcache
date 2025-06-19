package com.yetcache.embedded;

import com.yetcache.core.AbstractKVCacheBuilder;
import com.yetcache.core.KVCache;

/**
 * @author walter.yan
 * @since 2025/6/19
 */
public class CaffeineKVCacheBuilder extends AbstractKVCacheBuilder<CaffeineKVCacheBuilder> {

    public static CaffeineKVCacheBuilder newBuilder() {
        return new CaffeineKVCacheBuilder();
    }

    public T limit()
}
