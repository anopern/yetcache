package com.yetcache.core.util;

import com.yetcache.core.config.*;

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

        if (spec == null) spec = new CaffeineCacheConfig();

        CaffeineCacheConfig result = new CaffeineCacheConfig();
        result.setTtlSecs(firstNonNull(spec.getTtlSecs(), global.getTtlSecs()));
        result.setMaxSize(firstNonNull(spec.getMaxSize(), global.getMaxSize()));
        result.setPenetrationProtect(merge(global.getPenetrationProtect(), spec.getPenetrationProtect()));

        return result;
    }

    public static RedisCacheConfig merge(RedisCacheConfig global, RedisCacheConfig spec) {
        Objects.requireNonNull(global, "Global RedisCacheConfig must not be null");

        if (spec == null) spec = new RedisCacheConfig();

        RedisCacheConfig result = new RedisCacheConfig();
        result.setTtlSecs(firstNonNull(spec.getTtlSecs(), global.getTtlSecs()));
        result.setPenetrationProtect(merge(global.getPenetrationProtect(), spec.getPenetrationProtect()));

        return result;
    }

    public static MultiTierCacheConfig merge(GlobalConfig global, MultiTierCacheConfig spec) {
        Objects.requireNonNull(global, "Global MultiTierCacheConfig must not be null");

        if (spec == null) spec = new MultiTierCacheConfig();

        MultiTierCacheConfig result = new MultiTierCacheConfig();
        result.setKeyPrefix(spec.getKeyPrefix());
        result.setUseHashTag(firstNonNull(spec.getUseHashTag(), global.getUseHashTag()));
        result.setTtlRandomPercent(firstNonNull(spec.getTtlRandomPercent(), global.getTtlRandomPercent()));
        result.setCacheTier(firstNonNull(spec.getCacheTier(), global.getCacheTier()));
        result.setTenantMode(firstNonNull(spec.getTenantMode(), global.getTenantMode()));

        result.setLocal(merge(global.getLocal(), spec.getLocal()));
        result.setRemote(merge(global.getRemote(), spec.getRemote()));

        return result;
    }

    public static PenetrationProtectConfig merge(PenetrationProtectConfig global, PenetrationProtectConfig spec) {
        if (global == null && spec == null) return null;
        if (global == null) return spec;
        if (spec == null) return global;

        PenetrationProtectConfig result = new PenetrationProtectConfig();
        result.setPrefix(firstNonNull(spec.getPrefix(), global.getPrefix()));
        result.setEnabled(firstNonNull(spec.getEnabled(), global.getEnabled()));
        result.setTtlSecs(firstNonNull(spec.getTtlSecs(), global.getTtlSecs()));
        result.setMaxSize(firstNonNull(spec.getMaxSize(), global.getMaxSize()));

        return result;
    }

    private static <T> T firstNonNull(T primary, T fallback) {
        return primary != null ? primary : fallback;
    }
}