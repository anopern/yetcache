package com.yetcache.core.result;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public class BaseResult<T> implements Result<T> {
    protected String componentName;
    protected CacheOutcome outcome;
    protected T value;
    protected Throwable error;

    public BaseResult(String componentName, CacheOutcome outcome, T value, Throwable error) {
        this.componentName = componentName;
        this.outcome = outcome;
        this.value = value;
        this.error = error;
    }

    public static <T> BaseResult<T> notFound(String componentName) {
        return new BaseResult<>(componentName, CacheOutcome.NOT_FOUND, null, null);
    }

    public String getComponentName() {
        return this.componentName;
    }

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
