package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.cache.command.SingleHashCachePutCommand;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.result.BaseSingleResult;
import com.yetcache.core.result.ResultFactory;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import com.yetcache.core.support.util.TtlRandomizer;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Slf4j
public class DefaultMultiTierDynamicHashCache<K, F, V> implements MultiTierDynamicHashCache<K, F, V> {
    private final String componentName;
    private final DynamicHashCacheConfig config;
    private CaffeineDynamicHashCache<V> localCache;
    private RedisDynamicHashCache<V> remoteCache;
    private final KeyConverter<K> keyConverter;
    private final FieldConverter<F> fieldConverter;

    public DefaultMultiTierDynamicHashCache(String componentName,
                                            DynamicHashCacheConfig config,
                                            RedissonClient redissonClient,
                                            KeyConverter<K> keyConverter,
                                            FieldConverter<F> fieldConverter) {
        this.componentName = Objects.requireNonNull(componentName, "cacheName");
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
    public BaseSingleResult<V> get(K bizKey, F bizField) {
        String key = keyConverter.convert(bizKey);
        String field = fieldConverter.convert(bizField);

        // 1. 尝试从本地缓存读取
        if (localCache != null) {
            CacheValueHolder<V> valueHolder = localCache.getIfPresent(key, field);
            if (valueHolder != null && valueHolder.isNotLogicExpired()) {
                return BaseSingleResult.hit(componentName, valueHolder, HitTier.LOCAL);
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
                return BaseSingleResult.hit(componentName, holder, HitTier.REMOTE);
            }
        }

        // 3. 所有缓存都 miss
        return BaseSingleResult.miss(componentName);
    }

//    @Override
//    public BaseBatchResult<F, V> batchGet(K bizKey, List<F> bizFields) {
//        try {
//            String key = keyConverter.convert(bizKey);
//            // 批量转换为原始字段
//            List<String> rawFields = bizFields.stream().map(fieldConverter::convert).collect(Collectors.toList());
//            final Map<F, CacheValueHolder<V>> cacheValueHolderMap = new HashMap<>();
//            final Map<F, HitTier> hitTierMap = new HashMap<>();
//            // 本地缓存尝试
//            final Map<String, CacheValueHolder<V>> localResult = localCache != null
//                    ? localCache.batchGet(key, rawFields) : Map.of();
//            List<String> missingLocalFields = new ArrayList<>(rawFields);
//            for (Map.Entry<String, CacheValueHolder<V>> entry : localResult.entrySet()) {
//                F bizField = fieldConverter.revert(entry.getKey());
//                CacheValueHolder<V> valueHolder = entry.getValue();
//                if (null != valueHolder && valueHolder.isNotLogicExpired()) {
//                    cacheValueHolderMap.put(bizField, valueHolder);
//                    hitTierMap.put(bizField, HitTier.LOCAL);
//                    missingLocalFields.remove(entry.getKey());
//                }
//            }
//
//            if (CollUtil.isNotEmpty(missingLocalFields) && remoteCache != null) {
//                Map<String, CacheValueHolder<V>> remoteResult = remoteCache.batchGet(key, missingLocalFields);
//                // 写回本地缓存（仅非过期）
//                for (Map.Entry<String, CacheValueHolder<V>> entry : remoteResult.entrySet()) {
//                    String field = entry.getKey();
//                    CacheValueHolder<V> valueHolder = entry.getValue();
//                    if (valueHolder != null && valueHolder.isNotLogicExpired()) {
//                        localCache.put(key, field, valueHolder);
//                        cacheValueHolderMap.put(fieldConverter.revert(field), valueHolder);
//                    }
//                }
//            }
//
//            return BaseBatchResult.hit(componentName, cacheValueHolderMap, hitTierMap);
//        } catch (Exception e) {
//            log.warn("缓存回源加载失败，cacheName={}, bizKey={}, bizFields={}", componentName, bizKey, bizFields, e);
//            return BaseBatchResult.fail(componentName, e);
//        }
//    }

//    @Override
//    public BaseBatchResult<F, V> listAll(K bizKey) {
//        String key = keyConverter.convert(bizKey);
//
//        // 1. 本地缓存尝试
//        if (localCache != null) {
//            Map<String, CacheValueHolder<V>> localResult = localCache.listAll(key);
//            if (CollUtil.isNotEmpty(localResult)) {
//                return DynamicCacheStorageBatchAccessResult.hit(rawHolderMap2typeHolderMap(localResult), HitTier.LOCAL);
//            }
//        }
//
//        // 2. 远程缓存尝试
//        if (remoteCache != null) {
//            Map<String, CacheValueHolder<V>> remoteMap = remoteCache.listAll(key);
//            if (remoteMap != null && !remoteMap.isEmpty()) {
//                // 回写到本地
//                if (localCache != null) {
//                    localCache.putAll(key, remoteMap);
//                }
//                return DynamicCacheStorageBatchAccessResult.hit(rawHolderMap2typeHolderMap(remoteMap), HitTier.REMOTE);
//            }
//        }
//
//        // 3. miss
//        return DynamicCacheStorageBatchAccessResult.miss();
//    }



    @Override
    public BaseSingleResult<Void> put(SingleHashCachePutCommand<K, F, V> cmd) {
        String key = keyConverter.convert(cmd.getKey());
        String field = fieldConverter.convert(cmd.getField());
        V value = cmd.getValue();

        // Step 1: 写入本地缓存（如启用）
        if (localCache != null) {
            CacheValueHolder<V> localHolder = new CacheValueHolder<>(value, System.currentTimeMillis(),
                    calcLogicExpireAt(cmd.getLocalLogicTtlSecs()));
            localCache.put(key, field, localHolder);
        }

        // Step 2: 写入远程缓存（如启用）
        if (remoteCache != null) {
            CacheValueHolder<V> remoteHolder = new CacheValueHolder<>(value, System.currentTimeMillis(),
                    calcLogicExpireAt(cmd.getRemoteLogicTtlSecs()));
            long realTtlSecs = TtlRandomizer.randomizeSecs(cmd.getRemotePhysicalTtlSecs(),
                    config.getRemote().getTtlRandomPct());
            remoteCache.put(key, field, remoteHolder, realTtlSecs);
        }

        // Step 3: 返回结构化结果
        return ResultFactory.successSingle(componentName);
    }

    private long calcLogicExpireAt(Long logicExpireSecs) {
        return logicExpireSecs == null ? 0L : System.currentTimeMillis() + logicExpireSecs * 1000;
    }

//    @Override
//    public BaseBatchResult<Void, Void> putAll(K bizKey, Map<F, CacheValueHolder<V>> valueMap) {
//        String key = keyConverter.convert(bizKey);
//        final long localTtlSecs = TtlRandomizer.randomizeSecs(config.getLocal().getLogicTtlSecs(),
//                config.getLocal().getTtlRandomPct());
//        final long remoteTtlSecs = TtlRandomizer.randomizeSecs(config.getRemote().getTtlSecs(),
//                config.getRemote().getTtlRandomPct());
//
//        // 写入远程缓存
//        if (remoteCache != null) {
//            remoteCache.putAll(key, remoteHolderMap);
//        }
//        // 写入本地缓存
//        if (localCache != null) {
//            Map<String, CacheValueHolder<V>> localHolderMap = typeMap2rawHolderMap(valueMap, localTtlSecs);
//            localCache.putAll(key, localHolderMap);
//        }
//
//        return ResultFactory.successBatch(componentName);
//    }


//    @Override
//    public BaseSingleResult<Void> invalidate(K bizKey, F bizField) {
//        String key = keyConverter.convert(bizKey);
//        String field = fieldConverter.convert(bizField);
//
//        // 清除本地缓存
//        if (localCache != null) {
//            localCache.remove(key, field);
//        }
//
//        // 清除远程缓存
//        if (remoteCache != null) {
//            remoteCache.invalidate(key, field);
//        }
//
//        return BaseSingleResult.success(componentName);
//    }
//
//    @Override
//    public DynamicCacheStorageBatchAccessResult<Void, Void> invalidateAll(K bizKey) {
//        String key = keyConverter.convert(bizKey);
//
//        // 清除本地缓存
//        if (localCache != null) {
//            localCache.removeAll(key);
//        }
//
//        // 清除远程缓存
//        if (remoteCache != null) {
//            remoteCache.invalidateAll(key);
//        }
//
//        return DynamicCacheStorageBatchAccessResult.success();
//    }

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
