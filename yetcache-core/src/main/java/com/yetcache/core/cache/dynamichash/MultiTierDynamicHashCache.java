package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.cache.AbstractMultiTierHashCache;
import com.yetcache.core.cache.CaffeineHashCache;
import com.yetcache.core.cache.RedisHashCache;
import com.yetcache.core.cache.loader.DynamicHashCacheLoader;
import com.yetcache.core.cache.result.dynamichash.DynamicHashCacheGetResult;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.MultiTierDynamicHashCacheConfig;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.support.result.CacheLookupResult;
import com.yetcache.core.support.trace.dynamichash.DefaultDynamicHashCacheAccessRecorder;
import com.yetcache.core.support.trace.dynamichash.DynamicHashCacheAccessRecorder;
import com.yetcache.core.support.util.CacheParamChecker;
import com.yetcache.core.util.CacheKeyUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MultiTierDynamicHashCache<K, F, V> extends AbstractMultiTierHashCache<F, V>
        implements DynamicHashCache<K, F, V> {
    private final MultiTierDynamicHashCacheConfig config;
    private KeyConverter<K> keyConverter;
    private final DynamicHashCacheLoader<K, F, V> loader;

    public MultiTierDynamicHashCache(String cacheName,
                                     MultiTierDynamicHashCacheConfig config,
                                     RedissonClient rClient,
                                     KeyConverter<K> keyConverter,
                                     FieldConverter<F> fieldConverter,
                                     DynamicHashCacheLoader<K, F, V> loader) {
        this.cacheName = cacheName;
        this.config = config;
        this.keyConverter = keyConverter;
        this.fieldConverter = fieldConverter;
        this.loader = loader;

        if (config.getCacheTier().useLocal()) {
            this.localCache = new CaffeineHashCache<>(config.getLocal());
            PenetrationProtectConfig ppConfig = config.getLocal().getPenetrationProtect();
            this.localPpCache = new CaffeinePenetrationProtectCache(ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }

        if (config.getCacheTier().useRemote()) {
            this.remoteCache = new RedisHashCache<>(config.getRemote(), rClient);
            PenetrationProtectConfig ppConfig = config.getRemote().getPenetrationProtect();
            this.remotePpCache = new RedisPenetrationProtectCache(rClient, ppConfig.getPrefix(), cacheName,
                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
        }
    }

    @Override
    public V get(K bizKey, F bizField) {
        return null;
    }

    @Override
    public DynamicHashCacheGetResult<K, F, V> getWithResult(K bizKey, F bizField) {
        try {
            CacheParamChecker.failIfNull(bizKey, cacheName);
            CacheParamChecker.failIfNull(bizField, cacheName);

            DynamicHashCacheAccessRecorder<K, F> recorder = new DefaultDynamicHashCacheAccessRecorder<>();
            recorder.recordStart(bizKey, bizField);

            String key = keyConverter.convert(bizKey);
            String field = fieldConverter.convert(bizField);
            DynamicHashCacheGetResult<K, F, V> result = new DynamicHashCacheGetResult<>();

            if (tryBlockAndRecord(key, field, bizKey, bizField, recorder)) {
                return end(recorder, result);
            }

            if (tryCacheLookupAndRecord(key, bizKey, field, bizField, recorder, result)) {
                return end(recorder, result);
            }

            if (tryLoadAndRecord(key, bizKey, field, bizField, recorder, result)) {
                return end(recorder, result);
            }

            return end(recorder, result);
        } finally {
            CacheAccessContext.clear();
        }
    }

    @Override
    public DynamicHashCacheGetResult<K, F, V> refreshWithResult(K bizKey, F bizField) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DynamicHashCacheGetResult<K, F, V> batchRefreshWithResult(Map<K, List<F>> bizKeyMap) {
        DynamicHashCacheGetResult<K, F, V> result = new DynamicHashCacheGetResult<>();
        DynamicHashCacheAccessRecorder<K, F> recorder = new DefaultDynamicHashCacheAccessRecorder<>();
        recorder.recordStart(bizKeyMap);

        try {
            Map<K, Map<F, V>> values = loader.batchLoad(bizKeyMap);
            return end(recorder, result);
        } finally {
            CacheAccessContext.clear();
        }
    }

    private boolean tryLoadAndRecord(String key, K bizKey, String field, F bizField,
                                     DynamicHashCacheAccessRecorder<K, F> recorder,
                                     DynamicHashCacheGetResult<K, F, V> result) {
        try {
            V value = loader.load(bizKey, bizField);
            if (value == null) {
                recorder.recordSourceLoadNoValue(bizKey, bizField);
                markPenetrationProtect(key, field);
                return true;
            }

            recorder.recordSourceLoaded(bizKey, bizField);

            CacheValueHolder<V> remoteHolder = CacheValueHolder.wrap(value, config.getRemote().getTtlSecs());
            CacheValueHolder<V> localHolder = CacheValueHolder.wrap(value, config.getLocal().getTtlSecs());

            if (remoteCache != null) {
                remoteCache.put(key, field, remoteHolder);
            }
            if (localCache != null) {
                localCache.put(key, field, localHolder);
            }

            result.setValueHolder(localHolder); // 返回本地 holder 以提升命中率
            return true;

        } catch (Exception e) {
            recorder.recordSourceLoadFailed(bizKey, bizField, e);
            return false;
        }
    }

    private boolean tryCacheLookupAndRecord(String key, K bizKey, String field, F bizField,
                                            DynamicHashCacheAccessRecorder<K, F> recorder,
                                            DynamicHashCacheGetResult<K, F, V> result) {
        CacheLookupResult<V> localResult = tryLocalGet(key, field);
        if (localResult != null) {
            if (localResult.isHit()) {
                recorder.recordLocalHit(bizKey, bizField);
                result.setValueHolder(localResult.getValueHolder());
                return true;
            } else if (localResult.isLogicalExpired()) {
                recorder.markLocalLogicExpired(bizKey, bizField);
            } else {
                recorder.recordLocalPhysicalMiss(bizKey, bizField);
            }
        }

        CacheLookupResult<V> remoteResult = tryRemoteGet(key, field);
        if (remoteResult != null) {
            if (remoteResult.isHit()) {
                recorder.recordRemoteHit(bizKey, bizField);
                result.setValueHolder(remoteResult.getValueHolder());
                return true;
            } else if (remoteResult.isLogicalExpired()) {
                recorder.recordRemoteLogicExpired(bizKey, bizField);
            } else {
                recorder.recordRemotePhysicalMiss(bizKey, bizField);
            }
        }

        return false;
    }

    private boolean tryBlockAndRecord(String key, String field, K bizKey, F bizField, DynamicHashCacheAccessRecorder<K, F> recorder) {
        String logicKey = CacheKeyUtil.joinLogicalKey(key, field);
        if (localPpCache != null && localPpCache.isBlocked(logicKey)) {
            recorder.recordLocalBlocked(bizKey, bizField);
            return true;
        }
        if (remotePpCache != null && remotePpCache.isBlocked(logicKey)) {
            recorder.recordRemoteBlocked(bizKey, bizField);
            return true;
        }
        return false;
    }

    private DynamicHashCacheGetResult<K, F, V> end(DynamicHashCacheAccessRecorder<K, F> recorder,
                                                   DynamicHashCacheGetResult<K, F, V> result) {
        recorder.recordEnd();
        result.setTrace(CacheAccessContext.getDynamicHashTrace());
        return result;
    }
}
