package com.yetcache.agent.core.port;


import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/8/11
 */
public interface KvCacheAgentRemovePort extends CacheAgentPort{
    CacheResult removeLocal(Object bizKey);
}
