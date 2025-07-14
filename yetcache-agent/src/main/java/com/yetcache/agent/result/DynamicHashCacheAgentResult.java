package com.yetcache.agent.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.result.CacheAccessTrace;
import com.yetcache.core.result.CacheOutcome;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public class DynamicHashCacheAgentResult <K,F,V>  extends CacheAgentResult<Map<F, CacheValueHolder<V>>> {
    DynamicHashCacheAgentResult(CacheOutcome outcome, Map<F, CacheValueHolder<V>> value, CacheAccessTrace trace,
                                String componentName, boolean fromCache) {
        super(outcome, value, trace, componentName, fromCache);
    }
}
