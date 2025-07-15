package com.yetcache.agent.result;

import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.result.CacheAccessResult;
import com.yetcache.core.result.CacheAccessTrace;
import com.yetcache.core.result.CacheOutcome;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public class CacheAgentResult<T> implements CacheAccessResult<T>, Serializable {

    private static final long serialVersionUID = 1L;

    /* ---------- core ---------- */
    protected final CacheOutcome outcome;
    protected final T value;
    protected final HitTier hitTier;
    protected final CacheAccessTrace trace;

    /* ---------- aggregation-specific ---------- */
    protected final String componentName;  // 业务侧标识，如 "user-config-cache"

    /* ------------------------------------------------------------------ */
    CacheAgentResult(CacheOutcome outcome,
                     T value,
                     HitTier hitTier,
                     CacheAccessTrace trace,
                     String componentName) {
        this.outcome = Objects.requireNonNull(outcome, "outcome");
        this.value = value;
        this.hitTier = hitTier;
        this.trace = Objects.requireNonNull(trace, "trace");
        this.componentName = Objects.requireNonNull(componentName, "agentName");
    }


    /* ===== getters =================================================== */
    @Override
    public CacheOutcome outcome() {
        return outcome;
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public CacheAccessTrace trace() {
        return trace;
    }

    public String componentName() {
        return componentName;
    }

    /* ===== copy-with-trace (for invoke模板) ========================== */
    @Override
    public CacheAgentResult<T> withTrace(CacheAccessTrace trace) {
        return new CacheAgentResult<>(outcome, value, hitTier, trace, componentName);
    }

    /* ===== helpers =================================================== */
    public boolean isSuccess() {
        return outcome == CacheOutcome.SUCCESS;
    }

    @Override
    public String toString() {
        return "CacheAgentResult{" +
                "agent='" + componentName + '\'' +
                ", outcome=" + outcome +
                ", latency=" + trace.latencyMicros() + "µs" +
                '}';
    }
}
