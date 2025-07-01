package com.yetcache.example.cache;


import com.yetcache.core.cache.flathash.FlatHashCache;
import com.yetcache.example.entity.ConfigCommonInfo;
import com.yetcache.example.enums.EnumCaches;
import com.yetcache.example.service.loader.ConfigCommonInfoCacheLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@Component
@Slf4j
public class ConfigCommonInfoCacheAgent extends BaseFlatHashCacheAgent {
    @Autowired
    private ConfigCommonInfoCacheLoader cacheLoader;
    private FlatHashCache<String, ConfigCommonInfo> cache;

    @Override
    protected void createCache() {
        cache = flatHashCacheManager.create(EnumCaches.CONFIG_COMMON_INFO_CACHE.getName(), rClient, cacheLoader);
    }

    public ConfigCommonInfo getByCode(String code) {
        return cache.get(code);
    }
}
