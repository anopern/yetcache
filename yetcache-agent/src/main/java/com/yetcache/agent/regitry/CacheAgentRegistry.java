package com.yetcache.agent.regitry;

import com.yetcache.agent.core.CacheStructureType;
import com.yetcache.agent.core.structure.AbstractCacheAgent;
import com.yetcache.agent.core.structure.dynamichash.AbstractDynamicHashCacheAgent;
import com.yetcache.agent.core.structure.flathash.AbstractFlatHashCacheAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册所有类型 CacheAgent 的中心组件，提供统一治理能力。
 * 支持 KV / FlatHash / DynamicHash 三种结构。
 *
 * @author walter
 * @since 2025/07/16
 */
@Component
public final class CacheAgentRegistry {

    //    private final Map<String, BaseKVCacheAgent<?, ?>> kvAgentMap = new ConcurrentHashMap<>();
    private final Map<String, AbstractFlatHashCacheAgent<?, ?>> flatHashAgentMap = new ConcurrentHashMap<>();
    private final Map<String, AbstractDynamicHashCacheAgent<?, ?, ?>> dynamicHashAgentMap = new ConcurrentHashMap<>();

    // 统一视角访问
    private final Map<String, AbstractCacheAgent> allAgentMap = new ConcurrentHashMap<>();

    @Autowired
    public CacheAgentRegistry(Optional<List<AbstractFlatHashCacheAgent<?, ?>>> flatHashAgents,
                              Optional<List<AbstractDynamicHashCacheAgent<?, ?, ?>>> dynamicHashAgents) {

//        kvAgents.ifPresent(list -> list.forEach(this::register));
        flatHashAgents.ifPresent(list -> list.forEach(this::register));
        dynamicHashAgents.ifPresent(list -> list.forEach(this::register));
    }

//    public void register(BaseKVCacheAgent<?, ?> agent) {
//        String name = agent.getComponentName();
//        checkDuplicate(name);
//        kvAgentMap.put(name, agent);
//        allAgentMap.put(name, agent);
//    }

    public void register(AbstractFlatHashCacheAgent<?, ?> agent) {
        String name = agent.getCacheName();
        checkDuplicate(name);
        flatHashAgentMap.put(name, agent);
        allAgentMap.put(name, agent);
    }

    public void register(AbstractDynamicHashCacheAgent<?, ?, ?> agent) {
        String name = agent.getCacheName();
        checkDuplicate(name);
        dynamicHashAgentMap.put(name, agent);
        allAgentMap.put(name, agent);
    }

    private void checkDuplicate(String name) {
        if (allAgentMap.containsKey(name)) {
            throw new IllegalArgumentException("CacheAgent name already exists: " + name);
        }
    }

    // =============== 结构特定访问 ===============
//    public Optional<BaseKVCacheAgent<?, ?>> getKVAgent(String name) {
//        return Optional.ofNullable(kvAgentMap.get(name));
//    }

    public Optional<AbstractFlatHashCacheAgent<?, ?>> getFlatHashAgent(String name) {
        return Optional.ofNullable(flatHashAgentMap.get(name));
    }

    public Optional<AbstractDynamicHashCacheAgent<?, ?, ?>> getDynamicHashAgent(String name) {
        return Optional.ofNullable(dynamicHashAgentMap.get(name));
    }

    // =============== 统一治理视角 ===============
    public Optional<AbstractCacheAgent> getAgent(String name) {
        return Optional.ofNullable(allAgentMap.get(name));
    }

    public AbstractCacheAgent<?> getAgent(CacheStructureType structureType, String name) {
        Map<String, AbstractCacheAgent<?>> agentMap = get(structureType);
        AbstractCacheAgent<?> agent = agentMap.get(name);
        if (agent == null) {
            throw new IllegalArgumentException("No agent found for structureType: " + structureType + ", name: " + name);
        }
        return agent;
    }

    @SuppressWarnings("unchecked")
    private Map<String, AbstractCacheAgent<?>> get(CacheStructureType structureType) {
        switch (structureType) {
            case CONFIG:
                return (Map<String, AbstractCacheAgent<?>>) (Map<?, ?>) flatHashAgentMap;
            case DYNAMIC_HASH:
                return (Map<String, AbstractCacheAgent<?>>) (Map<?, ?>) dynamicHashAgentMap;
            default:
                throw new IllegalArgumentException("Invalid structure type: " + structureType);
        }
    }

    public Set<String> getAllAgentNames() {
        return Collections.unmodifiableSet(allAgentMap.keySet());
    }

    public boolean contains(String name) {
        return allAgentMap.containsKey(name);
    }

    public int size() {
        return allAgentMap.size();
    }
}
