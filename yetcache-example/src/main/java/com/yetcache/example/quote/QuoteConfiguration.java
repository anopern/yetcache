package com.yetcache.example.quote;

import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.agent.CacheAgentPortRegistry;
import com.yetcache.agent.agent.kv.BaseKvCacheAgent;
import com.yetcache.agent.interceptor.CacheInvocationChainRegistry;
import com.yetcache.core.cache.YetCacheConfigResolver;
import com.yetcache.core.codec.JsonValueCodec;
import com.yetcache.core.codec.TypeDescriptor;
import com.yetcache.core.codec.TypeRef;
import com.yetcache.core.codec.TypeRefRegistry;
import com.yetcache.core.config.kv.KvCacheConfig;
import com.yetcache.example.config.CacheAgentNames;
import com.yetcache.example.quote.loader.QuoteLatestPriceCacheLoader;
import com.yetcache.example.quote.source.QuotesDataService;
import com.yetcache.example.quote.support.QuoteLatestPriceKeyConvertor;
import com.yetcache.example.quote.vo.QuoteLatestPriceVO;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
@Configuration
public class QuoteConfiguration {

    @Qualifier("quoteLatestPriceCacheAgent")
    @Bean
    public BaseKvCacheAgent quoteLatestPriceCacheAgent(
            YetCacheConfigResolver configResolver,
            QuoteLatestPriceCacheLoader quoteLatestPriceCacheLoader,
            RedissonClient redissonClient,
            QuoteLatestPriceKeyConvertor quoteLatestPriceKeyConvertor,
            CacheBroadcastPublisher broadcastPublisher,
            CacheInvocationChainRegistry chainRegistry,
            TypeRefRegistry typeRefRegistry,
            JsonValueCodec jsonValueCodec,
            CacheAgentPortRegistry agentPortRegistry) {
        final String cacheAgentName = CacheAgentNames.QUOTE_LATEST_PRICE_CACHE_AGENT;
        TypeDescriptor typeDescriptor = TypeDescriptor.of(new TypeRef<QuoteLatestPriceVO>() {
        });
        return new BaseKvCacheAgent(
                cacheAgentName,
                configResolver.resolveKv(cacheAgentName),
                redissonClient,
                quoteLatestPriceKeyConvertor,
                quoteLatestPriceCacheLoader,
                broadcastPublisher,
                chainRegistry,
                typeRefRegistry,
                typeDescriptor,
                jsonValueCodec,
                agentPortRegistry
        );
    }

    @Qualifier("quoteLatestPriceKeyConvertor")
    @Bean
    public QuoteLatestPriceKeyConvertor quoteLatestPriceKeyConvertor(YetCacheConfigResolver configResolver) {
        KvCacheConfig config = configResolver.resolveKv(CacheAgentNames.QUOTE_LATEST_PRICE_CACHE_AGENT);
        String prefix = config.getSpec().getKeyPrefix();
        return new QuoteLatestPriceKeyConvertor(prefix, false);
    }

    @Qualifier("quoteLatestPriceCacheLoader")
    @Bean
    public QuoteLatestPriceCacheLoader quoteLatestPriceCacheLoader(QuotesDataService quotesDataService) {
        return new QuoteLatestPriceCacheLoader(quotesDataService);
    }
}
