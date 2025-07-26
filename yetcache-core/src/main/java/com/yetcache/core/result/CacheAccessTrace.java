//package com.yetcache.core.result;
//
//import java.io.Serializable;
//import java.time.Instant;
//
//
///**
// * Lightweight trace object placed inside every result so that interceptors & callers can build a full chain.
// * @author walter.yan
// * @since 2025/7/13
// */
//public final class CacheAccessTrace implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    private final long startNanos;
//    private final long endNanos;
//    private final String reason;      // 说明文字（BLOCK / FAIL）
//    private final Throwable exception; // 失败异常
//
//    /* ------------------------------------------------------------------------
//     * Static factory – always start with NOW
//     * --------------------------------------------------------------------- */
//    public static CacheAccessTrace start() {
//        long now = System.nanoTime();
//        return new CacheAccessTrace(now, 0L, null, null);
//    }
//
//    /* ------------------------------------------------------------------------
//     * Chain terminators – return new instance with end-timestamp
//     * --------------------------------------------------------------------- */
//    public CacheAccessTrace success() {
//        return new CacheAccessTrace(startNanos, System.nanoTime(), null, null);
//    }
//
//    public CacheAccessTrace block(String reason) {
//        return new CacheAccessTrace(startNanos, System.nanoTime(), reason, null);
//    }
//
//    public CacheAccessTrace fail(Throwable ex) {
//        return new CacheAccessTrace(startNanos, System.nanoTime(),
//                ex == null ? "unknown" : ex.getMessage(), ex);
//    }
//
//    /* -------------------------------------------------------------------- */
//    private CacheAccessTrace(long startNanos, long endNanos,
//                             String reason, Throwable exception) {
//        this.startNanos = startNanos;
//        this.endNanos = endNanos;
//        this.reason = reason;
//        this.exception = exception;
//    }
//
//    // ---------------- getters ----------------
//    public long startNanos() {
//        return startNanos;
//    }
//
//    public long endNanos() {
//        return endNanos;
//    }
//
//    public String reason() {
//        return reason;
//    }
//
//    public Throwable exception() {
//        return exception;
//    }
//
//    /* convenience */
//    public long latencyMicros() {
//        return (endNanos == 0L ? 0L : (endNanos - startNanos) / 1_000);
//    }
//
//    @Override
//    public String toString() {
//        return "Trace@" + Instant.now() + " latency=" + latencyMicros() +
//                "µs reason=" + reason;
//    }
//}
