package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.AbstractMultiTierCache;
import com.yetcache.core.cache.loader.KVCacheLoader;
import com.yetcache.core.cache.result.CacheAccessStatus;
import com.yetcache.core.cache.result.SourceLoadStatus;
import com.yetcache.core.cache.result.singlehash.FlatHashCacheGetResult;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.config.singlehash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.context.CacheAccessContext;
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
public class MultiTierFlatHashCache<K, V> extends AbstractMultiTierCache<K>
        implements FlatHashCache<K, V> {

    private String cacheName;
    private final MultiTierFlatHashCacheConfig config;
    private final KVCacheLoader<K, V> cacheLoader;
    private CaffeineFlatHashCache<V> localCache;
    private RedisFlatHashCache<V> remoteCache;
    private CacheKeyConverter<K> keyConverter;
    private CacheFieldConverter<K> fieldConverter;

    public MultiTierFlatHashCache(String cacheName,
                                  MultiTierFlatHashCacheConfig config,
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
            this.localCache = new CaffeineFlatHashCache<>(config.getLocal());

            PenetrationProtectConfig ppConfig = config.getLocal().getPenetrationProtect();
            this.localPpCache = new CaffeinePenetrationProtectCache<>(ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }

        if (config.getCacheTier().useRemote()) {
            config.getRemote().setTtlRandomPercent(config.getTtlRandomPercent());
            this.remoteCache = new RedisFlatHashCache<>(config.getRemote(), rClient);

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
    public V get(String tenantCode, K bizField) {
        CacheAccessContext.setTenantCode(tenantCode);
        return get(bizField);
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
    public FlatHashCacheGetResult<K, V> getWithResult(K bizField) {
        try {
            CacheParamChecker.failIfNull(bizField, cacheName);
            long startMills = System.currentTimeMillis();

            String key = config.getKey();
            String field = fieldConverter.convert(bizField);

            FlatHashCacheGetResult<K, V> result = new FlatHashCacheGetResult<>(cacheName, config.getCacheTier(), key, bizField, field, startMills);

            if (tryBlock(bizField, result)) {
                return result;
            }

            CacheValueHolder<V> holder = tryLocalGet(key, field, result);
            if (holder != null) {
                return end(result, holder);
            }

            holder = tryRemoteGet(key, field, result);
            if (holder != null) {
                return end(result, holder);
            }

            if (!Boolean.TRUE.equals(config.getEnableLoadFallbackOnMiss())) {
                return end(result, null);
            }

            holder = tryLoad(key, field, bizField, result);
            return end(result, holder);
        } finally {
            CacheAccessContext.clear();
        }
    }

    private boolean tryBlock(K bizField, FlatHashCacheGetResult<K, V> result) {
        return tryLocalBlock(bizField, result) || tryRemoteBlock(bizField, result);
    }

    private CacheValueHolder<V> tryLocalGet(String key, String field, FlatHashCacheGetResult<K, V> result) {
        if (localCache == null) {
            return null;
        }

        CacheValueHolder<V> holder = localCache.getIfPresent(key, field);
        if (holder == null) {
            result.setLocalStatus(CacheAccessStatus.PHYSICAL_MISS);
            return null;
        }

        if (holder.isNotLogicExpired()) {
            result.setLocalStatus(CacheAccessStatus.HIT);
            result.setValueHolder(holder);
            return holder;
        } else {
            result.setLocalStatus(CacheAccessStatus.LOGIC_EXPIRED);
            return null;
        }
    }

    private CacheValueHolder<V> tryRemoteGet(String key, String field, FlatHashCacheGetResult<K, V> result) {
        if (remoteCache == null) {
            return null;
        }

        CacheValueHolder<V> holder = remoteCache.get(key);
        if (holder == null) {
            result.setRemoteStatus(CacheAccessStatus.PHYSICAL_MISS);
            return null;
        }

        if (holder.isNotLogicExpired()) {
            result.setRemoteStatus(CacheAccessStatus.HIT);
            result.setValueHolder(holder);
            if (localCache != null) {
                localCache.put(key, field, CacheValueHolder.wrap(holder.getValue(), config.getLocal().getTtlSecs()));
            }
            return holder;
        } else {
            result.setRemoteStatus(CacheAccessStatus.LOGIC_EXPIRED);
            return null;
        }
    }

    private CacheValueHolder<V> tryLoad(String key, String field, K bizField, FlatHashCacheGetResult<K, V> result) {
        try {
            V loaded = cacheLoader.load(bizField);
            if (loaded == null) {
                result.setLoadStatus(SourceLoadStatus.NO_VALUE);
                markPenetrationProtect(bizField);
                return null;
            }

            CacheValueHolder<V> wrappedRemote = CacheValueHolder.wrap(loaded, config.getRemote().getTtlSecs());
            CacheValueHolder<V> wrappedLocal = CacheValueHolder.wrap(loaded, config.getLocal().getTtlSecs());

            if (remoteCache != null) {
                remoteCache.put(key, wrappedRemote);
            }
            if (localCache != null) {
                localCache.put(key, field, wrappedLocal);
            }

            result.setLoadStatus(SourceLoadStatus.LOADED);
            result.setValueHolder(new CacheValueHolder<>(loaded));
            return result.getValueHolder();
        } catch (Exception e) {
            result.setLoadStatus(SourceLoadStatus.ERROR);
            result.setException(e);
            log.warn("缓存回源加载失败，cacheName={}, bizKey={}, key={}", cacheName, bizField, key, e);
            return null;
        }
    }

    private void markPenetrationProtect(K bizField) {
        if (localPpCache != null) {
            localPpCache.markMiss(bizField);
        }
        if (remotePpCache != null) {
            remotePpCache.markMiss(bizField);
        }
    }

    private FlatHashCacheGetResult<K, V> end(FlatHashCacheGetResult<K, V> result, CacheValueHolder<V> holder) {
        if (holder != null) {
            result.setValueHolder(holder);
        }
        result.end();
        return result;
    }

}
