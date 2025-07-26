package com.yetcache.agent.regitry;

import com.yetcache.agent.core.structure.CacheAgent;

import java.util.Collection;
import java.util.Set;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
// 每个结构类型的注册中心通用接口
public interface CacheAgentRegistry<A extends CacheAgent> {
    A get(String name);
    void register(A agent);
    void unregister(String name);
    Set<String> listAgentNames();
    Collection<A> listAgents();
}
