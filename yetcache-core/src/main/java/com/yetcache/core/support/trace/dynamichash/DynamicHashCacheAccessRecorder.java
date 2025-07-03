package com.yetcache.core.support.trace.dynamichash;

import com.yetcache.core.support.trace.CacheAccessRecorder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
public interface DynamicHashCacheAccessRecorder<K, F> extends CacheAccessRecorder {

    void recordStart(K bizKey, F bizField);

    void recordStart(Map<K, List<F>> bizKeyMap);

    void recordLocalPhysicalMiss(K bizKey, F bizField);

    void markLocalLogicExpired(K bizKey, F bizField);

    void recordLocalHit(K bizKey, F bizField);

    void recordLocalBlocked(K bizKey, F bizField);

    void recordRemoteBlocked(K bizKey, F bizField);

    void recordRemoteHit(K bizKey, F bizField);

    void recordRemotePhysicalMiss(K bizKey, F bizField);

    void recordRemoteLogicExpired(K bizKey, F bizField);

    void recordSourceLoaded(K bizKey, F bizField);

    void recordSourceLoadNoValue(K bizKey, F bizField);

    void recordSourceLoadFailed(K bizKey, F bizField, Exception e);

    void recordExceptionBeforeLoop();

    void recordSourceLoadAllNoValue();
}
