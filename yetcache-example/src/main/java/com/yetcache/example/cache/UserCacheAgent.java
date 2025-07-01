package com.yetcache.example.cache;

import com.yetcache.core.cache.kv.MultiTierKVCache;
import com.yetcache.core.cache.loader.KVCacheLoader;
import com.yetcache.example.entity.User;
import com.yetcache.example.enums.EnumCaches;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Component
public final class UserCacheAgent extends BaseKVCacheAgent {
    @Autowired
    @Qualifier("idKeyUserCacheLoader")
    private KVCacheLoader<Long, User> idKeyCacheLoader;
    private MultiTierKVCache<Long, User> idKeyCache;

    @Override
    protected void createCache() {
        idKeyCache = kVCacheManager.create(EnumCaches.USER_ID_KEY_CACHE.getName(), rClient, idKeyCacheLoader, null);
    }

    public User get(Long id) {
        return idKeyCache.get(id);
    }
}
