package com.yetcache.example.cache;

import com.yetcache.core.cache.manager.KVCacheManager;
import com.yetcache.core.cache.kv.MultiTierKVCache;
import lombok.Data;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author walter.yan
 * @since 2025/6/28
 */

@Data
public abstract class BaseCacheAgent {
    @Autowired
    protected KVCacheManager kVCacheManager;
    @Autowired
    protected RedissonClient rClient;

    @PostConstruct
    public void init() {
        createCache();
    }

    protected void createCache() {

    }
}
