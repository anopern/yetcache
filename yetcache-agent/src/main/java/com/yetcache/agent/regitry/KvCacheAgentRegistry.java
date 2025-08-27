package com.yetcache.agent.regitry;

import com.yetcache.agent.core.structure.kv.KvCacheAgent;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class KvCacheAgentRegistry {

    // 存储 agent 实例，按 componentName 唯一标识
    private final Map<String, KvCacheAgent> agentMap = new ConcurrentHashMap<>();

    /**
     * 注册一个 Agent，如果重复 componentName 将覆盖
     */
    public void register(KvCacheAgent agent) {
        Objects.requireNonNull(agent, "Agent must not be null");
        String name = agent.cacheAgentName();
        agentMap.put(name, agent);
    }

    /**
     * 根据名称获取 agent，找不到返回 null
     */
    public KvCacheAgent get(String componentName) {
        return agentMap.get(componentName);
    }

    /**
     * 删除一个 Agent
     */
    public void unregister(String componentName) {
        agentMap.remove(componentName);
    }

    /**
     * 获取所有已注册的名称
     */
    public Set<String> listAgentNames() {
        return agentMap.keySet();
    }

    /**
     * 获取所有 Agent 实例
     */
    public Collection<KvCacheAgent> listAgents() {
        return agentMap.values();
    }

    /**
     * 判断是否包含某个组件
     */
    public boolean contains(String componentName) {
        return agentMap.containsKey(componentName);
    }
}
