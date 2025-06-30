package com.yetcache.core.support.trace;

import java.util.Collection;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public interface CacheAccessRecorder<K, F> {
    CacheAccessTrace<K, F> onStart(K bizKey);

    CacheAccessTrace<K, F> onStart(K bizKey, F bizField);

    CacheAccessTrace<K, F> onBatchStart(K bizKey, Collection<F> bizField);

    void localPhysicalMiss(F bizField);

    void localLogicExpired(F bizField);

    void localHit(F bizField);

    void localBlocked(F bizField);

    void remoteBlocked(F bizField);

    void end();

    void remoteHit(F bizField);

    void remotePhysicalMiss(F bizField);

    void remoteLogicExpired(F bizField);

    void sourceLoaded(F bizField);

    void sourceLoadNoValue(F bizField);

    void sourceLoadError(F bizField);
}
