package com.yetcache.agent.interceptor;

import com.yetcache.agent.agent.ChainKey;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
public interface CacheInvocationCommand {
    ChainKey chainKey();
}
