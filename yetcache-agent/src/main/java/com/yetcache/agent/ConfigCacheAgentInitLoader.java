//package com.yetcache.agent;
//
//import com.yetcache.agent.regitry.CacheAgentRegistry;
//import com.yetcache.core.cache.flathash.FlatHashAccessResult;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.Map;
//
///**
// * 配置类型的缓存Agent初始化加载器
// *
// * @author walter.yan
// * @since 2025/7/12
// */
//@Component
//@Slf4j
//public class ConfigCacheAgentInitLoader {
//    private static final int MAX_RETRY = 3;
//    private static final long RETRY_DELAY_MS = 1000L;
//
//    private final CacheAgentRegistry cacheAgentRegistry;
//
//    @Autowired
//    public ConfigCacheAgentInitLoader(CacheAgentRegistry cacheAgentRegistry) {
//        this.cacheAgentRegistry = cacheAgentRegistry;
//    }
//
//    @PostConstruct
//    public void initLoad() {
//        long start = System.currentTimeMillis();
//
//        int total = 0;
//        int success = 0;
//        int fail = 0;
//
//        for (AbstractConfigCacheAgent<?, ?> agent : cacheAgentRegistry.getConfigCacheAgentAmp().values()) {
//            total++;
//            String name = agent.getName();
//            boolean loaded = false;
//
//            for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
//                FlatHashAccessResult<? extends Map<?, ?>> result = agent.refreshAllWithResult();
//                if (result.isSuccess()) {
//                    log.info("[initLoad] CacheAgent {} loaded successfully on attempt {}", name, attempt);
//                    loaded = true;
//                    success++;
//                    break;
//                } else {
//                    String errMsg = result.getException() != null
//                            ? result.getException().getMessage()
//                            : "unknown error";
//                    log.warn("[initLoad] ConfigCacheAgent {} load failed on attempt {}/{}: {}",
//                            name, attempt, MAX_RETRY, errMsg);
//                    try {
//                        Thread.sleep(RETRY_DELAY_MS);
//                    } catch (InterruptedException ie) {
//                        Thread.currentThread().interrupt();
//                        log.error("[initLoad] Retry for ConfigCacheAgent {} interrupted", name, ie);
//                        break;
//                    }
//                }
//            }
//
//            if (!loaded) {
//                fail++;
//                log.error("[initLoad] ConfigCacheAgent {} failed to load after {} attempts", name, MAX_RETRY);
//            }
//        }
//
//        long cost = System.currentTimeMillis() - start;
//        log.info("[initLoad] ConfigCacheAgent init load completed: total={}, success={}, failed={}, cost={}ms",
//                total, success, fail, cost);
//
//        if (fail > 0) {
//            throw new IllegalStateException("[initLoad] ConfigCacheAgent init load failed, system startup aborted.");
//        }
//    }
//}
