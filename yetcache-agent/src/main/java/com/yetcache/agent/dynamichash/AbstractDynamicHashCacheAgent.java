package com.yetcache.agent.dynamichash;

import com.yetcache.agent.AbstractCacheAgent;
import com.yetcache.agent.MetricsInterceptor;
import com.yetcache.agent.interceptor.CacheInvocationInterceptor;
import com.yetcache.agent.result.DynamicHashCacheAgentResult;
import com.yetcache.core.cache.dynamichash.DefaultMultiTierDynamicHashCache;
import com.yetcache.core.cache.dynamichash.DynamicHashStorageResult;
import com.yetcache.core.cache.dynamichash.MultiTierDynamicHashCache;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.result.CacheOutcome;
import com.yetcache.core.result.StorageCacheAccessResult;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Slf4j
public class AbstractDynamicHashCacheAgent<K, F, V> extends AbstractCacheAgent<DynamicHashCacheAgentResult<K, F, V>>
        implements DynamicHashCacheAgent<K, F, V> {
    protected final MultiTierDynamicHashCache<K, F, V> cache;
    protected final DynamicHashCacheConfig config;
    protected final DynamicHashCacheLoader<K, F, V> cacheLoader;

    /**
     * 拦截器链，可按需在子类或外部继续追加
     */
    protected final List<CacheInvocationInterceptor> interceptors = new CopyOnWriteArrayList<>();

    public AbstractDynamicHashCacheAgent(String componentNane,
                                         DynamicHashCacheConfig config,
                                         RedissonClient redissonClient,
                                         KeyConverter<K> keyConverter,
                                         FieldConverter<F> fieldConverter,
                                         DynamicHashCacheLoader<K, F, V> cacheLoader,
                                         MeterRegistry registry) {
        super(componentNane);
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.cache = new DefaultMultiTierDynamicHashCache<>(componentNane, config, redissonClient, keyConverter,
                fieldConverter);

        this.interceptors.add(new MetricsInterceptor(registry));
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> get(K bizKey, F bizField) {
        StorageCacheAccessResult<CacheValueHolder<V>> result = cache.get(bizKey, bizField);
        if (result.outcome() == CacheOutcome.HIT) {
            return DynamicHashCacheAgentResult.success(agentName(), result.value(), result.tier() == HitTier.LOCAL);
        }
        return null;
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> list(K bizKey) {
        return null;
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> refreshAll(K bizKey) {
        return null;
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> invalidate(K bizKey, F bizField) {
        return null;
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> invalidateAll(K bizKey) {
        return null;
    }

    @Override
    protected DynamicHashCacheAgentResult<K, F, V> defaultFail(String method, Throwable t) {
        return null;
    }
}
