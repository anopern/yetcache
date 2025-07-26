package com.yetcache.agent.regitry;

import com.yetcache.agent.core.structure.CacheAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public class RegistryHub {
    private final DynamicHashCacheAgentRegistry dhRegistry = new DynamicHashCacheAgentRegistry();

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
