package com.yetcache.core.kv;

import com.yetcache.core.config.MultiTierCacheConfig;
import com.yetcache.core.key.CacheKeyConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MultiTierKVCache<K, V> extends AbstractKVCache<K, V> {
    private final MultiTierCacheConfig config;
    private final KVCacheLoader<K, V> cacheLoader;
    private CaffeineKVCache<V> localCache;
    private RedisKVCache<V> redisCache;
    private CacheKeyConverter<K> keyConverter;

    public MultiTierKVCache(MultiTierCacheConfig config,
                            RedissonClient rClient,
                            KVCacheLoader<K, V> cacheLoader,
                            CacheKeyConverter<K> keyConverter) {
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.keyConverter = keyConverter;

        this.localCache = config.getCacheTier().useLocal() ? new CaffeineKVCache<>(config.getLocal()) : null;
        this.redisCache = config.getCacheTier().useRemote() ? new RedisKVCache<>(config.getRemote(), rClient) : null;
    }

    @Override
    public CacheGetResult<K, V> getWithResult(K bizKey) {
        String key = keyConverter.convert(bizKey);
        // 1. 先查本地缓存
        if (localCache != null) {
            V localValue = localCache.getIfPresent(key);
            if (localValue != null) {
                return CacheGetResult.localHit(bizKey, localValue);
            }
        }

        // 2. 查 Redis 缓存
        if (redisCache != null) {
            V remoteValue = redisCache.get(key);
            if (remoteValue != null) {
                // 回写本地缓存（穿透保护、预热可选）
                if (localCache != null) {
                    localCache.put(key, remoteValue);
                }
                return CacheGetResult.remoteHit(bizKey, remoteValue);
            }
        }

        // 3. 回源（业务加载器）
        V loaded = cacheLoader.load(bizKey);
        if (loaded != null) {
            if (redisCache != null) {
                redisCache.put(key, loaded);
            }
            if (localCache != null) {
                localCache.put(key, loaded);
            }
            return CacheGetResult.missThenLoad(bizKey, loaded);
        }

        // 4. 完全 miss
        return CacheGetResult.notFound(bizKey);
    }

    @Override
    public CacheResult putWithResult(K key, V value) {
        return null;
    }

    @Override
    public CacheResult invalidateWithResult(K key) {
        return null;
    }

    @Override
    public KVCacheRefreshResult<K, V> refresh(K key) {
        return null;
    }
}
