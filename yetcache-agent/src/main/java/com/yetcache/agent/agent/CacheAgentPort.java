package com.yetcache.agent.agent;

import com.yetcache.agent.agent.StructureBehaviorKey;

/**
 * @author walter.yan
 * @since 2025/8/27
 */
public interface CacheAgentPort {
    boolean support(StructureBehaviorKey sbKey);
}
