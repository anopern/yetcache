package com.yetcache.agent.agent.kv.port;


import com.yetcache.agent.agent.CacheAgentPort;
import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/8/11
 */
public interface KvCacheAgentRemovePort extends CacheAgentPort {
    CacheResult removeLocal(String key);
}
