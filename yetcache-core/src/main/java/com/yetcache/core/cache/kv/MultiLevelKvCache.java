package com.yetcache.core.cache.kv;


import com.yetcache.core.cache.kv.command.KvCacheGetCommand;
import com.yetcache.core.cache.kv.command.KvCachePutCommand;
import com.yetcache.core.cache.kv.command.KvCacheRemoveCommand;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.core.result.CacheResult;
import com.yetcache.core.support.CacheValueHolder;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public interface MultiLevelKvCache {
    <T> BaseCacheResult<CacheValueHolder<T>> get(KvCacheGetCommand cmd);

    <T> BaseCacheResult<Void> put(KvCachePutCommand cmd);

    BaseCacheResult<Void> remove(KvCacheRemoveCommand cmd);
}
