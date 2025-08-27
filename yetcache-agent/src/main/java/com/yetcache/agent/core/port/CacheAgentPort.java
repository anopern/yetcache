package com.yetcache.agent.core.port;

import com.yetcache.agent.interceptor.StructureBehaviorKey;

/**
 * @author walter.yan
 * @since 2025/8/27
 */
public interface CacheAgentPort {
    boolean support(StructureBehaviorKey sbKey);
}
