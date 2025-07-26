package com.yetcache.core.result;

import java.io.Serializable;


/**
 * Top‑level contract that every YetCache read‑write operation returns.
 * Interceptors & metrics rely <strong>only</strong> on this interface.
 *
 * @author walter.yan
 * @since 2025/7/13
 */
public interface Result<T> extends Serializable {

    CacheOutcome outcome();

    T value();

    Throwable error();

    default boolean isSuccess() {
        return outcome() == CacheOutcome.SUCCESS;
    }
}