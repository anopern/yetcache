package com.yetcache.example.cache.agent;

import com.yetcache.agent.core.structure.dynamichash.AbstractDynamicHashCacheAgent;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheLoader;
import com.yetcache.agent.interceptor.CacheInvocationInterceptor;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.example.entity.StockHoldInfo;
import io.micrometer.core.instrument.MeterRegistry;
import org.redisson.api.RedissonClient;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public class StockHoldInfoCacheAgent extends AbstractDynamicHashCacheAgent<String, Long, StockHoldInfo> {
    public StockHoldInfoCacheAgent(String componentNane,
                                   DynamicHashCacheConfig config,
                                   RedissonClient redissonClient,
                                   KeyConverter<String> keyConverter,
                                   FieldConverter<Long> fieldConverter,
                                   DynamicHashCacheLoader<String, Long, StockHoldInfo> cacheLoader,
                                   List<CacheInvocationInterceptor> interceptors) {
        super(componentNane, config, redissonClient, keyConverter, fieldConverter, cacheLoader, interceptors);
    }
}
