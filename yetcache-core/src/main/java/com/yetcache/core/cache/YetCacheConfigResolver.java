package com.yetcache.core.cache;

import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.kv.KvCacheConfig;
import com.yetcache.core.merger.CacheConfigMerger;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
public class YetCacheConfigResolver {

    private final YetCacheProperties props;

    public YetCacheConfigResolver(YetCacheProperties props) {
        this.props = props;
    }

    public KvCacheConfig resolveKv(String cacheName) {
        KvCacheConfig raw = props.getCaches().getKv().get(cacheName);
        if (raw == null) {
            throw new IllegalArgumentException("KV结构未配置: " + cacheName);
        }
        return CacheConfigMerger.merge(props.getGlobal(), raw);
    }
}