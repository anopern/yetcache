package com.yetcache.agent.core.exception;

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

    /* ------------- 构造器 ------------- */

    public CacheUnavailableException(String cacheName,
                                     CacheOutcome outcome) {
        super(buildMessage(cacheName, outcome));
        this.cacheName = cacheName;
        this.outcome   = outcome;
    }

    public CacheUnavailableException(String cacheName,
                                     CacheOutcome outcome,
                                     Throwable cause) {
        super(buildMessage(cacheName, outcome), cause);
        this.cacheName = cacheName;
        this.outcome   = outcome;
    }

    /* ------------- 工具 ------------- */
    private static String buildMessage(String cache,
                                       CacheOutcome outcome) {
        return "Cache \"" + cache + "\" unavailable: outcome=" + outcome;
    }
}