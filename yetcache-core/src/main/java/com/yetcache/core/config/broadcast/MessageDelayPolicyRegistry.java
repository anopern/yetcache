package com.yetcache.core.config.broadcast;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author walter.yan
 * @since 2025/8/16
 */
public class MessageDelayPolicyRegistry {
    private final ConcurrentMap<String, MessageDelayPolicy> map = new ConcurrentHashMap<>();
    private final MessageDelayPolicy defaultPolicy;

    public MessageDelayPolicyRegistry(MessageDelayPolicy defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
    }

    public void register(String agentName, MessageDelayPolicy policy) {
        map.put(agentName, policy);
    }

    public MessageDelayPolicy get(String agentName) {
        return map.getOrDefault(agentName, defaultPolicy);
    }
}
