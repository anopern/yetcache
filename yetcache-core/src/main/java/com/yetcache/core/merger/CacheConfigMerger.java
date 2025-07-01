package com.yetcache.core.merger;

import com.yetcache.core.config.*;
import com.yetcache.core.config.kv.CaffeineKVCacheConfig;
import com.yetcache.core.config.kv.MultiTierKVCacheConfig;
import com.yetcache.core.config.kv.RedisKVCacheConfig;
import com.yetcache.core.config.singlehash.CaffeineFlatHashCacheConfig;
import com.yetcache.core.config.singlehash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.config.singlehash.RedisFlatHashCacheConfig;

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

    public static CaffeineKVCacheConfig merge(CaffeineKVCacheConfig global, CaffeineKVCacheConfig spec) {
        Objects.requireNonNull(global, "Global CaffeineCacheConfig must not be null");

        if (spec == null) spec = new CaffeineKVCacheConfig();

        CaffeineKVCacheConfig result = new CaffeineKVCacheConfig();
        result.setTtlSecs(firstNonNull(spec.getTtlSecs(), global.getTtlSecs()));
        result.setMaxSize(firstNonNull(spec.getMaxSize(), global.getMaxSize()));
        result.setPenetrationProtect(merge(global.getPenetrationProtect(), spec.getPenetrationProtect()));

        return result;
    }

    public static RedisKVCacheConfig merge(RedisKVCacheConfig global, RedisKVCacheConfig spec) {
        Objects.requireNonNull(global, "Global RedisCacheConfig must not be null");

        if (spec == null) spec = new RedisKVCacheConfig();

        RedisKVCacheConfig result = new RedisKVCacheConfig();
        result.setTtlSecs(firstNonNull(spec.getTtlSecs(), global.getTtlSecs()));
        result.setPenetrationProtect(merge(global.getPenetrationProtect(), spec.getPenetrationProtect()));

        return result;
    }

    public static MultiTierKVCacheConfig merge(GlobalConfig global, MultiTierKVCacheConfig spec) {
        Objects.requireNonNull(global, "Global MultiTierCacheConfig must not be null");

        if (spec == null) spec = new MultiTierKVCacheConfig();

        MultiTierKVCacheConfig result = new MultiTierKVCacheConfig();
        result.setKeyPrefix(spec.getKeyPrefix());
        result.setUseHashTag(firstNonNull(spec.getUseHashTag(), global.getUseHashTag()));
        result.setTtlRandomPercent(firstNonNull(spec.getTtlRandomPercent(), global.getTtlRandomPercent()));
        result.setCacheTier(firstNonNull(spec.getCacheTier(), global.getCacheTier()));
        result.setTenantMode(firstNonNull(spec.getTenantMode(), global.getTenantMode()));

        result.setLocal(merge(global.getKvLocal(), spec.getLocal()));
        result.setRemote(merge(global.getKvRemote(), spec.getRemote()));

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

    public static CaffeineFlatHashCacheConfig merge(CaffeineFlatHashCacheConfig global, CaffeineFlatHashCacheConfig spec) {
        Objects.requireNonNull(global, "Global CaffeineCacheConfig must not be null");

        if (spec == null) spec = new CaffeineFlatHashCacheConfig();

        CaffeineFlatHashCacheConfig result = new CaffeineFlatHashCacheConfig();
        result.setTtlSecs(firstNonNull(spec.getTtlSecs(), global.getTtlSecs()));
        result.setMaxSize(firstNonNull(spec.getMaxSize(), global.getMaxSize()));
        result.setPenetrationProtect(merge(global.getPenetrationProtect(), spec.getPenetrationProtect()));

        return result;
    }

    public static RedisFlatHashCacheConfig merge(RedisFlatHashCacheConfig global, RedisFlatHashCacheConfig spec) {
        Objects.requireNonNull(global, "Global RedisCacheConfig must not be null");

        if (spec == null) spec = new RedisFlatHashCacheConfig();

        RedisFlatHashCacheConfig result = new RedisFlatHashCacheConfig();
        result.setTtlSecs(firstNonNull(spec.getTtlSecs(), global.getTtlSecs()));
        result.setPenetrationProtect(merge(global.getPenetrationProtect(), spec.getPenetrationProtect()));

        return result;
    }

    public static MultiTierFlatHashCacheConfig merge(GlobalConfig global, MultiTierFlatHashCacheConfig spec) {
        Objects.requireNonNull(global, "GlobalConfig must not be null");

        if (spec == null) spec = new MultiTierFlatHashCacheConfig();

        MultiTierFlatHashCacheConfig result = new MultiTierFlatHashCacheConfig();
        result.setKey(spec.getKey());
        result.setUseHashTag(firstNonNull(spec.getUseHashTag(), global.getUseHashTag()));
        result.setTtlRandomPercent(firstNonNull(spec.getTtlRandomPercent(), global.getTtlRandomPercent()));
        result.setCacheTier(firstNonNull(spec.getCacheTier(), global.getCacheTier()));
        result.setTenantMode(firstNonNull(spec.getTenantMode(), global.getTenantMode()));

        // Local + Remote 合并，重用 KVConfig 的结构
        result.setLocal(CacheConfigMerger.merge(global.getFlatHashLocal(), spec.getLocal()));
        result.setRemote(CacheConfigMerger.merge(global.getFlatHashRemote(), spec.getRemote()));

        return result;
    }


    private static <T> T firstNonNull(T primary, T fallback) {
        return primary != null ? primary : fallback;
    }
}