package com.yetcache.agent.result;

import com.yetcache.core.result.CacheAccessResult;
import com.yetcache.core.result.CacheAccessTrace;
import com.yetcache.core.result.CacheOutcome;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public class CacheAgentResult <T> implements CacheAccessResult<T>, Serializable {

    private static final long serialVersionUID = 1L;

    /* ---------- core ---------- */
    private final CacheOutcome outcome;
    private final T value;
    private final CacheAccessTrace trace;

    /* ---------- aggregation-specific ---------- */
    private final String agentName;  // 业务侧标识，如 "user-config-cache"
    private final boolean fromCache; // true=直接命中缓存, false=回源(如preload/init)

    /* ------------------------------------------------------------------ */
    CacheAgentResult(CacheOutcome outcome,
                     T value,
                     CacheAccessTrace trace,
                     String agentName,
                     boolean fromCache) {
        this.outcome   = Objects.requireNonNull(outcome, "outcome");
        this.value     = value;
        this.trace     = Objects.requireNonNull(trace, "trace");
        this.agentName = Objects.requireNonNull(agentName, "agentName");
        this.fromCache = fromCache;
    }

    /* ===== static factories ========================================== */
    public static <T> CacheAgentResult<T> success(String agentName, T value, boolean fromCache) {
        return new CacheAgentResult<>(
                CacheOutcome.SUCCESS,
                value,
                CacheAccessTrace.start().success(),
                agentName,
                fromCache
        );
    }

    /** 读操作未命中（写操作不用此场景）。 */
    public static <T> CacheAgentResult<T> miss(String agentName) {
        return new CacheAgentResult<>(
                CacheOutcome.MISS,
                null,
                CacheAccessTrace.start().success(),
                agentName,
                true
        );
    }

    /** 因限流/阻断被拒绝。 */
    public static <T> CacheAgentResult<T> block(String agentName, String reason) {
        return new CacheAgentResult<>(
                CacheOutcome.BLOCK,
                null,
                CacheAccessTrace.start().block(reason),
                agentName,
                true
        );
    }

    /** 异常失败。 */
    public static <T> CacheAgentResult<T> fail(String agentName, Throwable ex) {
        return new CacheAgentResult<>(
                CacheOutcome.FAIL,
                null,
                CacheAccessTrace.start().fail(ex),
                agentName,
                true
        );
    }

    /* ===== getters =================================================== */
    @Override public CacheOutcome outcome()          { return outcome; }
    @Override public T value()                       { return value; }
    @Override public CacheAccessTrace trace()        { return trace; }

    public String agentName()  { return agentName; }
    public boolean fromCache() { return fromCache; }

    /* ===== copy-with-trace (for invoke模板) ========================== */
    @Override
    public CacheAgentResult<T> withTrace(CacheAccessTrace trace) {
        return new CacheAgentResult<>(outcome, value, trace, agentName, fromCache);
    }

    /* ===== helpers =================================================== */
    public boolean isSuccess() { return outcome == CacheOutcome.SUCCESS; }

    @Override public String toString() {
        return "CacheAgentResult{" +
                "agent='" + agentName + '\'' +
                ", outcome=" + outcome +
                ", fromCache=" + fromCache +
                ", latency=" + trace.latencyMicros() + "µs" +
                '}';
    }
}
