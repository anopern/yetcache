package com.yetcache.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/5/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseHotKeyProviderProperties {
    protected static final String COMMON_CACHE_PREFIX = "common.cache-hkp.";

    // 是否在启动时是否从数据源加载热键
    private boolean loadOnStartup = true;

    // 热键缓存key
    private String cacheKeyPrefix;

    // 热键缓存过期时间
    private Integer redisExpireSecs = 3600 * 24 * 2;
}

