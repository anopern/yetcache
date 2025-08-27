package com.yetcache.agent.core.structure.kv.loader;

import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public interface KvCacheLoader<K> {

    String getLoaderName();

 CacheResult load(KvCacheLoadCommand<K> cmd);

  CacheResult batchLoad(KvCacheBatchLoadCommand<K> cmd);
}
