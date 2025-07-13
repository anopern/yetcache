package com.yetcache.agent.flathash;

import com.yetcache.agent.FlatHashCacheLoader;
import com.yetcache.agent.exception.CacheUnavailableException;
import com.yetcache.agent.result.FlatHashCacheAgentResult;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 业务便捷父类：所有业务 Flat-Hash Agent 统一继承它，
 * 自动拥有 list()/get()，内部仍走 listAllWithResult()。
 *
 * @author walter.yan
 * @since 2025/7/14
 */
public abstract class AbstractListableFlatHashAgent<F, V>
        extends AbstractFlatHashCacheAgent<F, V> {

    protected AbstractListableFlatHashAgent(String name,
                                            MultiTierFlatHashCacheConfig config,
                                            FlatHashCacheLoader<F, V> loader,
                                            MeterRegistry meter) {
        super(name, config, loader, meter);
    }

    /**
     * 业务常用：返回只读 Map<F,V>（无 Holder）
     */
    public Map<F, V> list() {
        FlatHashCacheAgentResult<F, V> r = listAllWithResult();
        if (!r.isSuccess()) {
            throw new CacheUnavailableException(getCacheAgentName(), r.outcome(), r.trace());
        }
        return unwrap(r.value());            // Map<F,CacheValueHolder<V>> ➜ Map<F,V>
    }

    /**
     * 业务常用：读取单字段
     */
    public V get(F field) {
        return list().get(field);
    }

    /* ---------- 工具 ---------- */
    private Map<F, V> unwrap(Map<F, CacheValueHolder<V>> raw) {
        return raw.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getValue()));
    }
}