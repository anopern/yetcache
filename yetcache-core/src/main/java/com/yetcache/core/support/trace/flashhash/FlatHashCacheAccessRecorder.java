package com.yetcache.core.support.trace.flashhash;

import com.yetcache.core.support.trace.CacheAccessRecorder;

import java.util.Collection;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
public interface FlatHashCacheAccessRecorder<F> extends CacheAccessRecorder {
    FlatHashCacheAccessTrace<F> recordStart();

    FlatHashCacheAccessTrace<F> recordStart(F bizField);

    FlatHashCacheAccessTrace<F> recordStart(Collection<F> bizField);

    void recordLocalPhysicalMiss(F bizField);

    void markLocalLogicExpired(F bizField);

    void recordLocalHit(F bizField);

    void recordLocalBlocked(F bizField);

    void recordRemoteBlocked(F bizField);

    void recordEnd();

    void recordRemoteHit(F bizField);

    void recordRemotePhysicalMiss(F bizField);

    void recordRemoteLogicExpired(F bizField);

    void recordSourceLoaded(F bizField);

    void recordSourceLoadNoValue(F bizField);

    void recordSourceLoadError(F bizField);
}
