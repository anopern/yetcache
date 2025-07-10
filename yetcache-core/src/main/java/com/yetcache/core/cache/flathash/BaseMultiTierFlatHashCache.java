package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.kv.CaffeineKVCache;
import com.yetcache.core.cache.kv.RedisKVCache;
import com.yetcache.core.cache.loader.KVCacheLoader;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheSpec;
import com.yetcache.core.config.kv.MultiTierKVCacheConfig;
import com.yetcache.core.config.kv.MultiTierKVCacheSpec;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import com.yetcache.core.support.key.KeyConverter;
import org.checkerframework.checker.units.qual.K;
import org.redisson.api.RedissonClient;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/10
 */
public class BaseMultiTierFlatHashCache<F, V> implements MultiTierFlatHashCache<F, V> {
    protected String cacheName;
    private final MultiTierFlatHashCacheConfig config;
    private CaffeineKVCache<V> localCache;
    private RedisKVCache<V> redisCache;
    private KeyConverter<Void> keyConverter;
    private final FlatHashCacheLoader<F, V> cacheLoader;
    protected CaffeinePenetrationProtectCache localPpCache;
    protected RedisPenetrationProtectCache remotePpCache;

    public BaseMultiTierFlatHashCache(String cacheName,
                                      MultiTierFlatHashCacheConfig config,
                                      RedissonClient rClient,
                                      KeyConverter<Void> keyConverter,
                                      FlatHashCacheLoader<F, V> cacheLoader) {
        this.cacheName = cacheName;
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.keyConverter = keyConverter;
        MultiTierFlatHashCacheSpec spec = config.getSpec();
        if (spec.getCacheTier().useLocal()) {
            this.localCache = new CafF<>(config.getLocal());

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
}
