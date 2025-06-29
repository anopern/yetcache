package com.yetcache.core.cache.singlehash;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.singlehash.RedisSingleHashCacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
@Slf4j
public class RedisSingleHashCache<V> {
    protected final RedisSingleHashCacheConfig config;
    protected final RedissonClient rClient;

    public RedisSingleHashCache(RedisSingleHashCacheConfig config, RedissonClient rClient) {
        this.config = config;
        this.rClient = rClient;
    }

    public CacheValueHolder<V> get(String field) {
        return getRedisMap().get(field);
    }

    public void put(String field, V value) {
        CacheValueHolder<V> valueHolder = CacheValueHolder.wrap(value, config.getTtlSecs());
        getRedisMap().put(field, valueHolder);
    }

    public void invalidate(String field) {
        getRedisMap().remove(field);
    }

    public Map<String, CacheValueHolder<V>> listAll() {
        return getRedisMap().readAllMap();
    }

    protected RMap<String, CacheValueHolder<V>> getRedisMap() {
        return rClient.getMap(config.getKey());
    }
}
