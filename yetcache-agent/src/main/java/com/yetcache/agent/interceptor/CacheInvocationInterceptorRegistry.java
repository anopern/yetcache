package com.yetcache.agent.interceptor;

import com.yetcache.agent.core.StructureType;
import com.yetcache.core.result.Result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
public class CacheInvocationInterceptorRegistry<C extends CacheInvocationContext, T, R extends Result<T>> {
    private final List<CacheInterceptor<C, T, R>> all = new ArrayList<>();

    public void register(CacheInterceptor<C, T, R> interceptor) {
        all.add(interceptor);
    }

    public List<CacheInterceptor<C, T, R>> getChainFor(StructureBehaviorKey sbKey) {
        return all.stream()
                .filter(it -> it.supportStructure(sbKey.getStructureType())
                        && it.supportBehavior(sbKey.getBehaviorType()))
                .sorted(Comparator.comparingInt(CacheInterceptor::getOrder))
                .collect(Collectors.toList());
    }

    public List<CacheInterceptor<C, T, R>> getChainFor(StructureType structureType, BehaviorType behaviorType) {
        return all.stream()
                .filter(it -> it.supportStructure(structureType) && it.supportBehavior(behaviorType))
                .sorted(Comparator.comparingInt(CacheInterceptor::getOrder))
                .collect(Collectors.toList());
    }

}
