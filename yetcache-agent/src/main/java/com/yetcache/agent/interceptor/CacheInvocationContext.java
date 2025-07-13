package com.yetcache.agent.interceptor;

import com.yetcache.core.context.CacheAccessContext;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
@Data
public class CacheInvocationContext implements AutoCloseable {
    private final String cacheName;
    private final String methodName;
    private final CacheAccessContext.Context accessContext;

    // 用户可以挂载任意信息，例如计时、trace id、布隆过滤器等
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public CacheInvocationContext(String cacheName, String methodName, CacheAccessContext.Context accessContext) {
        this.cacheName = cacheName;
        this.methodName = methodName;
        this.accessContext = accessContext;
    }

    public static CacheInvocationContext start(String cache, String method) {
        return new CacheInvocationContext(cache, method, CacheAccessContext.begin(method));
    }

    @Override
    public void close() {
        CacheAccessContext.clear();
    }

    public <T> void setAttribute(String key, T value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }
}
