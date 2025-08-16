package com.yetcache.agent.core.capability;

import com.yetcache.core.result.CacheResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Slf4j
@Component
public class ForceIntervalRefreshScheduler {
    private final List<ForceIntervalRefreshable> refreshables;
    private final Map<String, Long> lastRefreshTimeMap = new ConcurrentHashMap<>();

    @Autowired
    public ForceIntervalRefreshScheduler(List<ForceIntervalRefreshable> refreshables) {
        this.refreshables = refreshables;
        long now = System.currentTimeMillis();
        for (ForceIntervalRefreshable agent : refreshables) {
            lastRefreshTimeMap.put(agent.getComponentName(), now);
        }
    }

    @Scheduled(fixedRate = 30_000)
    public void refresh() {
        long now = System.currentTimeMillis();
        for (ForceIntervalRefreshable agent : refreshables) {
            long intervalMs = agent.getRefreshIntervalSecs() * 1000L;
            String name = agent.getComponentName();
            long last = lastRefreshTimeMap.getOrDefault(name, 0L);
            if (now - last >= intervalMs) {
                try {
                    CacheResult result = agent.intervalRefresh();
                    if (result.isSuccess()) {
                        lastRefreshTimeMap.put(name, now);
                    } else {
                        log.error("{} refresh failed: {}", name, result);
                    }
                } catch (Exception e) {
                    log.error("{} refresh failed", name, e);
                }
            }
        }
    }
}
