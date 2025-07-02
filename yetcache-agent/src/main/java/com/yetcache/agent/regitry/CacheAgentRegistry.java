package com.yetcache.agent.regitry;

import com.yetcache.agent.BaseConfigCacheAgent;
import com.yetcache.agent.BaseDynamicHashCacheAgent;
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
    private final Map<String, BaseConfigCacheAgent<?, ?>> flatHashCacheAgentMap = new ConcurrentHashMap<>();
    private final Map<String, BaseDynamicHashCacheAgent<?, ?, ?>> dynamicHashCacheAgentMap = new ConcurrentHashMap<>();

    public void register(BaseKVCacheAgent agent) {
        checkCacheAgentName(agent.getCacheName());
        kvCacheAgentMap.put(agent.getCacheName(), agent);
    }

    public void register(BaseConfigCacheAgent<?, ?> agent) {
        checkCacheAgentName(agent.getCacheName());
        flatHashCacheAgentMap.put(agent.getCacheName(), agent);
    }

    public void register(BaseDynamicHashCacheAgent<?, ?, ?> agent) {
        checkCacheAgentName(agent.getCacheName());
        dynamicHashCacheAgentMap.put(agent.getCacheName(), agent);
    }

    private void checkCacheAgentName(String agentName) {
        if (kvCacheAgentMap.containsKey(agentName) || flatHashCacheAgentMap.containsKey(agentName)) {
            throw new IllegalArgumentException("CacheAgent name already exists: " + agentName);
        }
    }
}
