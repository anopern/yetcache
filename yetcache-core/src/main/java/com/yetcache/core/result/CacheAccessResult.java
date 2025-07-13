package com.yetcache.core.result;

import java.io.Serializable;


/**
 * Top‑level contract that every YetCache read‑write operation returns.
 * Interceptors & metrics rely <strong>only</strong> on this interface.
 *
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheAccessResult<T> extends Serializable {

    CacheOutcome outcome();

    T value();

    CacheAccessTrace trace();

    /**
     * Return a shallow copy holding the supplied trace.
     */
    CacheAccessResult<T> withTrace(CacheAccessTrace trace);
}