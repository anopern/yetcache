package com.yetcache.agent.core.exception;

import com.yetcache.core.result.CacheAccessTrace;
import com.yetcache.core.result.CacheOutcome;
import lombok.Getter;

/**
 * 当缓存（或其初始化 / 刷新）不可用且业务不允许继续时抛出。
 *
 * <p>推荐用法：</p>
 * <pre>{@code
 * FlatHashCacheAgentResult<F,V> r = agent.listAllWithResult();
 * if (!r.isSuccess()) {
 *     throw new CacheUnavailableException(agent.getName(), r.outcome(), r.trace());
 * }
 * }</pre>
 */
@Getter
public class CacheUnavailableException extends RuntimeException {

    private final String          cacheName;
    private final CacheOutcome    outcome;
    private final CacheAccessTrace trace;

    /* ------------- 构造器 ------------- */

    public CacheUnavailableException(String cacheName,
                                     CacheOutcome outcome,
                                     CacheAccessTrace trace) {
        super(buildMessage(cacheName, outcome, trace));
        this.cacheName = cacheName;
        this.outcome   = outcome;
        this.trace     = trace;
    }

    public CacheUnavailableException(String cacheName,
                                     CacheOutcome outcome,
                                     CacheAccessTrace trace,
                                     Throwable cause) {
        super(buildMessage(cacheName, outcome, trace), cause);
        this.cacheName = cacheName;
        this.outcome   = outcome;
        this.trace     = trace;
    }

    /* ------------- 工具 ------------- */
    private static String buildMessage(String cache,
                                       CacheOutcome outcome,
                                       CacheAccessTrace trace) {
        return "Cache \"" + cache + "\" unavailable: outcome=" + outcome +
                ", reason=" + trace.reason() +
                ", latency=" + trace.latencyMicros() + "µs";
    }
}