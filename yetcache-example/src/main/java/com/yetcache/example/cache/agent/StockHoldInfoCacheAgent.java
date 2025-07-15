package com.yetcache.example.cache.agent;

import com.yetcache.agent.dynamichash.AbstractDynamicHashCacheAgent;
import com.yetcache.agent.dynamichash.DynamicHashCacheLoader;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.example.entity.StockHoldInfo;
import io.micrometer.core.instrument.MeterRegistry;
import org.redisson.api.RedissonClient;

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
                                   MeterRegistry registry) {
        super(componentNane, config, redissonClient, keyConverter, fieldConverter, cacheLoader, registry);
    }
}
