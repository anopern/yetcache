package com.yetcache.core.cache.hash;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.core.cache.command.HashCacheRemoveCommand;
import com.yetcache.core.codec.*;
import com.yetcache.core.cache.WriteTier;
import com.yetcache.core.cache.command.HashCacheBatchGetCommand;
import com.yetcache.core.cache.command.HashCacheSingleGetCommand;
import com.yetcache.core.cache.command.HashCachePutAllCommand;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.result.*;
import com.yetcache.core.config.dynamichash.HashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Slf4j
public class DefaultMultiTierHashCache implements MultiTierHashCache {
    private final String componentName;
    private CaffeineHashCache localCache;
    private RedisHashCache remoteCache;
    private final KeyConverter keyConverter;
    private final FieldConverter fieldConverter;

    public DefaultMultiTierHashCache(String componentName,
                                     HashCacheConfig config,
                                     RedissonClient redissonClient,
                                     KeyConverter keyConverter,
                                     FieldConverter fieldConverter,
                                     JsonValueCodec jsonValueCodec) {
        this.componentName = Objects.requireNonNull(componentName, "cacheName");
        this.keyConverter = Objects.requireNonNull(keyConverter, "keyConverter");
        this.fieldConverter = Objects.requireNonNull(fieldConverter, "fieldConverter");

        if (config.getSpec().getCacheTier().useLocal()) {
            this.localCache = new CaffeineHashCache(config.getLocal());
        }
        if (config.getSpec().getCacheTier().useRemote()) {
            this.remoteCache = new RedisHashCache(redissonClient, jsonValueCodec);
        }
    }

    @Override
    public <T> CacheResult get(HashCacheSingleGetCommand cmd) {
        String key = keyConverter.convert(cmd.getBizKey());
        String field = fieldConverter.convert(cmd.getBizField());

        // 1. 尝试从本地缓存读取
        if (localCache != null) {
            CacheValueHolder<T> valueHolder = localCache.getIfPresent(key, field, cmd.getValueTypeRef());
            if (valueHolder != null && valueHolder.isNotLogicExpired()) {
                return BaseCacheResult.singleHit(componentName, valueHolder, HitTier.LOCAL);
            }
        }

        // 2. 尝试从远程缓存读取
        if (remoteCache != null) {
            CacheValueHolder<T> holder = remoteCache.get(key, field, cmd.valueTypeRef());
            if (holder != null && holder.isNotLogicExpired()) {
                // 回写到本地缓存
                if (localCache != null) {
                    localCache.put(key, field, holder);
                }
                return BaseCacheResult.singleHit(componentName, holder, HitTier.REMOTE);
            }
        }

        // 3. 所有缓存都 miss
        return BaseCacheResult.miss(componentName);
    }

