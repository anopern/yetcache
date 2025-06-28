package com.yetcache.core.kv;

import com.yetcache.core.CacheAccessStatus;
import com.yetcache.core.CacheValueHolder;
import com.yetcache.core.SourceLoadStatus;
import com.yetcache.core.config.MultiTierCacheConfig;
import com.yetcache.core.key.CacheKeyConverter;
import com.yetcache.core.util.CacheParamChecker;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class MultiTierKVCache<K, V> extends AbstractKVCache<K, V> {
    private String cacheName;
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
        CacheParamChecker.failIfNull(bizKey, getCacheName());
        String key = keyConverter.convert(bizKey);

        CacheValueHolder<V> localHolder;
        CacheValueHolder<V> remoteHolder;
        CacheGetResult<K, V> getResult = new CacheGetResult<>(getCacheName(), config.getCacheTier(), bizKey, key);

        // === 1. 本地缓存 ===
        if (localCache != null) {
            localHolder = localCache.getIfPresent(key);
            if (localHolder != null) {
                if (localHolder.isNotLogicExpired()) {
                    getResult.setLocalStatus(CacheAccessStatus.HIT);
                    getResult.setValueHolder(localHolder);
                    return getResult;
                } else {
                    getResult.setLocalStatus(CacheAccessStatus.LOGIC_EXPIRED);
                }
            }
        }

        // === 2. Redis 缓存 ===
        if (redisCache != null) {
            remoteHolder = redisCache.get(key);
            if (remoteHolder != null) {
                if (remoteHolder.isNotLogicExpired()) {
                    // 回写本地
                    if (localCache != null) {
                        localCache.put(key, remoteHolder.getValue());
                    }
                    getResult.setRemoteStatus(CacheAccessStatus.HIT);
                    getResult.setValueHolder(remoteHolder);
                    return getResult;
                } else {
                    getResult.setRemoteStatus(CacheAccessStatus.LOGIC_EXPIRED);
                }
            } else {
                getResult.setRemoteStatus(CacheAccessStatus.PHYSICAL_MISS);
            }
        }

        // === 3. 回源加载 ===
        try {
            V loaded = cacheLoader.load(bizKey);
            if (loaded != null) {
                if (redisCache != null) redisCache.put(key, loaded);
                if (localCache != null) localCache.put(key, loaded);
                getResult.setLoadStatus(SourceLoadStatus.LOADED);
                getResult.setValueHolder(new CacheValueHolder<>(loaded));
            } else {
                getResult.setLoadStatus(SourceLoadStatus.NO_VALUE);
            }
        } catch (Exception e) {
            // 加载失败，可带上旧值用于降级
            getResult.setLoadStatus(SourceLoadStatus.ERROR);
            getResult.setException(e);
            log.warn("缓存回源加载失败，cacheName={}, bizKey={}, key={}, ", getCacheName(), bizKey, key, e);
        }
        return getResult;
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
