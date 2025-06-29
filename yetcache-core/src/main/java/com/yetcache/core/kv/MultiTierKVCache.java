package com.yetcache.core.kv;

import com.yetcache.core.CacheAccessStatus;
import com.yetcache.core.CacheValueHolder;
import com.yetcache.core.SourceLoadStatus;
import com.yetcache.core.config.MultiTierCacheConfig;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.key.CacheKeyConverter;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import com.yetcache.core.util.CacheParamChecker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
@Data
@Slf4j
public class MultiTierKVCache<K, V> implements KVCache<K, V> {
    private String cacheName;
    private final MultiTierCacheConfig config;
    private final KVCacheLoader<K, V> cacheLoader;
    private CaffeineKVCache<V> localCache;
    private RedisKVCache<V> redisCache;
    private CacheKeyConverter<K> keyConverter;
    private CaffeinePenetrationProtectCache<K> localPpCache;
    private RedisPenetrationProtectCache<K> remotePpCache;

    public MultiTierKVCache(String cacheName,
                            MultiTierCacheConfig config,
                            RedissonClient rClient,
                            KVCacheLoader<K, V> cacheLoader,
                            CacheKeyConverter<K> keyConverter) {
        this.cacheName = cacheName;
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.keyConverter = keyConverter;

        this.localCache = config.getCacheTier().useLocal() ? new CaffeineKVCache<>(config.getLocal()) : null;
        this.redisCache = config.getCacheTier().useRemote() ? new RedisKVCache<>(config.getRemote(), rClient) : null;

        if (config.getCacheTier().useLocal()) {
            PenetrationProtectConfig ppConfig = config.getLocal().getPenetrationProtect();
            this.localPpCache = new CaffeinePenetrationProtectCache<>(ppConfig.getPrefix(), getCacheName(),
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }

        if (config.getCacheTier().useRemote()) {
            PenetrationProtectConfig ppConfig = config.getRemote().getPenetrationProtect();
            this.remotePpCache = new RedisPenetrationProtectCache<>(rClient, ppConfig.getPrefix(), getCacheName(),
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }
    }

    @Override
    public V get(K bizKey) {
        CacheGetResult<K, V> getResult = getWithResult(bizKey);
        log.debug("CacheGetResult: {}", getResult);
        if (getResult.getValueHolder() != null) {
            return getResult.getValueHolder().getValue();
        }
        return null;
    }

    @Override
    public Map<K, V> batchGet(List<K> bizKeys) {
        return null;
    }

    @Override
    public void put(K key, V value) {
        putWithResult(key, value);
    }

    @Override
    public void invalidate(K key) {
        invalidateWithResult(key);
    }

    @Override
    public CacheGetResult<K, V> getWithResult(K bizKey) {
        CacheParamChecker.failIfNull(bizKey, getCacheName());
        String key = keyConverter.convert(bizKey);

        CacheValueHolder<V> localHolder;
        CacheValueHolder<V> remoteHolder;
        CacheGetResult<K, V> getResult = new CacheGetResult<>(getCacheName(), config.getCacheTier(), bizKey, key);

        if (null != localPpCache) {
            boolean blocked = localPpCache.isBlocked(bizKey);
            if (blocked) {
                getResult.setLocalStatus(CacheAccessStatus.BLOCKED);
                return getResult;
            }
        }
        if (null != remotePpCache) {
            boolean blocked = remotePpCache.isBlocked(bizKey);
            if (blocked) {
                getResult.setLocalStatus(CacheAccessStatus.PHYSICAL_MISS);
                getResult.setRemoteStatus(CacheAccessStatus.BLOCKED);
                if (null != localPpCache) {
                    localPpCache.markMiss(bizKey);
                }
                return getResult;
            }
        }

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
            } else {
                getResult.setLocalStatus(CacheAccessStatus.PHYSICAL_MISS);
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
                if (null != localPpCache) {
                    localPpCache.markMiss(bizKey);
                }
                if (null != remotePpCache) {
                    remotePpCache.markMiss(bizKey);
                }
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
    public CachePutResult<K, V> putWithResult(K bizKey, V value) {
        CacheParamChecker.failIfNull(bizKey, getCacheName());
        String key = keyConverter.convert(bizKey);
        CachePutResult<K, V> putResult = new CachePutResult<>(getCacheName(), config.getCacheTier(), bizKey, key);
        if (localCache != null) {
            localCache.put(key, value);
        }
        if (redisCache != null) {
            redisCache.put(key, value);
        }
        return putResult;
    }

    @Override
    public CacheResult<K> invalidateWithResult(K bizKey) {
        CacheParamChecker.failIfNull(bizKey, getCacheName());
        String key = keyConverter.convert(bizKey);
        CacheInvalidateResult<K> invalidateResult = new CacheInvalidateResult<>(getCacheName(), config.getCacheTier(),
                bizKey, key);
        if (localCache != null) {
            localCache.invalidate(key);
        }
        if (redisCache != null) {
            redisCache.invalidate(key);
        }
        return invalidateResult;
    }

    @Override
    public CacheRefreshResult<K, V> refresh(K bizKey) {
        CacheParamChecker.failIfNull(bizKey, getCacheName());
        String key = keyConverter.convert(bizKey);
        CacheRefreshResult<K, V> refreshResult = new CacheRefreshResult<>(getCacheName(), config.getCacheTier(), bizKey, key);
        V loaded = cacheLoader.load(bizKey);
        if (loaded != null) {
            if (localCache != null) {
                localCache.put(key, loaded);
            }
            if (redisCache != null) {
                redisCache.put(key, loaded);
            }
            refreshResult.valueHolder = new CacheValueHolder<>(loaded);
        } else {
            refreshResult.setLoadStatus(SourceLoadStatus.NO_VALUE);
        }
        return refreshResult;
    }
}
