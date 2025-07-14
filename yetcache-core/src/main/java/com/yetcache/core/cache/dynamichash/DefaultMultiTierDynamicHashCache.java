package com.yetcache.core.cache.dynamichash;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.result.StorageCacheAccessResult;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.support.util.TtlRandomizer;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public class DefaultMultiTierDynamicHashCache<K, F, V> implements MultiTierDynamicHashCache<K, F, V> {
    private final String cacheName;
    private final DynamicHashCacheConfig config;
    private CaffeineDynamicHashCache<V> localCache;
    private RedisDynamicHashCache<V> remoteCache;
    private final KeyConverter<K> keyConverter;
    private final FieldConverter<F> fieldConverter;

    public DefaultMultiTierDynamicHashCache(String cacheName,
                                            DynamicHashCacheConfig config,
                                            RedissonClient redissonClient,
                                            KeyConverter<K> keyConverter,
                                            FieldConverter<F> fieldConverter) {
        this.cacheName = Objects.requireNonNull(cacheName, "cacheName");
        this.config = Objects.requireNonNull(config, "config");
        this.keyConverter = Objects.requireNonNull(keyConverter, "keyConverter");
        this.fieldConverter = Objects.requireNonNull(fieldConverter, "fieldConverter");

        if (config.getSpec().getCacheTier().useLocal()) {
            this.localCache = new CaffeineDynamicHashCache<>(config.getLocal());
        }
        if (config.getSpec().getCacheTier().useRemote()) {
            this.remoteCache = new RedisDynamicHashCache<>(config.getRemote(), redissonClient);
        }
    }

    @Override
    public StorageCacheAccessResult<CacheValueHolder<V>> get(K bizKey, F bizField) {
        String key = keyConverter.convert(bizKey);
        String field = fieldConverter.convert(bizField);

        // 1. 尝试从本地缓存读取
        if (localCache != null) {
            CacheValueHolder<V> holder = localCache.getIfPresent(key, field);
            if (holder != null && holder.isNotLogicExpired()) {
                return StorageCacheAccessResult.hit(holder, HitTier.LOCAL);
            }
        }

        // 2. 尝试从远程缓存读取
        if (remoteCache != null) {
            CacheValueHolder<V> holder = remoteCache.getIfPresent(key, field);
            if (holder != null && holder.isNotLogicExpired()) {
                // 回写到本地缓存
                if (localCache != null) {
                    localCache.put(key, field, holder);
                }
                return StorageCacheAccessResult.hit(holder, HitTier.REMOTE);
            }
        }

        // 3. 所有缓存都 miss
        return StorageCacheAccessResult.miss();
    }

    @Override
    public StorageCacheAccessResult<Map<F, CacheValueHolder<V>>> list(K bizKey) {
        String key = keyConverter.convert(bizKey);

        // 1. 本地缓存尝试
        if (localCache != null) {
            Map<String, CacheValueHolder<V>> localMap = localCache.listAll(key);
            if (CollUtil.isNotEmpty(localMap)) {
                return StorageCacheAccessResult.hitHolderMap(rawHolderMap2typeHolderMap(localMap), HitTier.LOCAL);
            }
        }

        // 2. 远程缓存尝试
        if (remoteCache != null) {
            Map<String, CacheValueHolder<V>> remoteMap = remoteCache.listAll(key);
            if (remoteMap != null && !remoteMap.isEmpty()) {
                // 回写到本地
                if (localCache != null) {
                    localCache.putAll(key, remoteMap);
                }
                return StorageCacheAccessResult.hitHolderMap(rawHolderMap2typeHolderMap(remoteMap), HitTier.REMOTE);
            }
        }

        // 3. miss
        return StorageCacheAccessResult.miss();
    }

    @Override
    public StorageCacheAccessResult<Void> put(K bizKey, F bizField, V value) {
        String key = keyConverter.convert(bizKey);
        String field = fieldConverter.convert(bizField);

        // 写入本地缓存（如启用）
        if (localCache != null) {
            CacheValueHolder<V> localHolder = CacheValueHolder.wrap(value, config.getLocal().getTtlSecs());
            localCache.put(key, field, localHolder);
        }

        // 写入远程缓存（如启用）
        if (remoteCache != null) {
            CacheValueHolder<V> remoteHolder = CacheValueHolder.wrap(value, config.getRemote().getTtlSecs());
            remoteCache.put(key, field, remoteHolder);
        }

        return StorageCacheAccessResult.success();
    }

    @Override
    public StorageCacheAccessResult<Void> putAll(K bizKey, Map<F, V> valueMap) {
        String key = keyConverter.convert(bizKey);
        final long localTtlSecs = TtlRandomizer.randomizeSecs(config.getLocal().getTtlSecs(),
                config.getLocal().getTtlRandomPct());
        final long remoteTtlSecs = TtlRandomizer.randomizeSecs(config.getRemote().getTtlSecs(),
                config.getRemote().getTtlRandomPct());

        // 写入远程缓存
        if (remoteCache != null) {
            Map<String, CacheValueHolder<V>> remoteHolderMap = typeMap2rawHolderMap(valueMap, remoteTtlSecs);
            remoteCache.putAll(key, remoteHolderMap);
        }
        // 写入本地缓存
        if (localCache != null) {
            Map<String, CacheValueHolder<V>> localHolderMap = typeMap2rawHolderMap(valueMap, localTtlSecs);
            localCache.putAll(key, localHolderMap);
        }

        return StorageCacheAccessResult.success();
    }


    @Override
    public StorageCacheAccessResult<Void> invalidate(K bizKey, F bizField) {
        String key = keyConverter.convert(bizKey);
        String field = fieldConverter.convert(bizField);

        // 清除本地缓存
        if (localCache != null) {
            localCache.invalidate(key, field);
        }

        // 清除远程缓存
        if (remoteCache != null) {
            remoteCache.invalidate(key, field);
        }

        return StorageCacheAccessResult.success();
    }

    @Override
    public StorageCacheAccessResult<Void> invalidateAll(K bizKey) {
        String key = keyConverter.convert(bizKey);

        // 清除本地缓存
        if (localCache != null) {
            localCache.invalidateAll(key);
        }

        // 清除远程缓存
        if (remoteCache != null) {
            remoteCache.invalidateAll(key);
        }

        return StorageCacheAccessResult.success();
    }

    private Map<F, CacheValueHolder<V>> rawHolderMap2typeHolderMap(Map<String, CacheValueHolder<V>> rwaMap) {
        // 构建返回值 Map<F, CacheValueHolder<V>>（回转字段）
        return rwaMap.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> fieldConverter.revert(e.getKey()),
                        Map.Entry::getValue
                ));
    }

    private Map<String, CacheValueHolder<V>> typeMap2rawHolderMap(Map<F, V> valueMap, long ttlSecs) {
        return valueMap.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> fieldConverter.convert(e.getKey()),
                        e -> CacheValueHolder.wrap(e.getValue(), ttlSecs)
                ));
    }

}
