package com.yetcache.core.cache;

import com.yetcache.core.config.YetCacheProperties;
import com.yetcache.core.config.dynamichash.HashCacheConfig;
import com.yetcache.core.config.flathash.FlatHashCacheConfig;
import com.yetcache.core.config.kv.MultiLevelKVCacheConfig;
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

    public MultiLevelKVCacheConfig resolveKV(String cacheName) {
        MultiLevelKVCacheConfig raw = props.getCaches().getKv().get(cacheName);
        if (raw == null) {
            throw new IllegalArgumentException("KV结构未配置: " + cacheName);
        }
        return CacheConfigMerger.merge(props.getGlobal(), raw);
    }

    public FlatHashCacheConfig resolveFlatHash(String cacheName) {
        FlatHashCacheConfig raw = props.getCaches().getFlatHash().get(cacheName);
        if (raw == null) {
            throw new IllegalArgumentException("FlatHash结构未配置: " + cacheName);
        }
        return CacheConfigMerger.merge(props.getGlobal(), raw);
    }

    public HashCacheConfig resolveHash(String cacheName) {
        HashCacheConfig raw = props.getCaches().getDynamicHash().get(cacheName);
        if (raw == null) {
            throw new IllegalArgumentException("DynamicHash结构未配置: " + cacheName);
        }
        return CacheConfigMerger.merge(props.getGlobal(), raw);
    }
}