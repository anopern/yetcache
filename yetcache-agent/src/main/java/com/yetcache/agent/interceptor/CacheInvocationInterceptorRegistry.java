package com.yetcache.agent.interceptor;

import com.yetcache.agent.agent.StructureBehaviorKey;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
public class CacheInvocationInterceptorRegistry {
    private final List<CacheInterceptor> all = new ArrayList<>();

    public void register(CacheInterceptor interceptor) {
        all.add(interceptor);
    }

    public List<CacheInterceptor> getChainFor(InterceptorSupportCriteria criteria) {
        return all.stream()
                .filter(it -> it.supports(criteria))
                .sorted(Comparator.comparingInt(CacheInterceptor::getOrder))
                .collect(Collectors.toList());
    }

}
