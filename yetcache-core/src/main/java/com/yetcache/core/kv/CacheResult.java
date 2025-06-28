package com.yetcache.core.kv;

import com.yetcache.core.CacheAccessStatus;
import com.yetcache.core.CacheTier;
import com.yetcache.core.CacheValueHolder;
import com.yetcache.core.SourceLoadStatus;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
@Data
public class CacheResult<K, V> {
    protected String cacheName;
    protected CacheTier cacheTier;
    protected K bizKey;
    protected String key;
    protected CacheValueHolder<V> valueHolder;
    protected CacheAccessStatus localStatus;
    protected CacheAccessStatus remoteStatus;
    protected SourceLoadStatus loadStatus;
    protected Exception exception;
}
