package com.yetcache.agent.regitry;

import com.yetcache.agent.BaseKVCacheAgent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
@Component
public final class CacheAgentRegistry {
    private final Map<String, BaseKVCacheAgent<?, ?>> kvCacheAgentMap = new ConcurrentHashMap<>();

    public void register(BaseKVCacheAgent agent) {
        checkCacheAgentName(agent.getCacheName());
        kvCacheAgentMap.put(agent.getCacheName(), agent);
    }

    private void checkCacheAgentName(String agentName) {
        if (kvCacheAgentMap.containsKey(agentName)) {
            throw new IllegalArgumentException("CacheAgent name already exists: " + agentName);
        }
    }
}
