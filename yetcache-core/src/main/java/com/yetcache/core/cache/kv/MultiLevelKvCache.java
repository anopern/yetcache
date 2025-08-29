package com.yetcache.core.cache.kv;


import com.yetcache.core.cache.command.kv.KvCacheGetCommand;
import com.yetcache.core.cache.command.kv.KvCachePutCommand;
import com.yetcache.core.cache.command.kv.KvCacheRemoveCommand;
import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public interface MultiLevelKvCache {
    <T> CacheResult get(KvCacheGetCommand cmd);

    <T> CacheResult put(KvCachePutCommand cmd);

    CacheResult remove(KvCacheRemoveCommand cmd);
}
