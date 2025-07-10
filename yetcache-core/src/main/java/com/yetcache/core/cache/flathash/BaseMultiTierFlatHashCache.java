package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheSpec;
import com.yetcache.core.metrics.HitTier;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import org.redisson.api.RedissonClient;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/10
 */
public class BaseMultiTierFlatHashCache<F, V> implements MultiTierFlatHashCache<F, V> {
    protected String cacheName;
    private final MultiTierFlatHashCacheConfig config;
    private CaffeineFlatHashCache<V> localCache;
    private RedisFlatHashCache<V> redisCache;
    private KeyConverter<F> keyConverter;
    private FieldConverter<F> fieldConverter;
    private final FlatHashCacheLoader<F, V> cacheLoader;
    protected CaffeinePenetrationProtectCache localPpCache;
    protected RedisPenetrationProtectCache remotePpCache;

    public BaseMultiTierFlatHashCache(String cacheName,
                                      MultiTierFlatHashCacheConfig config,
                                      RedissonClient rClient,
                                      KeyConverter<Void> keyConverter,
                                      FieldConverter<F> fieldConverter,
                                      FlatHashCacheLoader<F, V> cacheLoader) {
        this.cacheName = cacheName;
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.fieldConverter = fieldConverter;
        MultiTierFlatHashCacheSpec spec = config.getSpec();
        if (spec.getCacheTier().useLocal()) {
            this.localCache = new CaffeineFlatHashCache<>(config.getLocal());

            PenetrationProtectConfig ppConfig = config.getLocal().getPenetrationProtect();
            this.localPpCache = new CaffeinePenetrationProtectCache(ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }

        if (spec.getCacheTier().useRemote()) {
            this.redisCache = new RedisFlatHashCache<>(config.getRemote(), rClient);

            PenetrationProtectConfig ppConfig = config.getRemote().getPenetrationProtect();
            this.remotePpCache = new RedisPenetrationProtectCache(rClient, ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }
    }

    @Override
    public V get(F bizField) {
        return null;
    }

    @Override
    public FlatHashAccessResult<V> getWithResult(F bizField) {
        String key = keyConverter.convert(null);
        String field = fieldConverter.convert(bizField);

        FlatHashAccessResult<V> result = new FlatHashAccessResult<>();
        FlatHashAccessTrace trace = new FlatHashAccessTrace();
        result.setTrace(trace);

        // === 穿透保护 ===
        if (localPpCache != null && localPpCache.isBlocked(key + ":" + field)) {
            trace.setHitTier(HitTier.BLOCKED);
            return result;
        }
        if (remotePpCache != null && remotePpCache.isBlocked(key + ":" + field)) {
            trace.setHitTier(HitTier.BLOCKED);
            return result;
        }

        // === 本地缓存读取 ===
        if (localCache != null) {
            CacheValueHolder<V> holder = localCache.getIfPresent(key, field);
            if (holder != null && holder.isNotLogicExpired()) {
                result.setValueHolder(holder);
                trace.setHitTier(HitTier.LOCAL);
                return result;
            }
        }

        // === Redis 缓存读取 ===
        if (redisCache != null) {
            CacheValueHolder<V> holder = redisCache.getIfPresent(key, field);
            if (holder != null && holder.isNotLogicExpired()) {
                trace.setHitTier(HitTier.REMOTE);
                result.setValueHolder(holder);

                // 写回本地
                if (localCache != null) {
                    localCache.put(key, field, CacheValueHolder.wrap(holder.getValue(), config.getLocal().getTtlSecs()));
                }

                return result;
            }
        }

        throw new RuntimeException("not implemented");
    }

    @Override
    public Map<F, V> listAll() {
        return null;
    }

    @Override
    public FlatHashAccessResult<V> listAllWithResult() {
        return null;
    }

    @Override
    public boolean refreshAll() {
        return false;
    }

    @Override
    public FlatHashAccessResult<V> refreshAllWithResult() {
        return null;
    }
}
