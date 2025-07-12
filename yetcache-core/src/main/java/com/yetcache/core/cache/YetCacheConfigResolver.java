package com.yetcache.core.cache;

import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
public class YetCacheConfigResolver {
    private final YetCacheProperties props;

    public YetCacheConfigResolver(YetCacheProperties props) {
        this.props = props;
    }

    public MultiTierFlatHashCacheConfig resolveFlatHash(String cacheName) {
        MultiTierFlatHashCacheConfig config = props.getCaches().getFlatHash().get(cacheName);
        if (config == null) {
            throw new IllegalArgumentException("未找到 FlatHash 结构 [" + cacheName + "] 的配置，请检查 yetcache.caches.flathash");
        }
        return config;
    }
}
