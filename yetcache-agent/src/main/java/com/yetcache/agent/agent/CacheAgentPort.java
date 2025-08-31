package com.yetcache.agent.agent;

/**
 * @author walter.yan
 * @since 2025/8/27
 */
public interface CacheAgentPort {
    boolean support(ChainKey sbKey);
}
