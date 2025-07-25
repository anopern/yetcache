package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.dynamichash.RedisDynamicHashCacheConfig;
import com.yetcache.core.support.util.TtlRandomizer;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 二级远程缓存组件，仅封装基础 KV 操作。
 * 不负责观测性、不持有 cacheName，所有上下文需由上层传入。
 *
 * @author walter.yan
 * @since 2025/6/25
 */
@Slf4j
public class RedisDynamicHashCache<V> {

    protected final RedisDynamicHashCacheConfig config;
    protected final RedissonClient rClient;

    public RedisDynamicHashCache(RedisDynamicHashCacheConfig config, RedissonClient rClient) {
        this.config = config;
        this.rClient = rClient;
    }

    public CacheValueHolder<V> getIfPresent(String key, String field) {
        RMap<String, CacheValueHolder<V>> map = rClient.getMap(key);
        return map.get(field);
    }

    public Map<String, CacheValueHolder<V>> batchGet(String key, List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return Collections.emptyMap();
        }

        RMap<String, CacheValueHolder<V>> redisMap = rClient.getMap(key);

        // 使用 Redisson 的批量 getAll 接口，避免 N 次 get 请求
        Map<String, CacheValueHolder<V>> resultMap = redisMap.getAll(new HashSet<>(fields));

        // 返回非 null map（Redisson 不保证值一定不为 null）
        return resultMap != null ? resultMap : Collections.emptyMap();
    }

    public void put(String key, String field, CacheValueHolder<V> value) {
        RMap<String, CacheValueHolder<V>> map = rClient.getMap(key);
        long realTtlSecs = TtlRandomizer.randomizeSecs(config.getTtlSecs(), config.getTtlRandomPct());
        map.put(field, value);
        map.expire(realTtlSecs, TimeUnit.SECONDS);
    }

    public Map<String, CacheValueHolder<V>> listAll(String key) {
        RMap<String, CacheValueHolder<V>> map = rClient.getMap(key);
        return map.readAllMap();
    }

    public void putAll(String key, Map<String, CacheValueHolder<V>> holderMap) {
        RMap<String, CacheValueHolder<V>> map = rClient.getMap(key);
        map.putAll(holderMap);
        long realTtlSecs = TtlRandomizer.randomizeSecs(config.getTtlSecs(), config.getTtlRandomPct());
        map.expire(realTtlSecs, TimeUnit.SECONDS);
    }

    public void invalidate(String key, String field) {
        Map<String, CacheValueHolder<V>> rmap = rClient.getMap(key);
        rmap.remove(field);
    }

    public void invalidateAll(String key) {
        RMap<String, CacheValueHolder<V>> map = rClient.getMap(key);
        map.clear();
    }
}
