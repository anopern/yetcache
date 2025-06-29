package com.yetcache.core.cache.kv;

import com.yetcache.core.cache.result.*;
import com.yetcache.core.cache.result.CacheAccessStatus;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.result.SourceLoadStatus;
import com.yetcache.core.cache.loader.KVCacheLoader;
import com.yetcache.core.config.MultiTierCacheConfig;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.support.key.CacheKeyConverter;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import com.yetcache.core.support.util.CacheParamChecker;
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
    private CaffeineKVCache<CacheValueHolder<V>> localCache;
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

        if (config.getCacheTier().useLocal()) {
            config.getLocal().setTtlRandomPercent(config.getTtlRandomPercent());
            this.localCache = new CaffeineKVCache<>(config.getLocal());

            PenetrationProtectConfig ppConfig = config.getLocal().getPenetrationProtect();
            this.localPpCache = new CaffeinePenetrationProtectCache<>(ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }

        if (config.getCacheTier().useRemote()) {
            config.getRemote().setTtlRandomPercent(config.getTtlRandomPercent());
            this.redisCache = new RedisKVCache<>(config.getRemote(), rClient);

            PenetrationProtectConfig ppConfig = config.getRemote().getPenetrationProtect();
            this.remotePpCache = new RedisPenetrationProtectCache<>(rClient, ppConfig.getPrefix(), cacheName,
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
        long startMills = System.currentTimeMillis();
        CacheParamChecker.failIfNull(bizKey, cacheName);
        String key = keyConverter.convert(bizKey);

        CacheValueHolder<V> localHolder;
        CacheValueHolder<V> remoteHolder;
        CacheGetResult<K, V> getResult = new CacheGetResult<>(cacheName, config.getCacheTier(), bizKey, key, startMills);
        if (null != localPpCache) {
            boolean blocked = localPpCache.isBlocked(bizKey);
            if (blocked) {
                getResult.setLocalStatus(CacheAccessStatus.BLOCKED);
                getResult.end();
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
                getResult.end();
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
                    getResult.end();
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
                        localCache.put(key, CacheValueHolder.wrap(remoteHolder.getValue(), config.getLocal().getTtlSecs()));
                    }
                    getResult.setRemoteStatus(CacheAccessStatus.HIT);
                    getResult.setValueHolder(remoteHolder);
                    getResult.end();
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
                if (redisCache != null) {
                    CacheValueHolder<V> valueHolder = CacheValueHolder.wrap(loaded, config.getRemote().getTtlSecs());
                    redisCache.put(key, valueHolder);
                }
                if (localCache != null) {
                    CacheValueHolder<V> valueHolder = CacheValueHolder.wrap(loaded, config.getLocal().getTtlSecs());
                    localCache.put(key, valueHolder);
                }
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
            log.warn("缓存回源加载失败，cacheName={}, bizKey={}, key={}, ", cacheName, bizKey, key, e);
        }
        getResult.end();
        return getResult;
    }

    @Override
    public CachePutResult<K, V> putWithResult(K bizKey, V value) {
        CacheParamChecker.failIfNull(bizKey, cacheName);
        String key = keyConverter.convert(bizKey);
        CachePutResult<K, V> putResult = new CachePutResult<>(cacheName, config.getCacheTier(), bizKey, key);
        if (redisCache != null) {
            redisCache.put(key, CacheValueHolder.wrap(value, config.getRemote().getTtlSecs()));
        }
        if (localCache != null) {
            localCache.put(key, CacheValueHolder.wrap(value, config.getLocal().getTtlSecs()));
        }
        return putResult;
    }

    @Override
    public CacheResult<K> invalidateWithResult(K bizKey) {
        CacheParamChecker.failIfNull(bizKey, cacheName);
        Long startMills = System.currentTimeMillis();
        String key = keyConverter.convert(bizKey);
        CacheInvalidateResult<K> invalidateResult = new CacheInvalidateResult<>(cacheName, config.getCacheTier(),
                bizKey, key, startMills);
        if (localCache != null) {
            localCache.invalidate(key);
        }
        if (redisCache != null) {
            redisCache.invalidate(key);
        }
        return invalidateResult.end();
    }

    @Override
    public CacheRefreshResult<K, V> refresh(K bizKey) {
        Long startMills = System.currentTimeMillis();
        CacheParamChecker.failIfNull(bizKey, cacheName);
        String key = keyConverter.convert(bizKey);
        CacheRefreshResult<K, V> refreshResult = new CacheRefreshResult<>(cacheName, config.getCacheTier(),
                bizKey, key, startMills);
        V loaded = cacheLoader.load(bizKey);
        if (loaded != null) {
            if (localCache != null) {
                localCache.put(key, CacheValueHolder.wrap(loaded, config.getLocal().getTtlSecs()));
            }
            if (redisCache != null) {
                redisCache.put(key, CacheValueHolder.wrap(loaded, config.getRemote().getTtlSecs()));
            }
            refreshResult.setValueHolder(new CacheValueHolder<>(loaded));
        } else {
            refreshResult.setLoadStatus(SourceLoadStatus.NO_VALUE);
        }
        refreshResult.end();
        return refreshResult;
    }
}
