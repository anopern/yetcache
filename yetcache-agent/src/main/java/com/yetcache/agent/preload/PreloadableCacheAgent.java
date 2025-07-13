package com.yetcache.agent.preload;

import com.yetcache.core.result.CacheAccessResult;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface PreloadableCacheAgent {
    int getPriority();

    String getCacheAgentName();

    <R extends CacheAccessResult<?>> R preload();
}
