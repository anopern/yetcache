package com.yetcache.agent.flathash;

import com.yetcache.agent.preload.PreloadableCacheAgent;
import com.yetcache.agent.result.FlatHashCacheAgentResult;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FlathashCacheInitialLoader implements ApplicationRunner {
    private final List<PreloadableCacheAgent> agents;
    private final RedissonClient redisson;
    private final MeterRegistry meter;
    @Value("${yetcache.init.maxParallel:1}")
    private int maxParallel;
    @Value("${yetcache.init.lockLeaseSec:120}")
    private long lockLeaseSec;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (agents.isEmpty()) {
            log.info("[InitLoad] no Flat-Hash agent found, skip.");
            return;
        }

        // 按优先级升序
        agents.sort(Comparator.comparingInt(PreloadableCacheAgent::getPriority));

        ExecutorService pool = Executors.newFixedThreadPool(maxParallel);
        List<Future<Boolean>> futures = new CopyOnWriteArrayList<>();

        for (PreloadableCacheAgent agent : agents) {
            futures.add(pool.submit(() -> preloadOneAgent(agent)));
        }

        pool.shutdown();
        for (Future<Boolean> f : futures) {
            if (!f.get()) {                     // 任意失败即退出
                log.error("[InitLoad] fail-fast: application will exit.");
                System.exit(1);
            }
        }
        log.info("[InitLoad] ALL Flat-Hash caches loaded OK ({} agents).", agents.size());
    }

    /* ------------------ 单个 Agent 预加载 ------------------ */
    private boolean preloadOneAgent(PreloadableCacheAgent agent) {
        String cache = agent.getCacheAgentName();
        String lockKey = "yetcache:init:" + cache;
        Timer.Sample sample = Timer.start(meter);

        RLock lock = redisson.getLock(lockKey);
        try {
            if (!lock.tryLock(0, lockLeaseSec, TimeUnit.SECONDS)) {
                log.warn("[InitLoad][{}] skipped – another node is loading", cache);
                sample.stop(meter.timer("yetcache.init.latency", "cache", cache, "status", "lock_skip"));
                return true;                    // 认为成功，另一节点负责加载
            }

            FlatHashCacheAgentResult<?, ?> res = (FlatHashCacheAgentResult<?, ?>) agent.preload();
            if (res.isSuccess()) {
                sample.stop(meter.timer("yetcache.init.latency", "cache", cache, "status", "success"));
                meter.counter("yetcache.init.count", "cache", cache, "outcome", "SUCCESS").increment();
                return true;
            } else {
                meter.counter("yetcache.init.count", "cache", cache, "outcome", res.outcome().name()).increment();
                sample.stop(meter.timer("yetcache.init.latency", "cache", cache, "status", "fail"));
                log.error("[InitLoad][{}] FAILED outcome={}", cache, res.outcome());
                return false;
            }

        } catch (Exception ex) {
            meter.counter("yetcache.init.count", "cache", cache, "outcome", "EXCEPTION").increment();
            sample.stop(meter.timer("yetcache.init.latency", "cache", cache, "status", "exception"));
            log.error("[InitLoad][{}] EXCEPTION", cache, ex);
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
