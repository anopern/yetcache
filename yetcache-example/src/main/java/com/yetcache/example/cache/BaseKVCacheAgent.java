package com.yetcache.example.cache;

import com.yetcache.core.cache.manager.KVCacheManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author walter.yan
 * @since 2025/6/28
 */

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseKVCacheAgent extends BaseCacheAgent {
    @Autowired
    protected KVCacheManager kVCacheManager;

}
