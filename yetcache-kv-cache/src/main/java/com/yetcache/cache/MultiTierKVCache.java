package com.yetcache.cache;

import com.yetcache.core.cache.kv.KVCache;
import com.yetcache.core.cache.loader.KVCacheLoader;
import com.yetcache.core.cache.result.KVCacheGetResult;
import com.yetcache.core.cache.result.KVCacheRefreshResult;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.KVCacheGetTrace;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.config.kv.MultiTierKVCacheConfig;
import com.yetcache.core.config.kv.MultiTierKVCacheSpec;
import com.yetcache.core.metrics.HitTier;
import com.yetcache.core.metrics.KVCacheMetrics;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.support.util.CacheParamChecker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
@Data
@Slf4j
public class MultiTierKVCache<K, V> implements KVCache<K, V> {
    protected String cacheName;
    private final MultiTierKVCacheConfig config;
    private CaffeineKVCache<V> localCache;
    private RedisKVCache<V> redisCache;
    private KeyConverter<K> keyConverter;
    private final KVCacheLoader<K, V> cacheLoader;
    protected CaffeinePenetrationProtectCache localPpCache;
    protected RedisPenetrationProtectCache remotePpCache;

    public MultiTierKVCache(String cacheName,
                            MultiTierKVCacheConfig config,
                            RedissonClient rClient,
                            KeyConverter<K> keyConverter,
                            KVCacheLoader<K, V> cacheLoader) {
        this.cacheName = cacheName;
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.keyConverter = keyConverter;
        MultiTierKVCacheSpec spec = config.getSpec();
        if (spec.getCacheTier().useLocal()) {
            this.localCache = new CaffeineKVCache<>(config.getLocal());

            PenetrationProtectConfig ppConfig = config.getLocal().getPenetrationProtect();
            this.localPpCache = new CaffeinePenetrationProtectCache(ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }

        if (spec.getCacheTier().useRemote()) {
            this.redisCache = new RedisKVCache<>(config.getRemote(), rClient);

            PenetrationProtectConfig ppConfig = config.getRemote().getPenetrationProtect();
            this.remotePpCache = new RedisPenetrationProtectCache(rClient, ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }
    }

    @Override
    public V get(K bizKey) {
        KVCacheGetResult<V> getResult = getWithResult(bizKey);
        KVCacheMetrics.countGetHit(cacheName, getResult.getTrace().getHitTier().name());
        log.debug("CacheGetResult: {}", getResult);
        if (getResult.getValueHolder() != null) {
            return getResult.getValueHolder().getValue();
        }
        return null;
    }

    @Override
    public void refresh(K bizKey) {

    }

    @Override
    public KVCacheGetResult<V> getWithResult(K bizKey) {
        CacheParamChecker.failIfNull(bizKey, cacheName);
        String key = keyConverter.convert(bizKey);

        CacheValueHolder<V> localHolder;
        CacheValueHolder<V> remoteHolder;

        KVCacheGetResult<V> result = new KVCacheGetResult<>();
        KVCacheGetTrace trace = new KVCacheGetTrace();
        result.setTrace(trace);
        if (null != localPpCache) {
            boolean blocked = localPpCache.isBlocked(key);
            if (blocked) {
                trace.setHitTier(HitTier.BLOCKED);
                return result;
            }
        }

        if (null != remotePpCache) {
            boolean blocked = remotePpCache.isBlocked(key);
            if (blocked) {
                trace.setHitTier(HitTier.BLOCKED);
                return result;
            }
        }

        // === 1. 本地缓存 ===
        if (localCache != null) {
            localHolder = localCache.getIfPresent(key);
            if (localHolder != null && localHolder.isNotLogicExpired()) {
                result.setValueHolder(localHolder);
                trace.setHitTier(HitTier.LOCAL);
                return result;
            }
        }

        // === 2. Redis 缓存 ===
        if (redisCache != null) {
            remoteHolder = redisCache.getIfPresent(key);
            if (remoteHolder != null && remoteHolder.isNotLogicExpired()) {
                // 回写本地
                if (localCache != null) {
                    localCache.put(key, CacheValueHolder.wrap(remoteHolder.getValue(), config.getLocal().getTtlSecs()));
                }
                result.setValueHolder(remoteHolder);
                trace.setHitTier(HitTier.REMOTE);
                return result;
            }
        }

        // === 3. 回源加载 ===
        try {
            V loaded = cacheLoader.load(bizKey);
            if (loaded != null) {
                if (redisCache != null) {
                    CacheValueHolder<V> valueHolder = CacheValueHolder.wrap(loaded, config.getRemote().getTtlSecs());
                    redisCache.put(key, valueHolder);
                    result.setValueHolder(valueHolder);
                }
                if (localCache != null) {
                    CacheValueHolder<V> valueHolder = CacheValueHolder.wrap(loaded, config.getLocal().getTtlSecs());
                    localCache.put(key, valueHolder);
                    result.setValueHolder(valueHolder);
                }
                trace.setHitTier(HitTier.SOURCE);
            } else {
                if (null != localPpCache) {
                    localPpCache.markMiss(key);
                }
                if (null != remotePpCache) {
                    remotePpCache.markMiss(key);
                }
            }
        } catch (Exception e) {
            log.warn("缓存回源加载失败，cacheName={}, bizKey={}, key={}, ", cacheName, bizKey, key, e);
        }
        return result;
    }

    @Override
    public KVCacheRefreshResult<V> refreshWithResult(K bizKey) {
        CacheParamChecker.failIfNull(bizKey, cacheName);
        String key = keyConverter.convert(bizKey);
        KVCacheRefreshResult<V> result = new KVCacheRefreshResult<>();
        V loaded = cacheLoader.load(bizKey);
        if (loaded != null) {
            if (redisCache != null) {
                redisCache.put(key, CacheValueHolder.wrap(loaded, config.getRemote().getTtlSecs()));
            }
            if (localCache != null) {
                localCache.put(key, CacheValueHolder.wrap(loaded, config.getLocal().getTtlSecs()));
            }
            result.setValueHolder(new CacheValueHolder<>(loaded));
        }
        return result;
    }
}
