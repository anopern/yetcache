package com.yetcache.example.cache;

import com.yetcache.example.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Component
public final class UserCacheAgent extends BaseCacheAgent<Long, User> {

    @Override
    protected void createCache() {
        cacheManager.create(getCacheName());
    }

    @Override
    protected String getCacheName() {
        return "user-id-key-cache";
    }
}
