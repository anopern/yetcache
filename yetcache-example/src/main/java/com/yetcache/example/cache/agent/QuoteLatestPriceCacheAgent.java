package com.yetcache.example.cache.agent;

import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.structure.dynamichash.AbstractDynamicHashCacheAgent;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheLoader;
import com.yetcache.agent.interceptor.InvocationInterceptor;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.example.cache.agent.key.QuoteLatestPriceCacheKey;
import com.yetcache.example.entity.QuoteSimpleQuoteRespVO;
import org.redisson.api.RedissonClient;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/7/23
 */
public class QuoteLatestPriceCacheAgent extends
        AbstractDynamicHashCacheAgent<QuoteLatestPriceCacheKey, String, QuoteSimpleQuoteRespVO> {
    public QuoteLatestPriceCacheAgent(String componentNane,
                                      DynamicHashCacheConfig config,
                                      RedissonClient redissonClient,
                                      KeyConverter<QuoteLatestPriceCacheKey> keyConverter,
                                      FieldConverter<String> fieldConverter,
                                      DynamicHashCacheLoader<QuoteLatestPriceCacheKey, String, QuoteSimpleQuoteRespVO> cacheLoader,
                                      List<InvocationInterceptor> interceptors,
                                      CacheBroadcastPublisher broadcastPublisher) {
        super(componentNane, config, redissonClient, keyConverter, fieldConverter, cacheLoader, interceptors,
                broadcastPublisher);
    }
}
