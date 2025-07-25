package com.yetcache.agent.result;

import com.yetcache.core.result.CacheAccessResult;
import com.yetcache.core.result.CacheOutcome;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public abstract class AbstractCacheAgentResult<T> implements CacheAccessResult<T>, Serializable {
    private static final long serialVersionUID = 1L;
    protected String cacheName;
    protected CacheOutcome outcome;
    protected T value;
    protected Throwable error;

    AbstractCacheAgentResult(String cacheName,
                             CacheOutcome outcome,
                             T value) {
        this.cacheName = Objects.requireNonNull(cacheName, "agentName");
        this.outcome = Objects.requireNonNull(outcome, "outcome");
        this.value = value;
    }

    @Override
    public CacheOutcome outcome() {
        return outcome;
    }

    @Override
    public T value() {
        return value;
    }
}
