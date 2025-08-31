package com.yetcache.starter;

import com.yetcache.core.config.CacheGroups;
import com.yetcache.core.config.GlobalConfig;
import com.yetcache.core.config.RedisPubSubConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author walter.yan
 * @since 2025/8/31
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "yetcache")
public class YetCacheProperties {
    private GlobalConfig global = new GlobalConfig();
    private RedisPubSubConfig pubSub = RedisPubSubConfig.defaultConfig();
    protected CacheGroups caches = new CacheGroups();
}