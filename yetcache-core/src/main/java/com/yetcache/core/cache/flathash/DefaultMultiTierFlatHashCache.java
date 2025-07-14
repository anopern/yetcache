package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.config.flathash.FlatHashCacheConfig;
import com.yetcache.core.result.BasicFailResult;
import com.yetcache.core.result.CacheAccessResult;
import com.yetcache.core.result.CacheAccessTrace;
import com.yetcache.core.result.FlatHashStorageResult;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Default implementation of a Flat‑Hash multi‑tier cache.
 * <p>
 * <strong>约束</strong>
 * <ul>
 *   <li><b>Memory Only</b>：运行期<strong>绝不</strong>回源；只在定时或显式 <code>putAll/refreshAll</code> 时装载。</li>
 *   <li><b>One as All</b>：仅支持整张表级别操作（get/listAll/putAll）。</li>
 *   <li><b>Fail‑Fast Init</b>：构造函数不做任何隐式加载；由上层 Agent 负责初始化。</li>
 * </ul>
 * <p>
 * 返回值全部使用 {@link CacheAccessResult} 衔接横切增强（metrics、trace、exception mask …）。
 *
 * @author walter.yan
 * @since 2025/07/10
 */
@Slf4j
public class DefaultMultiTierFlatHashCache<F, V> implements MultiTierFlatHashCache<F, V> {

    private final String cacheName;
    private final FlatHashCacheConfig config;
    private final CaffeineFlatHashCache<V> localCache;   // JVM / Caffeine
    private final KeyConverter<Void> keyConverter;
    private final FieldConverter<F> fieldConverter;

    public DefaultMultiTierFlatHashCache(String cacheName,
                                         FlatHashCacheConfig config,
                                         KeyConverter<Void> keyConverter,
                                         FieldConverter<F> fieldConverter) {
        this.cacheName = Objects.requireNonNull(cacheName, "cacheName");
        this.config = Objects.requireNonNull(config, "config");
        this.keyConverter = Objects.requireNonNull(keyConverter, "keyConverter");
        this.fieldConverter = Objects.requireNonNull(fieldConverter, "fieldConverter");
        this.localCache = new CaffeineFlatHashCache<>(config.getLocal());
    }

    /* ====================================================================== */
    /* Template wrapper – ALWAYS attach trace + unify exception → FailResult  */
    /* ====================================================================== */
    @SuppressWarnings("unchecked")
    private <R extends CacheAccessResult<?>> R invoke(String method, Supplier<R> business) {
        CacheAccessTrace trace = CacheAccessTrace.start();
        try {
            R res = business.get();
            return (R) res.withTrace(trace.success());
        } catch (Exception e) {
            log.error("cache.{}/{} threw exception: {}", cacheName, method, e.toString());
            return (R) BasicFailResult.of(method, e, trace.fail(e));
        }
    }


    @Override
    public FlatHashStorageResult<F, V> listAll() {
        return invoke("listAll", this::doListAll);
    }

    private FlatHashStorageResult<F, V> doListAll() {
        String key = keyConverter.convert(null);
        Map<String, CacheValueHolder<V>> raw = localCache.listAll(key);
        if (raw == null || raw.isEmpty()) {
            return FlatHashStorageResult.miss(HitTier.LOCAL);
        }

        Map<F, CacheValueHolder<V>> typed = raw.entrySet().stream()
                .collect(Collectors.toMap(e -> safeReverse(e.getKey()), Map.Entry::getValue));

        return FlatHashStorageResult.hit(Collections.unmodifiableMap(typed), HitTier.LOCAL);
    }

    private F safeReverse(String fieldKey) {
        try {
            return fieldConverter.revert(fieldKey);
        } catch (Exception ex) {
            log.warn("[{}] reverse field failed: {}", cacheName, fieldKey, ex);
            return null; // caller will filter null keys
        }
    }

    /* =============================  WRITE  ================================ */

    @Override
    public FlatHashStorageResult<F, V> putAll(Map<F, V> dataMap) {
        return invoke("putAll", () -> doPutAll(dataMap));
    }

    private FlatHashStorageResult<F, V> doPutAll(Map<F, V> dataMap) {
        if (dataMap == null || dataMap.isEmpty()) {
            return FlatHashStorageResult.block("empty putAll");
        }

        String key = keyConverter.convert(null);
        Map<String, CacheValueHolder<V>> cacheMap = new HashMap<>(dataMap.size());
        long ttlSecs = config.getLocal().getTtlSecs();

        dataMap.forEach((field, value) -> {
            if (field == null || value == null) {
                log.debug("skip null in putAll: field={}, value={}", field, value);
                return;
            }
            String fieldKey = fieldConverter.convert(field);
            cacheMap.put(fieldKey, CacheValueHolder.wrap(value, ttlSecs));
        });

        localCache.putAll(key, cacheMap);
        return FlatHashStorageResult.success();
    }
}
