package com.yetcache.agent.regitry;

import com.yetcache.agent.core.structure.CacheAgent;
import com.yetcache.agent.core.structure.kv.KvCacheAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public class CacheAgentRegistryHub {
    private final KvCacheAgentRegistry kvRegistry = new KvCacheAgentRegistry();

    public void register(CacheAgent agent) {
        if (agent instanceof KvCacheAgent) {
            kvRegistry.register((KvCacheAgent) agent);
        }
    }

    public Optional<CacheAgent> find(String name) {
        if (kvRegistry.get(name) != null) {
            return Optional.of(kvRegistry.get(name));
        }
        return Optional.empty();
    }

    public List<CacheAgent> allAgents() {
        return new ArrayList<>(kvRegistry.listAgents());
    }
}
