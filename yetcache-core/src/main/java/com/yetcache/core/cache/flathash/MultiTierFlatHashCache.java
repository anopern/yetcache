package com.yetcache.core.cache.flathash;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.core.cache.AbstractMultiTierCache;
import com.yetcache.core.cache.loader.FlatHashCacheLoader;
import com.yetcache.core.cache.result.flathash.FlatHashCacheResult;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.config.singlehash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.FlatHashKeyConverter;
import com.yetcache.core.support.result.CacheLookupResult;
import com.yetcache.core.support.trace.CacheBatchAccessStatus;
import com.yetcache.core.support.trace.flashhash.DefaultFlatHashCacheAccessRecorder;
import com.yetcache.core.support.trace.flashhash.FlatHashCacheAccessRecorder;
import com.yetcache.core.support.util.CacheParamChecker;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.*;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class MultiTierFlatHashCache<F, V> extends AbstractMultiTierCache<F>
        implements FlatHashCache<F, V> {
    private String cacheName;
    private final MultiTierFlatHashCacheConfig config;
    private final FlatHashCacheLoader<F, V> cacheLoader;
    private CaffeineFlatHashCache<V> localCache;
    private RedisFlatHashCache<V> remoteCache;
    private FlatHashKeyConverter keyConverter;
    private FieldConverter<F> fieldConverter;

    public MultiTierFlatHashCache(String cacheName,
                                  MultiTierFlatHashCacheConfig config,
                                  RedissonClient rClient,
                                  FlatHashCacheLoader<F, V> cacheLoader,
                                  FlatHashKeyConverter keyConverter,
                                  FieldConverter<F> fieldConverter) {
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
    public V get(F bizField) {
        FlatHashCacheResult<F, V> result = getWithResult(bizField);
        log.debug("CacheResult: {}", result);
        CacheValueHolder<V> valueHolder = result.getValueHolder();
        if (valueHolder != null) {
            return valueHolder.getValue();
        }
        return null;
    }

    @Override
    public FlatHashCacheResult<F, V> getWithResult(F bizField) {
        try {
            CacheParamChecker.failIfNull(bizField, cacheName);
            DefaultFlatHashCacheAccessRecorder<F> recorder = new DefaultFlatHashCacheAccessRecorder<>();
            recorder.recordStart(bizField);

            String key = keyConverter.convert();
            String field = fieldConverter.convert(bizField);
            FlatHashCacheResult<F, V> result = new FlatHashCacheResult<>();

            // --- 封装后的三段核心逻辑 ---
            if (tryBlockAndRecord(bizField, recorder)) {
                return end(recorder, result);
            }

            if (tryCacheLookupAndRecord(key, field, bizField, recorder, result)) {
                return end(recorder, result);
            }

            return end(recorder, result);
        } finally {
            CacheAccessContext.clear();
        }
    }

    @Override
    public CacheBatchAccessStatus refreshAll() {
        FlatHashCacheResult<F, V> result = refreshAllWithResult();
        log.debug("refreshAll result: {}", result);
        CacheBatchAccessStatus status = result.getTrace().getBatchStatus();
        if (CacheBatchAccessStatus.ALL_SUCCESS.equals(status)) {
            return status;
        }
        log.error("FlatHash refreshAll NOT all success, cacheName={}", cacheName);
        return status;
    }

    @Override
    public FlatHashCacheResult<F, V> refreshAllWithResult() {
        FlatHashCacheAccessRecorder<F> recorder = new DefaultFlatHashCacheAccessRecorder<>();
        FlatHashCacheResult<F, V> result = new FlatHashCacheResult<>();
        try {
            recorder.recordStart();
            Map<F, V> map = cacheLoader.loadAll();
            if (CollUtil.isEmpty(map)) {
                recorder.recordSourceLoadAllNoValue();
                return end(recorder, result);
            }
            String key = keyConverter.convert();
            Map<String, CacheValueHolder<V>> remoteMap = new HashMap<>();
            Map<String, CacheValueHolder<V>> localMap = new HashMap<>();
            for (Map.Entry<F, V> entry : map.entrySet()) {
                try {
                    F fieldBizKey = entry.getKey();
                    V value = entry.getValue();
                    if (fieldBizKey == null || value == null) {
                        continue;
                    }

                    String field = fieldConverter.convert(fieldBizKey);
                    CacheValueHolder<V> remoteHolder = CacheValueHolder.wrap(value, config.getRemote().getTtlSecs());
                    CacheValueHolder<V> localHolder = CacheValueHolder.wrap(value, config.getLocal().getTtlSecs());

                    remoteMap.put(field, remoteHolder);
                    localMap.put(field, localHolder);

                    recorder.recordSourceLoaded(fieldBizKey); // 单条打点
                } catch (Exception fe) {
                    recorder.recordSourceLoadFailed(entry.getKey()); // field 级别记录
                    log.warn("FlatHash refreshAll field load exception: field={}", entry.getKey(), fe);
                }
            }
            if (CacheBatchAccessStatus.ALL_SUCCESS
                    == CacheAccessContext.getContext().getFlatHashTrace().getBatchStatus()) {
                if (remoteCache != null && !remoteMap.isEmpty()) {
                    remoteCache.putAll(key, remoteMap);
                }
                if (localCache != null && !localMap.isEmpty()) {
                    localCache.putAll(key, localMap);
                }
            } else {
                log.warn("FlatHash refreshAll not all success, DO NOT refresh remote and local cache. cacheName={}",
                        cacheName);
            }
            return end(recorder, result);
        } catch (Exception e) {
            log.warn("FlatHash refreshAll load fail(exception before loop)，cacheName={}", cacheName, e);
            recorder.recordExceptionBeforeLoop();
            return end(recorder, result);
        } finally {
            CacheAccessContext.clear();
        }
    }

    private boolean tryBlockAndRecord(F bizField, FlatHashCacheAccessRecorder<F> recorder) {
        if (localPpCache != null && localPpCache.isBlocked(bizField)) {
            recorder.recordLocalBlocked(bizField);
            return true;
        }
        if (remotePpCache != null && remotePpCache.isBlocked(bizField)) {
            recorder.recordRemoteBlocked(bizField);
            return true;
        }
        return false;
    }

    private boolean tryCacheLookupAndRecord(String key, String field, F bizField,
                                            FlatHashCacheAccessRecorder<F> recorder,
                                            FlatHashCacheResult<F, V> result) {
        CacheLookupResult<V> localResult = tryLocalGet(key, field);
        if (localResult != null) {
            if (localResult.isHit()) {
                recorder.recordLocalHit(bizField);
                result.setValueHolder(localResult.getValueHolder());
                return true;
            } else if (localResult.isLogicalExpired()) {
                recorder.markLocalLogicExpired(bizField);
            } else {
                recorder.recordLocalPhysicalMiss(bizField);
            }
        }

        CacheLookupResult<V> remoteResult = tryRemoteGet(key, field);
        if (remoteResult != null) {
            if (remoteResult.isHit()) {
                recorder.recordRemoteHit(bizField);
                result.setValueHolder(remoteResult.getValueHolder());
                return true;
            } else if (remoteResult.isLogicalExpired()) {
                recorder.recordRemoteLogicExpired(bizField);
            } else {
                recorder.recordRemotePhysicalMiss(bizField);
            }
        }

        return false;
    }

    private CacheLookupResult<V> tryLocalGet(String key, String field) {
        if (localCache == null) {
            return null;
        }
        CacheLookupResult<V> result = new CacheLookupResult<>();
        CacheValueHolder<V> holder = localCache.getIfPresent(key, field);
        if (holder == null) {
            result.physicalMiss();
        } else {
            if (holder.isNotLogicExpired()) {
                result.hit();
                result.setValueHolder(holder);
            } else {
                result.logicalExpired();
            }
        }
        return result;
    }

    private CacheLookupResult<V> tryRemoteGet(String key, String field) {
        if (remoteCache == null) {
            return null;
        }
        CacheLookupResult<V> result = new CacheLookupResult<>();
        CacheValueHolder<V> holder = remoteCache.getIfPresent(key, field);
        if (holder == null) {
            result.physicalMiss();
        } else {
            if (holder.isNotLogicExpired()) {
                result.hit();
                result.setValueHolder(holder);
            } else {
                result.logicalExpired();
            }
        }
        return result;
    }

    private FlatHashCacheResult<F, V> end(FlatHashCacheAccessRecorder<F> recorder,
                                          FlatHashCacheResult<F, V> result) {
        recorder.recordEnd();
        result.setTrace(CacheAccessContext.getFlatHashTrace());
        return result;
    }
}
