package com.yetcache.core.config;

import com.yetcache.core.config.item.KVCacheItem;
import com.yetcache.core.config.item.MultiHashCacheItem;
import com.yetcache.core.config.item.SingleHashCacheItem;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class CacheGroups {
    protected Map<String, KVCacheItem> kv = new HashMap<>();
    protected Map<String, SingleHashCacheItem> singleHash = new HashMap<>();
    protected Map<String, MultiHashCacheItem> multiHash = new HashMap<>();
}
