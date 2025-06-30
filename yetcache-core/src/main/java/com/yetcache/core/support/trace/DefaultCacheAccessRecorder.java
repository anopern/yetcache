package com.yetcache.core.support.trace;


import com.yetcache.core.cache.result.CacheAccessStatus;
import com.yetcache.core.cache.result.SourceLoadStatus;
import com.yetcache.core.context.CacheAccessContext;

import java.util.Collection;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public class DefaultCacheAccessRecorder<K, F> implements CacheAccessRecorder<K, F> {

    @Override
    public CacheAccessTrace<K, F> onStart(K bizKey) {
        return onStart(bizKey, null);
    }

    @Override
    public CacheAccessTrace<K, F> onStart(K bizKey, F bizField) {
        CacheAccessTrace<K, F> trace = new CacheAccessTrace<>();
        CacheAccessContext.setTrace(trace);
        trace.setStartMills(System.currentTimeMillis());
        trace.setBizKey(bizKey);
        trace.getLocalStatusMap().put(bizField, CacheAccessStatus.PENDING);
        trace.getRemoteStatusMap().put(bizField, CacheAccessStatus.PENDING);
        trace.getLoadStatusMap().put(bizField, SourceLoadStatus.PENDING);
        return trace;
    }

    @Override
    public CacheAccessTrace<K, F> onBatchStart(K bizKey, Collection<F> bizFields) {
        CacheAccessTrace<K, F> trace = new CacheAccessTrace<>();
        trace.setStartMills(System.currentTimeMillis());
        trace.setBizKey(bizKey);
        for (F bizField : bizFields) {
            trace.getLocalStatusMap().put(bizField, CacheAccessStatus.PENDING);
            trace.getRemoteStatusMap().put(bizField, CacheAccessStatus.PENDING);
            trace.getLoadStatusMap().put(bizField, SourceLoadStatus.PENDING);
        }
        return trace;
    }

    @Override
    public void localPhysicalMiss(F bizField) {
        CacheAccessContext.getTrace().getLocalStatusMap().put(bizField, CacheAccessStatus.PHYSICAL_MISS);
    }

    @Override
    public void localHit(F bizField) {
        CacheAccessContext.getTrace().getLocalStatusMap().put(bizField, CacheAccessStatus.HIT);
    }

    @Override
    public void localLogicExpired(F bizField) {
        CacheAccessContext.getTrace().getLocalStatusMap().put(bizField, CacheAccessStatus.LOGIC_EXPIRED);
    }

    @Override
    public void localBlocked(F bizField) {
        CacheAccessContext.getTrace().getLocalStatusMap().put(bizField, CacheAccessStatus.BLOCKED);
    }

    @Override
    public void remoteBlocked(F bizField) {
        CacheAccessContext.getTrace().getRemoteStatusMap().put(bizField, CacheAccessStatus.BLOCKED);
    }

    public void end() {
        CacheAccessContext.getTrace().setEndMills(System.currentTimeMillis());
    }

    @Override
    public void remoteHit(F bizField) {
        CacheAccessContext.getTrace().getRemoteStatusMap().put(bizField, CacheAccessStatus.HIT);
    }

    @Override
    public void remotePhysicalMiss(F bizField) {
        CacheAccessContext.getTrace().getRemoteStatusMap().put(bizField, CacheAccessStatus.PHYSICAL_MISS);
    }

    @Override
    public void remoteLogicExpired(F bizField) {
        CacheAccessContext.getTrace().getRemoteStatusMap().put(bizField, CacheAccessStatus.LOGIC_EXPIRED);
    }

    @Override
    public void sourceLoaded(F bizField) {
        CacheAccessContext.getTrace().getLoadStatusMap().put(bizField, SourceLoadStatus.LOADED);
    }

    @Override
    public void sourceLoadNoValue(F bizField) {
        CacheAccessContext.getTrace().getLoadStatusMap().put(bizField, SourceLoadStatus.NO_VALUE);
    }

    @Override
    public void sourceLoadError(F bizField) {
        CacheAccessContext.getTrace().getLoadStatusMap().put(bizField, SourceLoadStatus.ERROR);
    }
}
