package com.yetcache.agent;

import com.yetcache.agent.preload.MandatoryStartupInitializable;
import com.yetcache.core.result.CacheAccessResult;
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
@Component
@RequiredArgsConstructor
@Slf4j
public class MandatoryStartupInitializer implements ApplicationRunner {
    private final List<MandatoryStartupInitializable> components;
    private final RedissonClient redisson;
    private final MeterRegistry meter;
    @Value("${yetcache.init.maxParallel:1}")
    private int maxParallel;
    @Value("${yetcache.init.lockLeaseSec:120}")
    private long lockLeaseSec;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (components.isEmpty()) {
            log.info("[StartupInit] no components found, skip.");
            return;
        }

        components.sort(Comparator.comparingInt(MandatoryStartupInitializable::getPriority));
        ExecutorService pool = Executors.newFixedThreadPool(maxParallel);
        List<Future<Boolean>> results = new CopyOnWriteArrayList<>();

        for (MandatoryStartupInitializable c : components) {
            results.add(pool.submit(() -> initializeOne(c)));
        }

        pool.shutdown();
        for (Future<Boolean> f : results) {
            if (!f.get()) {
                log.error("[StartupInit] fail-fast: application will exit.");
                System.exit(1);
            }
        }
        log.info("[StartupInit] ALL components loaded OK ({} total).", components.size());
    }

    private boolean initializeOne(MandatoryStartupInitializable c) {
        String name = c.getComponentName();
        String lockKey = "startup:init:" + name;
        Timer.Sample sample = Timer.start(meter);
        RLock lock = redisson.getLock(lockKey);

        try {
            if (!lock.tryLock(0, lockLeaseSec, TimeUnit.SECONDS)) {
                log.warn("[StartupInit][{}] skipped (locked by peer)", name);
                sample.stop(meter.timer("startup.init.latency", "component", name, "status", "lock_skip"));
                return true;
            }

            CacheAccessResult<?> res = c.initialize();
            if (res.isSuccess()) {
                meter.counter("startup.init.count", "component", name, "outcome", "SUCCESS").increment();
                sample.stop(meter.timer("startup.init.latency", "component", name, "status", "success"));
                return true;
            } else {
                meter.counter("startup.init.count", "component", name, "outcome", res.outcome().name()).increment();
                sample.stop(meter.timer("startup.init.latency", "component", name, "status", "fail"));
                log.error("[StartupInit][{}] FAILED: {}", name, res.outcome());
                return false;
            }
        } catch (Exception ex) {
            meter.counter("startup.init.count", "component", name, "outcome", "EXCEPTION").increment();
            sample.stop(meter.timer("startup.init.latency", "component", name, "status", "exception"));
            log.error("[StartupInit][{}] EXCEPTION", name, ex);
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
