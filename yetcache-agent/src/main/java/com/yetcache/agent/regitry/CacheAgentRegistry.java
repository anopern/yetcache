package com.yetcache.agent.regitry;

import com.yetcache.agent.BaseKVCacheAgent;
import com.yetcache.agent.flathash.AbstractFlatHashCacheAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
@Component
public final class CacheAgentRegistry {
    private final Map<String, BaseKVCacheAgent<?, ?>> kvCacheAgentMap = new ConcurrentHashMap<>();
    private final Map<String, AbstractFlatHashCacheAgent<?, ?>> flatHashCacheAgentMap = new ConcurrentHashMap<>();

    @Autowired
    public CacheAgentRegistry(List<AbstractFlatHashCacheAgent<?, ?>> configCacheAgents) {
        for (AbstractFlatHashCacheAgent<?, ?> agent : configCacheAgents) {
            register(agent);
        }
    }

    public void register(BaseKVCacheAgent<?, ?> agent) {
        checkCacheAgentName(agent.getCacheName());
        kvCacheAgentMap.put(agent.getCacheName(), agent);
    }

    public void register(AbstractFlatHashCacheAgent<?, ?> agent) {
        checkCacheAgentName(agent.getCacheAgentName());
        flatHashCacheAgentMap.put(agent.getCacheAgentName(), agent);
    }

    public Map<String, AbstractFlatHashCacheAgent<?, ?>> getFlatHashCacheAgentMap() {
        return flatHashCacheAgentMap;
    }

    private void checkCacheAgentName(String agentName) {
        if (kvCacheAgentMap.containsKey(agentName)) {
            throw new IllegalArgumentException("CacheAgent name already exists: " + agentName);
        }

        if (flatHashCacheAgentMap.containsKey(agentName)) {
            throw new IllegalArgumentException("CacheAgent name already exists: " + agentName);
        }
    }
}