    @Override
    public <T> CacheResult batchGet(HashCacheBatchGetCommand cmd) {
        Object bizKey = cmd.getBizKey();
        List<Object> bizFields = cmd.getBizFields();
        try {
            String key = keyConverter.convert(bizKey);
            // 批量转换为原始字段
            List<String> rawFields = bizFields.stream().map(fieldConverter::convert).collect(Collectors.toList());
            final Map<Object, CacheValueHolder<T>> valueHolderMap = new HashMap<>();
            final Map<Object, HitTier> hitTierMap = new HashMap<>();
            // 本地缓存尝试
            final Map<String, CacheValueHolder<T>> localResult = localCache != null
                    ? localCache.batchGet(key, rawFields, cmd.valueTypeRef()) : Collections.emptyMap();
            List<String> missingLocalFields = new ArrayList<>(rawFields);
            for (Map.Entry<String, CacheValueHolder<T>> entry : localResult.entrySet()) {
                Object bizField = fieldConverter.revert(entry.getKey());
                CacheValueHolder<T> valueHolder = entry.getValue();
                if (null != valueHolder && valueHolder.isNotLogicExpired()) {
                    valueHolderMap.put(bizField, valueHolder);
                    hitTierMap.put(bizField, HitTier.LOCAL);
                    missingLocalFields.remove(entry.getKey());
                }
            }

            if (CollUtil.isNotEmpty(missingLocalFields) && remoteCache != null) {
                Map<String, CacheValueHolder<T>> remoteResult = remoteCache.batchGet(key, missingLocalFields,
                        cmd.getValueTypeRef());
                // 写回本地缓存（仅非过期）
                for (Map.Entry<String, CacheValueHolder<T>> entry : remoteResult.entrySet()) {
                    String field = entry.getKey();
                    CacheValueHolder<T> valueHolder = entry.getValue();
                    if (valueHolder != null && valueHolder.isNotLogicExpired()) {
                        localCache.put(key, field, valueHolder);
                        valueHolderMap.put(fieldConverter.revert(field), valueHolder);
                    }
                }
            }

            return BaseCacheResult.batchHit(componentName, valueHolderMap, DefaultHitTierInfo.of(hitTierMap));
        } catch (Exception e) {
            log.warn("缓存回源加载失败，cacheName={}, bizKey={}, bizFields={}", componentName, bizKey, bizFields, e);
            return BaseCacheResult.fail(componentName, e);
        }
    }

//    @Override
//    public BaseBatchResult<F, V> listAll(K bizKey) {
//        String key = keyConverter.convert(bizKey);
//
//        // 1. 本地缓存尝试
//        if (localCache != null) {
//            Map<String, CacheValueHolder> localResult = localCache.listAll(key);
//            if (CollUtil.isNotEmpty(localResult)) {
//                return DynamicCacheStorageBatchAccessResult.hit(rawHolderMap2typeHolderMap(localResult), HitTier.LOCAL);
//            }
//        }
//
//        // 2. 远程缓存尝试
//        if (remoteCache != null) {
//            Map<String, CacheValueHolder> remoteMap = remoteCache.listAll(key);
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


//    @Override
//    @SuppressWarnings("unchecked")
//    public CacheResult put(HashCacheSinglePutCommand cmd) {
//        String key = keyConverter.convert(cmd.getBizKey());
//        String field = fieldConverter.convert(cmd.getBizField());
//        Object value = cmd.getValue();
//
//        // Step 1: 写入本地缓存（如启用）
//        if (localCache != null) {
//            CacheValueHolder localHolder = (CacheValueHolder) CacheValueHolder.wrap(value,
//                    cmd.getLocalLogicTtlSecs());
//            localCache.put(key, field, localHolder);
//        }
//
//        // Step 2: 写入远程缓存（如启用）
//        if (remoteCache != null) {
//            CacheValueHolder remoteHolder = (CacheValueHolder) CacheValueHolder.wrap(value,
//                    cmd.getRemoteLogicTtlSecs());
//            long realTtlSecs = TtlRandomizer.randomizeSecs(cmd.getRemotePhysicalTtlSecs(),
//                    config.getRemote().getTtlRandomPct());
//            remoteCache.put(key, field, remoteHolder, realTtlSecs);
//        }
//
//        // Step 3: 返回结构化结果
//        return SingleCacheResult.success(componentName);
//    }

    @Override
    public <T> CacheResult putAll(HashCachePutAllCommand cmd) {
        Object bizKey = cmd.getBizKey();
        Map<Object, Object> valueMap = cmd.getValueMap();
        String key = keyConverter.convert(bizKey);

        // 写入远程缓存
        if (isWriteRemote(cmd.getWriteTier()) && remoteCache != null) {
            Map<String, CacheValueHolder<T>> remoteHolderMap = typeMap2rawHolderMap(valueMap,
                    cmd.getTtl().getRemoteLogicSecs());
            remoteCache.putAll(key, remoteHolderMap, cmd.getTtl().getRemotePhysicalSecs());
        }

        // 写入本地缓存
        if (isWriteLocal(cmd.getWriteTier()) && localCache != null) {
            Map<String, CacheValueHolder<T>> localHolderMap = typeMap2rawHolderMap(valueMap,
                    cmd.getTtl().getLocalLogicSecs());
            localCache.putAll(key, localHolderMap);
        }

        return BaseCacheResult.success(componentName);
    }

    private boolean isWriteLocal(WriteTier writeTier) {
        return writeTier == WriteTier.ALL || writeTier == WriteTier.LOCAL;
    }

    private boolean isWriteRemote(WriteTier writeTier) {
        return writeTier == WriteTier.ALL || writeTier == WriteTier.REMOTE;
    }


    @Override
    public CacheResult remove(HashCacheRemoveCommand cmd) {
        String key = keyConverter.convert(cmd.getBizKey());
        String field = fieldConverter.convert(cmd.getBizField());

        // 清除本地缓存
        if (localCache != null) {
            localCache.remove(key, field);
        }

        // 清除远程缓存
        if (remoteCache != null) {
            remoteCache.remove(key, field);
        }

        return BatchCacheResult.success(componentName);
    }
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

    //    private Map<F, CacheValueHolder> rawHolderMap2typeHolderMap(Map<String, CacheValueHolder> rwaMap) {
//        // 构建返回值 Map<F, CacheValueHolder>（回转字段）
//        return rwaMap.entrySet().stream()
//                .collect(Collectors.toMap(
//                        e -> fieldConverter.revert(e.getKey()),
//                        Map.Entry::getValue
//                ));
//    }
//
    @SuppressWarnings("unchecked")
    private <T> Map<String, CacheValueHolder<T>> typeMap2rawHolderMap(Map<Object, ?> valueMap, long ttlSecs) {
        return valueMap.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> fieldConverter.convert(e.getKey()),
                        e -> (CacheValueHolder<T>) CacheValueHolder.wrap(e.getValue(), ttlSecs)
                ));
    }

}
