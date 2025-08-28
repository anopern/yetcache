package com.yetcache.agent.interceptor;

import com.yetcache.agent.core.StructureBehaviorKey;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
public interface CacheInvocationCommand {
    String cacheAgentName();
    StructureBehaviorKey sbKey();
}
