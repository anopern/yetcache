package com.yetcache.agent.core.structure.dynamichash.get;

import com.yetcache.agent.broadcast.command.ExecutableCommand;
import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.CacheAgentMethod;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheLoader;
import com.yetcache.agent.interceptor.BehaviorType;
import com.yetcache.agent.interceptor.InvocationChain;
import com.yetcache.agent.interceptor.InvocationChainRegistry;
import com.yetcache.agent.interceptor.InvocationInterceptor;
import com.yetcache.core.cache.dynamichash.MultiTierDynamicHashCache;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.result.BaseSingleResult;
import com.yetcache.core.result.CacheOutcome;
import com.yetcache.core.result.ResultFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Slf4j
public class DynamicHashCacheGetInterceptor<K, F, V> implements InvocationInterceptor<DynamicHashGetContext<K, F>,
        CacheValueHolder<V>, BaseSingleResult<V>> {
    protected final String componentName;
    protected final MultiTierDynamicHashCache<K, F, V> multiTierCache;
    protected final DynamicHashCacheConfig config;
    protected final DynamicHashCacheLoader<K, F, V> cacheLoader;
    private final CacheBroadcastPublisher broadcastPublisher;
    private final InvocationChainRegistry chainRegistry;


    public DynamicHashCacheGetInterceptor(String componentName,
                                          MultiTierDynamicHashCache<K, F, V> multiTierCache,
                                          DynamicHashCacheConfig config,
                                          DynamicHashCacheLoader<K, F, V> cacheLoader,
                                          CacheBroadcastPublisher broadcastPublisher,
                                          InvocationChainRegistry chainRegistry) {
        this.componentName = componentName;
        this.multiTierCache = multiTierCache;
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.broadcastPublisher = broadcastPublisher;
        this.chainRegistry = chainRegistry;
    }

    @Override
    public String id() {
        return "DynamicHashCacheGetInterceptor";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public boolean supportBehavior(BehaviorType type) {
        return BehaviorType.SINGLE_GET.equals(type);
    }

    @Override
    public boolean supportStructure(StructureType type) {
        return StructureType.DYNAMIC_HASH.equals(type);
    }

    @Override
    public BaseSingleResult<V> invoke(DynamicHashGetContext<K, F> ctx, InvocationChain<DynamicHashGetContext<K, F>,
            CacheValueHolder<V>, BaseSingleResult<V>> chain) throws Throwable {
        K bizKey = ctx.getBizKey();
        F bizField = ctx.getBizField();
        try {
            BaseSingleResult<V> result = multiTierCache.get(bizKey, bizField);
            if (result.outcome() == CacheOutcome.HIT) {
                CacheValueHolder<V> holder = result.value();
                if (holder.isNotLogicExpired()) {
                    return BaseSingleResult.hit(componentName, holder, result.hitTier());
                }
            }

            // 回源加载数据
            V loaded = cacheLoader.load(bizKey, bizField);
            if (loaded == null) {
                return ResultFactory.notFoundSingle(componentName);
            }

            // 封装为缓存值并写入缓存
            CacheValueHolder<V> valueHolder = CacheValueHolder.wrap(loaded, config.getRemote().getTtlSecs());
            multiTierCache.put(bizKey, bizField, valueHolder);

            try {
                Map<F, V> loadedMap = Collections.singletonMap(bizField, loaded);
                ExecutableCommand command = ExecutableCommand.dynamicHash(componentName, CacheAgentMethod.PUT_ALL,
                        bizKey, loadedMap);
                broadcastPublisher.publish(command);
            } catch (Exception e) {
                log.warn("broadcast failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            }

            return BaseSingleResult.hit(componentName, CacheValueHolder.wrap(loaded, 0),
                    HitTier.SOURCE);
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            return BaseSingleResult.fail(componentName, e);
        }
    }
}
