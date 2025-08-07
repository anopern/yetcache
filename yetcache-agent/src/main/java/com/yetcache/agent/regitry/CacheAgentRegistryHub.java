package com.yetcache.agent.regitry;

import com.yetcache.agent.core.structure.CacheAgent;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public class CacheAgentRegistryHub {
    private final DynamicHashCacheAgentRegistry dhRegistry = new DynamicHashCacheAgentRegistry();

    public void register(CacheAgent agent) {
        if (agent instanceof DynamicHashCacheAgent) {
            dhRegistry.register((DynamicHashCacheAgent) agent);
        }
    }

    public Optional<CacheAgent> find(String name) {
        if (dhRegistry.get(name) != null) return Optional.of(dhRegistry.get(name));
        return Optional.empty();
    }

    public List<CacheAgent> allAgents() {
        List<CacheAgent> all = new ArrayList<>();
        all.addAll(dhRegistry.listAgents());
        return all;
    }
}
