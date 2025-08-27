package com.yetcache.agent.core.port;

import com.yetcache.agent.interceptor.BehaviorType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walter.yan
 * @since 2025/8/27
 */
public class CacheAgentPortRegistry {
    private final Map<String, CacheAgentPort> map = new ConcurrentHashMap<>();

    public void register(String cacheAgentName, BehaviorType behaviorType, CacheAgentPort port) {
        map.put(getKey(cacheAgentName, behaviorType), port);
    }

    public CacheAgentPort get(String cacheAgentName, BehaviorType behaviorType) {
        return map.get(getKey(cacheAgentName, behaviorType));
    }

    private String getKey(String cacheAgentName, BehaviorType behaviorType) {
        return String.format("%s:%s", cacheAgentName, behaviorType);
    }
}
