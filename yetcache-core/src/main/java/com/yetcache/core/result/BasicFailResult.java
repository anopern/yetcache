package com.yetcache.core.result;

import java.util.Objects;

/**
 * Minimal fail result impl used by the invoke() template as a fallback.
 *
 * @author walter.yan
 * @since 2025/7/13
 */
public final class BasicFailResult implements Result<Void> {
    private final CacheOutcome outcome;
    private final CacheAccessTrace trace;
    private final String note;

    private BasicFailResult(CacheOutcome outcome, CacheAccessTrace trace, String note) {
        this.outcome = outcome;
        this.trace = trace;
        this.note = note;
    }

    public static BasicFailResult of(String method, Throwable ex, CacheAccessTrace trace) {
        Objects.requireNonNull(ex, "exception");
        return new BasicFailResult(CacheOutcome.FAIL, trace,
                method + ":" + ex.getClass().getSimpleName());
    }

    @Override
    public CacheOutcome outcome() {
        return outcome;
    }

    @Override
    public Void value() {
        return null;
    }

    @Override
    public CacheAccessTrace trace() {
        return trace;
    }

    @Override
    public BasicFailResult withTrace(CacheAccessTrace trace) {
        return new BasicFailResult(this.outcome, trace, this.note);
    }
}
