package com.yetcache.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
@ConfigurationProperties(prefix = "yetcache")
public class YetCacheProperties {
    private GlobalConfig global = new GlobalConfig();
    private CaffeineCacheConfig caffeine = new CaffeineCacheConfig();
    private RedisCacheConfig redis = new RedisCacheConfig();
    private SyncConfig sync = new SyncConfig();
    private PreloadConfig preload = new PreloadConfig();
    private RefreshConfig refresh = new RefreshConfig();
    protected CacheGroups caches = new CacheGroups();
}
