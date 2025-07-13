package com.yetcache.core.cache.flathash;


import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.result.FlatHashStorageResult;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/7
 */
public interface MultiTierFlatHashCache<F, V> {

    FlatHashStorageResult<F, V> getWithResult(F bizField);

    FlatHashStorageResult<F, V> listAllWithResult();

    FlatHashStorageResult<F, V> putAllWithResult(Map<F, V> map);
}
