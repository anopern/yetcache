package com.yetcache.agent;

import com.yetcache.agent.regitry.CacheAgentRegistry;
import com.yetcache.core.cache.flathash.FlatHashAccessResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
@Component
@Slf4j
public class ConfigCacheAgentIntervalRefresher {
    private final CacheAgentRegistry cacheAgentRegistry;
    private final Map<String, Long> lastRefreshTimeMap = new ConcurrentHashMap<>();

    @Autowired
    public ConfigCacheAgentIntervalRefresher(CacheAgentRegistry cacheAgentRegistry) {
        this.cacheAgentRegistry = cacheAgentRegistry;

        long now = System.currentTimeMillis();
        for (AbstractConfigCacheAgent<?, ?> agent : cacheAgentRegistry.getConfigCacheAgentAmp().values()) {
            lastRefreshTimeMap.put(agent.getName(), now); // 初始化为 now，避免立即触发
        }
    }

    /**
     * 每30秒轮询一次，根据每个 Agent 的 refreshIntervalSecs 判断是否需要刷新
     */
    @Scheduled(initialDelay = 5 * 60 * 1000, fixedRate = 30_000)
    public void intervalRefresh() {
        long now = System.currentTimeMillis();

        for (AbstractConfigCacheAgent<?, ?> agent : cacheAgentRegistry.getConfigCacheAgentAmp().values()) {
            String name = agent.getName();
            long refreshIntervalMs = agent.getRefreshIntervalSecs() * 1000;

            long lastRefresh = lastRefreshTimeMap.getOrDefault(name, 0L);
            if (now - lastRefresh >= refreshIntervalMs) {
                try {
                    FlatHashAccessResult<? extends Map<?, ?>> result = agent.refreshAllWithResult();
                    if (result.isSuccess()) {
                        lastRefreshTimeMap.put(name, now);
                        log.debug("[intervalRefresh] Agent {} refreshed successfully", name);
                    } else {
                        log.warn("[intervalRefresh] Agent {} refresh failed: {}", name,
                                result.getException() != null ? result.getException().getMessage() : "unknown");
                    }
                } catch (Exception e) {
                    log.warn("[intervalRefresh] Agent {} refresh error: {}", name, e.getMessage(), e);
                }
            }
        }
    }
}
