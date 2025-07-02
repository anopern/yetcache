package com.yetcache.core.support.trace.dynamichash;


import com.yetcache.core.cache.result.CacheAccessStatus;
import com.yetcache.core.cache.result.SourceLoadStatus;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.support.trace.CacheBatchAccessStatus;
import com.yetcache.core.support.trace.flashhash.FlatHashCacheAccessRecorder;
import com.yetcache.core.support.trace.flashhash.FlatHashCacheAccessTrace;
import org.checkerframework.checker.units.qual.K;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public class DefaultDynamicHashCacheAccessRecorder<K, F> implements DynamicHashCacheAccessRecorder<K, F> {
    private boolean hasAnyFieldSuccess = false;
    private boolean hasAnyFieldFail = false;
    private boolean exceptionBeforeLoop = false;

    @Override
    public void recordStart() {
        DynamicHashCacheAccessTrace<K, F> trace = new DynamicHashCacheAccessTrace<>();
        trace.setBatchStatus(CacheBatchAccessStatus.NOT_EXECUTED);
        CacheAccessContext.setDynamicHashTrace(trace);
        trace.setStartMills(System.currentTimeMillis());
    }

    @Override
    public void recordStart(K bizKey, F bizField) {
        Map<K, Collection<F>> map = new HashMap<>();
        map.put(bizKey, Collections.singletonList(bizField));
        recordStart(map);
    }

    @Override
    public void recordStart(Map<K, Collection<F>> bizKeyMap) {
        recordStart();
        for (K bizKey : bizKeyMap.keySet()) {
            for (F bizField : bizKeyMap.get(bizKey)) {
                getTrace().getLocalStatusMap().get(bizKey).put(bizField, CacheAccessStatus.PENDING);
                getTrace().getRemoteStatusMap().get(bizKey).put(bizField, CacheAccessStatus.PENDING);
                getTrace().getLoadStatusMap().get(bizKey).put(bizField, SourceLoadStatus.PENDING);
            }
        }
    }

    @Override
    public void recordLocalPhysicalMiss(K bizKey, F bizField) {
        getTrace().getLocalStatusMap().get(bizKey).put(bizField, CacheAccessStatus.PHYSICAL_MISS);
    }

    @Override
    public void recordLocalHit(K bizKey, F bizField) {
        getTrace().getLocalStatusMap().get(bizKey).put(bizField, CacheAccessStatus.HIT);
    }

    @Override
    public void markLocalLogicExpired(K bizKey, F bizField) {
        getTrace().getLocalStatusMap().get(bizKey).put(bizField, CacheAccessStatus.LOGIC_EXPIRED);
    }

    @Override
    public void recordLocalBlocked(K bizKey, F bizField) {
        getTrace().getLocalStatusMap().get(bizKey).put(bizField, CacheAccessStatus.BLOCKED);
    }

    @Override
    public void recordRemoteBlocked(K bizKey, F bizField) {
        getTrace().getRemoteStatusMap().get(bizKey).put(bizField, CacheAccessStatus.BLOCKED);
    }


    @Override
    public void recordRemoteHit(K bizKey, F bizField) {
        getTrace().getRemoteStatusMap().get(bizKey).put(bizField, CacheAccessStatus.HIT);
    }

    @Override
    public void recordRemotePhysicalMiss(K bizKey, F bizField) {
        getTrace().getRemoteStatusMap().get(bizKey).put(bizField, CacheAccessStatus.PHYSICAL_MISS);
    }

    @Override
    public void recordRemoteLogicExpired(K bizKey, F bizField) {
        getTrace().getRemoteStatusMap().get(bizKey).put(bizField, CacheAccessStatus.LOGIC_EXPIRED);
    }

    @Override
    public void recordSourceLoaded(K bizKey, F bizField) {
        getTrace().getLoadStatusMap().get(bizKey).put(bizField, SourceLoadStatus.LOADED);
        hasAnyFieldSuccess = true;
    }

    @Override
    public void recordSourceLoadNoValue(K bizKey, F bizField) {
        getTrace().getLoadStatusMap().get(bizKey).put(bizField, SourceLoadStatus.NO_VALUE);
    }

    @Override
    public void recordSourceLoadFailed(K bizKey, F bizField) {
        getTrace().getLoadStatusMap().get(bizKey).put(bizField, SourceLoadStatus.ERROR);
        hasAnyFieldFail = true;
    }

    @Override
    public void recordExceptionBeforeLoop() {
        getTrace().setBatchStatus(CacheBatchAccessStatus.EXCEPTION_BEFORE_LOOP);
    }

    @Override
    public void recordSourceLoadAllNoValue() {
        getTrace().setBatchStatus(CacheBatchAccessStatus.ALL_FAILED);
    }

    @Override
    public void recordEnd() {
        // 若 trace 中已被外部 catch 显式设置（如 EXCEPTION_BEFORE_LOOP），则不覆盖
        if (getTrace().getBatchStatus() == null || getTrace().getBatchStatus() == CacheBatchAccessStatus.NOT_EXECUTED) {
            if (hasAnyFieldSuccess && hasAnyFieldFail) {
                getTrace().setBatchStatus(CacheBatchAccessStatus.PARTIAL_SUCCESS);
            } else if (hasAnyFieldSuccess) {
                getTrace().setBatchStatus(CacheBatchAccessStatus.ALL_SUCCESS);
            } else {
                getTrace().setBatchStatus(CacheBatchAccessStatus.ALL_FAILED);
            }
        }

        getTrace().setEndMills(System.currentTimeMillis());
    }

    private DynamicHashCacheAccessTrace<K, F> getTrace() {
        return CacheAccessContext.getDynamicHashTrace();
    }
}
