package com.yetcache.core.cache.manager;

import com.yetcache.core.cache.kv.MultiTierKVCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Slf4j
@Component
public final class KVCacheRegistry {
    private final Map<String, MultiTierKVCache<?, ?>> registry = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <K, V> MultiTierKVCache<K, V> computeIfAbsent(String name, Supplier<MultiTierKVCache<K, V>> constructor) {
        return (MultiTierKVCache<K, V>) registry.computeIfAbsent(name, key -> {
            MultiTierKVCache<K, V> cache = constructor.get();
            log.info("Cache [{}] created and registered", name);
            return cache;
        });
    }

    public MultiTierKVCache<?, ?> get(String name) {
        return registry.get(name);
    }

    public boolean containsCache(String name) {
        return registry.containsKey(name);
    }

    public void register(String name, MultiTierKVCache<?, ?> cache) {
        registry.put(name, cache);
    }

    public void clearRegistry() {
        registry.clear();
        log.warn("KVCache registry cleared");
    }

}
