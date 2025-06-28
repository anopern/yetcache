package com.yetcache.core.util;

import com.yetcache.core.config.CaffeineCacheConfig;
import com.yetcache.core.config.GlobalConfig;
import com.yetcache.core.config.MultiTierCacheConfig;
import com.yetcache.core.config.RedisCacheConfig;

import java.util.Objects;

/**
 * 工具类：用于合并全局配置与指定缓存配置
 *
 * @author chat
 */
public final class CacheConfigMerger {

    private CacheConfigMerger() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static CaffeineCacheConfig merge(CaffeineCacheConfig global, CaffeineCacheConfig spec) {
        Objects.requireNonNull(global, "Global CaffeineCacheConfig must not be null");
        if (spec == null) return global;

        CaffeineCacheConfig result = new CaffeineCacheConfig();
        result.setTtlSecs(spec.getTtlSecs() != null ? spec.getTtlSecs() : global.getTtlSecs());
        result.setPenetrationProtectEnabled(
                spec.getPenetrationProtectEnabled() != null
                        ? spec.getPenetrationProtectEnabled()
                        : global.getPenetrationProtectEnabled()
        );
        result.setPenetrationProtectTtlSecs(
                spec.getPenetrationProtectTtlSecs() != null
                        ? spec.getPenetrationProtectTtlSecs()
                        : global.getPenetrationProtectTtlSecs()
        );
        result.setMaxSize(spec.getMaxSize() != null ? spec.getMaxSize() : global.getMaxSize());

        return result;
    }

    public static RedisCacheConfig merge(RedisCacheConfig global, RedisCacheConfig spec) {
        Objects.requireNonNull(global, "Global RedisCacheConfig must not be null");
        if (spec == null) return global;

        RedisCacheConfig result = new RedisCacheConfig();
        result.setTtlSecs(spec.getTtlSecs() != null ? spec.getTtlSecs() : global.getTtlSecs());
        result.setPenetrationProtectEnabled(
                spec.getPenetrationProtectEnabled() != null
                        ? spec.getPenetrationProtectEnabled()
                        : global.getPenetrationProtectEnabled()
        );
        result.setPenetrationProtectTtlSecs(
                spec.getPenetrationProtectTtlSecs() != null
                        ? spec.getPenetrationProtectTtlSecs()
                        : global.getPenetrationProtectTtlSecs()
        );

        return result;
    }

    public static MultiTierCacheConfig merge(GlobalConfig global, MultiTierCacheConfig spec) {
        Objects.requireNonNull(global, "Global MultiTierCacheConfig must not be null");

        MultiTierCacheConfig result = new MultiTierCacheConfig();
        result.setCacheTier(spec.getCacheTier() != null ? spec.getCacheTier() : global.getCacheTier());
        result.setTenantMode(spec.getTenantMode() != null ? spec.getTenantMode() : global.getTenantMode());

        // 层级配置合并
        result.setLocal(merge(global.getLocal(), spec.getLocal()));
        result.setRemote(merge(global.getRemote(), spec.getRemote()));

        return result;
    }
}
