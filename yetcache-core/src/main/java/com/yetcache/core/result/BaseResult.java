package com.yetcache.core.result;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public class BaseResult<T> implements Result<T> {
    protected CacheOutcome outcome;
    protected T value;
    protected Throwable error;

    @Override
    public CacheOutcome outcome() {
        return this.outcome;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public Throwable error() {
        return this.error;
    }
}
