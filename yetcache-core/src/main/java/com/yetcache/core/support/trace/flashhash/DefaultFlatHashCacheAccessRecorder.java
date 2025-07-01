package com.yetcache.core.support.trace;


import com.yetcache.core.cache.result.CacheAccessStatus;
import com.yetcache.core.cache.result.SourceLoadStatus;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.support.trace.flashhash.FlatHashCacheAccessRecorder;
import com.yetcache.core.support.trace.flashhash.FlatHashCacheAccessTrace;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public class DefaultFlatHashCacheAccessRecorder<F> implements FlatHashCacheAccessRecorder<F> {

    @Override
    public FlatHashCacheAccessTrace<F> recordStart() {
        FlatHashCacheAccessTrace<F> trace = new FlatHashCacheAccessTrace<>();
        CacheAccessContext.setFlatHashTrace(trace);
        trace.setStartMills(System.currentTimeMillis());
        return trace;
    }

    @Override
    public FlatHashCacheAccessTrace<F> recordStart(F bizField) {
        return recordStart(Collections.singletonList(bizField));
    }

    @Override
    public FlatHashCacheAccessTrace<F> recordStart(Collection<F> bizFields) {
        FlatHashCacheAccessTrace<F> trace = recordStart();
        for (F bizField : bizFields) {
            trace.getLocalStatusMap().put(bizField, CacheAccessStatus.PENDING);
            trace.getRemoteStatusMap().put(bizField, CacheAccessStatus.PENDING);
            trace.getLoadStatusMap().put(bizField, SourceLoadStatus.PENDING);
        }
        return trace;
    }


//    @Override
//    public CacheAccessTrace<K, F> recordStart(F bizField) {
//        CacheAccessTrace<K, F> trace = recordStart();
//        trace.setBizKey(bizKey);
//        trace.getLocalStatusMap().put(bizField, CacheAccessStatus.PENDING);
//        trace.getRemoteStatusMap().put(bizField, CacheAccessStatus.PENDING);
//        trace.getLoadStatusMap().put(bizField, SourceLoadStatus.PENDING);
//        return trace;
//    }

//    @Override
//    public CacheAccessTrace<K, F> recordBatchFieldStart(K bizKey, Collection<F> bizFields) {
//        CacheAccessTrace<K, F> trace = new CacheAccessTrace<>();
//        trace.setStartMills(System.currentTimeMillis());
//        trace.setBizKey(bizKey);
//        for (F bizField : bizFields) {
//            trace.getLocalStatusMap().put(bizField, CacheAccessStatus.PENDING);
//            trace.getRemoteStatusMap().put(bizField, CacheAccessStatus.PENDING);
//            trace.getLoadStatusMap().put(bizField, SourceLoadStatus.PENDING);
//        }
//        return trace;
//    }

    @Override
    public void recordLocalPhysicalMiss(F bizField) {
        CacheAccessContext.getFlatHashTrace().getLocalStatusMap().put(bizField, CacheAccessStatus.PHYSICAL_MISS);
    }

    @Override
    public void recordLocalHit(F bizField) {
        CacheAccessContext.getFlatHashTrace().getLocalStatusMap().put(bizField, CacheAccessStatus.HIT);
    }

    @Override
    public void markLocalLogicExpired(F bizField) {
        CacheAccessContext.getFlatHashTrace().getLocalStatusMap().put(bizField, CacheAccessStatus.LOGIC_EXPIRED);
    }

    @Override
    public void recordLocalBlocked(F bizField) {
        CacheAccessContext.getFlatHashTrace().getLocalStatusMap().put(bizField, CacheAccessStatus.BLOCKED);
    }

    @Override
    public void recordRemoteBlocked(F bizField) {
        CacheAccessContext.getFlatHashTrace().getRemoteStatusMap().put(bizField, CacheAccessStatus.BLOCKED);
    }

    public void recordEnd() {
        CacheAccessContext.getFlatHashTrace().setEndMills(System.currentTimeMillis());
    }

    @Override
    public void recordRemoteHit(F bizField) {
        CacheAccessContext.getFlatHashTrace().getRemoteStatusMap().put(bizField, CacheAccessStatus.HIT);
    }

    @Override
    public void recordRemotePhysicalMiss(F bizField) {
        CacheAccessContext.getFlatHashTrace().getRemoteStatusMap().put(bizField, CacheAccessStatus.PHYSICAL_MISS);
    }

    @Override
    public void recordRemoteLogicExpired(F bizField) {
        CacheAccessContext.getFlatHashTrace().getRemoteStatusMap().put(bizField, CacheAccessStatus.LOGIC_EXPIRED);
    }

    @Override
    public void recordSourceLoaded(F bizField) {
        CacheAccessContext.getFlatHashTrace().getLoadStatusMap().put(bizField, SourceLoadStatus.LOADED);
    }

    @Override
    public void recordSourceLoadNoValue(F bizField) {
        CacheAccessContext.getFlatHashTrace().getLoadStatusMap().put(bizField, SourceLoadStatus.NO_VALUE);
    }

    @Override
    public void recordSourceLoadError(F bizField) {
        CacheAccessContext.getFlatHashTrace().getLoadStatusMap().put(bizField, SourceLoadStatus.ERROR);
    }
}
