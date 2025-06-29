package com.yetcache.core.cache.singlehash;

import com.yetcache.core.cache.AbstractMultiTierCache;
import com.yetcache.core.cache.loader.KVCacheLoader;
import com.yetcache.core.cache.result.CacheAccessStatus;
import com.yetcache.core.cache.result.singlehash.SingleHashCacheGetResult;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.config.singlehash.MultiTierSingleHashCacheConfig;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import com.yetcache.core.support.field.CacheFieldConverter;
import com.yetcache.core.support.key.CacheKeyConverter;
import com.yetcache.core.support.util.CacheParamChecker;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class MultiTierSingleHashCache<K, V> extends AbstractMultiTierCache<K>
        implements SingleHashCache<K, V> {

    private String cacheName;
    private final MultiTierSingleHashCacheConfig config;
    private final KVCacheLoader<K, V> cacheLoader;
    private CaffeineSingleHashCache<V> localCache;
    private RedisSingleHashCache<V> remoteCache;
    private CacheKeyConverter<K> keyConverter;
    private CacheFieldConverter<K> fieldConverter;

    public MultiTierSingleHashCache(String cacheName,
                                    MultiTierSingleHashCacheConfig config,
                                    RedissonClient rClient,
                                    KVCacheLoader<K, V> cacheLoader,
                                    CacheKeyConverter<K> keyConverter,
                                    CacheFieldConverter<K> fieldConverter) {
        this.cacheName = cacheName;
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.keyConverter = keyConverter;
        this.fieldConverter = fieldConverter;

        if (config.getCacheTier().useLocal()) {
            config.getLocal().setTtlRandomPercent(config.getTtlRandomPercent());
            this.localCache = new CaffeineSingleHashCache<>(config.getLocal());

            PenetrationProtectConfig ppConfig = config.getLocal().getPenetrationProtect();
            this.localPpCache = new CaffeinePenetrationProtectCache<>(ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }

        if (config.getCacheTier().useRemote()) {
            config.getRemote().setTtlRandomPercent(config.getTtlRandomPercent());
            this.remoteCache = new RedisSingleHashCache<>(config.getRemote(), rClient);

            PenetrationProtectConfig ppConfig = config.getRemote().getPenetrationProtect();
            this.remotePpCache = new RedisPenetrationProtectCache<>(rClient, ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }
    }

    @Override
    public V get(K field) {
        return null;
    }

    @Override
    public void refresh(K field) {

    }

    @Override
    public void invalidate(K field) {

    }

    @Override
    public Map<K, V> listAll(boolean forceRefresh) {
        return null;
    }

    @Override
    public SingleHashCacheGetResult<K, V> getWithResult(K bizField) {
        long startMills = System.currentTimeMillis();
        CacheParamChecker.failIfNull(bizField, cacheName);
        String key = config.getKey();
        String field = fieldConverter.convert(bizField);
        CacheValueHolder<V> localHolder;
        CacheValueHolder<V> remoteHolder;
        SingleHashCacheGetResult<K, V> getResult = new SingleHashCacheGetResult<>(cacheName, config.getCacheTier(),
                key, bizField, field, startMills);

        if (tryLocalBlock(bizField, getResult)) {
            return getResult;
        }
        if (tryRemoteBlock(bizField, getResult)) {
            return getResult;
        }

        // === 1. 本地缓存 ===
        if (localCache != null) {
            localHolder = localCache.getIfPresent(key, field);
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
        if (remoteCache != null) {
            remoteHolder = remoteCache.get(key);
            if (remoteHolder != null) {
                if (remoteHolder.isNotLogicExpired()) {
                    // 回写本地
                    if (localCache != null) {
                        localCache.put(key, field, CacheValueHolder.wrap(remoteHolder.getValue(), config.getLocal().getTtlSecs()));
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
        getResult.setValueHolder(CacheValueHolder.wrap(null, config.getRemote().getTtlSecs()));
        getResult.end();
        return getResult;
    }
}
