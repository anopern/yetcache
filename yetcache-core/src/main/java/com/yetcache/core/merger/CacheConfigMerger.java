package com.yetcache.core.merger;

import com.yetcache.core.config.GlobalConfig;
import com.yetcache.core.config.kv.KvCacheConfig;

/**
 * 工具类：用于合并全局配置与指定缓存配置
 *
 * @author chat
 */
public class CacheConfigMerger {
    public static KvCacheConfig merge(GlobalConfig global, KvCacheConfig raw) {
        if (global == null || global.getKv() == null) {
            return raw;
        }
        KvCacheConfig globalKv = global.getKv();
        return FieldMerger.mergeNonNullFields(globalKv, raw, 10);
    }
}

