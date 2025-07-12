package com.yetcache.agent.regitry;

import com.yetcache.agent.AbstractConfigCacheAgent;
import com.yetcache.agent.BaseKVCacheAgent;
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
    private final Map<String, AbstractConfigCacheAgent<?, ?>> configCacheAgentMap = new ConcurrentHashMap<>();

    @Autowired
    public CacheAgentRegistry(List<AbstractConfigCacheAgent<?, ?>> configCacheAgents) {
        for (AbstractConfigCacheAgent<?, ?> agent : configCacheAgents) {
            register(agent);
        }
    }

    public void register(BaseKVCacheAgent<?, ?> agent) {
        checkCacheAgentName(agent.getCacheName());
        kvCacheAgentMap.put(agent.getCacheName(), agent);
    }

    public void register(AbstractConfigCacheAgent<?, ?> agent) {
        checkCacheAgentName(agent.getName());
        configCacheAgentMap.put(agent.getName(), agent);
    }

    public Map<String, AbstractConfigCacheAgent<?, ?>> getConfigCacheAgentAmp() {
        return configCacheAgentMap;
    }

    private void checkCacheAgentName(String agentName) {
        if (kvCacheAgentMap.containsKey(agentName)) {
            throw new IllegalArgumentException("CacheAgent name already exists: " + agentName);
        }

        if (configCacheAgentMap.containsKey(agentName)) {
            throw new IllegalArgumentException("CacheAgent name already exists: " + agentName);
        }
    }
}
