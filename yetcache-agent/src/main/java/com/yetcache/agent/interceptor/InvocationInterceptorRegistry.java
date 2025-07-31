package com.yetcache.agent.interceptor;

import com.yetcache.core.result.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
public class InvocationInterceptorRegistry {
    private final List<CacheInterceptor<? extends InvocationContext, ?, ? extends Result<?>> all = new ArrayList<>();

    public void register(org.springframework.cache.interceptor.CacheInterceptor interceptor) {
        all.add(interceptor);
    }

    public List<org.springframework.cache.interceptor.CacheInterceptor> getChainFor(String behaviorType) {
        return all.stream()
                .filter(it -> it.supports(behaviorType))
                .sorted(Comparator.comparingInt(org.springframework.cache.interceptor.CacheInterceptor::getOrder))
                .collect(Collectors.toList());
    }

}
