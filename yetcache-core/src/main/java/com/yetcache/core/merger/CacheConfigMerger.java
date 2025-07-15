package com.yetcache.core.merger;

import com.yetcache.core.config.GlobalConfig;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.config.flathash.FlatHashCacheConfig;
import com.yetcache.core.config.kv.MultiTierKVCacheConfig;

/**
 * 工具类：用于合并全局配置与指定缓存配置
 *
 * @author chat
 */
public class CacheConfigMerger {
    public static MultiTierKVCacheConfig merge(GlobalConfig global, MultiTierKVCacheConfig raw) {
        if (global == null || global.getKv() == null) {
            return raw;
        }
        MultiTierKVCacheConfig globalKV = global.getKv();
        return FieldMerger.mergeNonNullFields(globalKV, raw, 10);
    }

    public static FlatHashCacheConfig merge(GlobalConfig global, FlatHashCacheConfig raw) {
        if (global == null || global.getKv() == null) {
            return raw;
        }
        FlatHashCacheConfig globalFlatHash = global.getFlatHash();
        return FieldMerger.mergeNonNullFields(globalFlatHash, raw, 10);
    }

    public static DynamicHashCacheConfig merge(GlobalConfig global, DynamicHashCacheConfig raw) {
        if (global == null || global.getKv() == null) {
            return raw;
        }
        DynamicHashCacheConfig globalDynamicHash = global.getDynamicHash();
        return FieldMerger.mergeNonNullFields(globalDynamicHash, raw, 10);
    }
}

