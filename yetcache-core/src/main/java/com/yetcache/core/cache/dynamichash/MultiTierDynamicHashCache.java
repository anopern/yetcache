package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.cache.AbstractMultiTierCache;
import com.yetcache.core.cache.CaffeineHashCache;
import com.yetcache.core.cache.RedisHashCache;
import com.yetcache.core.cache.result.dynamichash.DynamicHashCacheResult;
import com.yetcache.core.config.MultiTierDynamicHashCacheConfig;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import org.redisson.api.RedissonClient;
import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public class MultiTierDynamicHashCache<K, F, V> extends AbstractMultiTierCache<K>
        implements DynamicHashCache<K, F, V> {
    private final MultiTierDynamicHashCacheConfig config;
    private final DynamicHashCache<K, F, V> cacheLoader;
    private CaffeineHashCache<V> localCache;
    private RedisHashCache<V> remoteCache;
    private KeyConverter<K> keyConverter;
    private FieldConverter<F> fieldConverter;

    public MultiTierDynamicHashCache(String cacheName,
                                     MultiTierDynamicHashCacheConfig config,
                                     RedissonClient rClient,
                                     DynamicHashCache<K, F, V> cacheLoader,
                                     KeyConverter<K> keyConverter,
                                     FieldConverter<F> fieldConverter) {
        this.cacheName = cacheName;
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.keyConverter = keyConverter;
        this.fieldConverter = fieldConverter;

        if (config.getCacheTier().useLocal()) {
            this.localCache = new CaffeineHashCache<>(config.getLocal());
            PenetrationProtectConfig ppConfig = config.getLocal().getPenetrationProtect();
            this.localPpCache = new CaffeinePenetrationProtectCache<>(ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }

        if (config.getCacheTier().useRemote()) {
            this.remoteCache = new RedisHashCache<>(config.getRemote(), rClient);
            PenetrationProtectConfig ppConfig = config.getRemote().getPenetrationProtect();
            this.remotePpCache = new RedisPenetrationProtectCache<>(rClient, ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }
    }

    @Override
    public V get(K bizKey, F bizField) {
        return null;
    }

    @Override
    public DynamicHashCacheResult<K, F, V> getWithResult(K bizKey, F bizField) {
        return null;
    }

    @Override
    public DynamicHashCacheResult<K, F, V> refreshWithResult(K bizKey, F bizField) {
        return null;
    }

    @Override
    public DynamicHashCacheResult<K, F, V> batchRefreshWithResult(Map<K, List<F>> bizKeyMap) {
        return null;
    }
}
