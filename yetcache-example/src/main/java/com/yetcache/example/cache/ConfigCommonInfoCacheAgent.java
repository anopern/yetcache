package com.yetcache.example.cache;

import com.yetcache.core.cache.kv.MultiTierKVCache;
import com.yetcache.example.entity.ConfigCommonInfo;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public class ConfigCommonInfoCacheAgent extends BaseCacheAgent<String, ConfigCommonInfo> {
    @Override
    protected MultiTierKVCache<String, ConfigCommonInfo> doCreateCache() {
        return null;
    }

    @Override
    protected String getCacheName() {
        return "config-common-info-cache";
    }
}
