package com.yetcache.example.cache;

import com.yetcache.core.cache.loader.KVCacheLoader;
import com.yetcache.core.cache.kv.MultiTierKVCache;
import com.yetcache.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Component
public final class UserCacheAgent extends BaseCacheAgent<Long, User> {
    @Autowired
    @Qualifier("idKeyUserCacheLoader")
    private KVCacheLoader<Long, User> cacheLoader;

    @Override
    protected MultiTierKVCache<Long, User> doCreateCache() {
        return KVCacheManager.create(getCacheName(), rClient, cacheLoader);
    }

    @Override
    protected String getCacheName() {
        return "user-id-key-cache";
    }
}
