package com.yetcache.agent.core.structure.kv.loader;

import com.yetcache.core.result.CacheResult;

import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public interface KvCacheLoader<K, V> {
    CacheResult load(KvCacheLoadCommand<?> bizKey);


    Map<K, V> batchLoad(List<K> bizKeys);
}
