package com.yetcache.core.cache.flathash;


import com.yetcache.core.result.FlatHashStorageResult;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/7
 */
public interface MultiTierFlatHashCache<F, V> {

    FlatHashStorageResult<F, V> listAll();

    FlatHashStorageResult<F, V> putAll(Map<F, V> map);
}
