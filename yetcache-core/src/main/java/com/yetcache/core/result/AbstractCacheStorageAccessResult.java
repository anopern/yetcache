package com.yetcache.core.result;

import lombok.Data;

/**
 * Default unified implementation of CacheAccessResult for storage layer.
 * Immutable, traceable, and behavior-focused.
 *
 * @author walter.yan
 * @since 2025/7/15
 */
@Data
public abstract class AbstractCacheStorageAccessResult<T> implements CacheAccessResult<T> {
    protected CacheOutcome outcome;
    protected T value;
    protected Throwable error;

    public AbstractCacheStorageAccessResult(CacheOutcome outcome, T value) {
        this.outcome = outcome;
        this.value = value;
    }

    public AbstractCacheStorageAccessResult(CacheOutcome outcome, T value, Throwable error) {
        this.outcome = outcome;
        this.value = value;
        this.error = error;
    }

    @Override
    public CacheOutcome outcome() {
        return this.outcome;
    }

    @Override
    public T value() {
        return this.value;
    }
}