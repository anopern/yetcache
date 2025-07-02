package com.yetcache.core.cache;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.RedisCacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author walter.yan
 * @since 2025/6/29
 */
@Slf4j
public class RedisHashCache<V> {
    protected final RedisCacheConfig config;
    protected final RedissonClient rClient;

    public RedisHashCache(RedisCacheConfig config, RedissonClient rClient) {
        this.config = config;
        this.rClient = rClient;
    }

    public CacheValueHolder<V> getIfPresent(String key, String field) {
        return getRedisMap(key).get(field);
    }

    public void put(String key, String field, CacheValueHolder<V> valueHolder) {
        getRedisMap(key).put(field, valueHolder);
        getRedisMap(key).expire(config.getTtlSecs(), TimeUnit.SECONDS);
    }

    public void putAll(String key, Map<String, CacheValueHolder<V>> valueHolderMap) {
        getRedisMap(key).putAll(valueHolderMap);
        getRedisMap(key).expire(config.getTtlSecs(), TimeUnit.SECONDS);
    }

    public void invalidate(String key, String field) {
        getRedisMap(key).remove(field);
    }

    public Map<String, CacheValueHolder<V>> listAll(String key) {
        return getRedisMap(key).readAllMap();
    }

    protected RMap<String, CacheValueHolder<V>> getRedisMap(String key) {
        return rClient.getMap(key);
    }
}
