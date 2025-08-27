package com.yetcache.core.cache.hash;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.core.cache.command.hash.*;
import com.yetcache.core.codec.*;
import com.yetcache.core.cache.WriteLevel;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.result.*;
import com.yetcache.core.config.hash.HashCacheConfig;
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
public class DefaultMultiLevelHashCache implements MultiLevelHashCache {
    private final String componentName;
    private CaffeineHashCache localCache;
    private RedisHashCache remoteCache;
    private final KeyConverter keyConverter;
    private final FieldConverter fieldConverter;

    public DefaultMultiLevelHashCache(String componentName,
                                      HashCacheConfig config,
                                      RedissonClient redissonClient,
                                      KeyConverter keyConverter,
                                      FieldConverter fieldConverter,
                                      JsonValueCodec jsonValueCodec) {
        this.componentName = Objects.requireNonNull(componentName, "cacheName");
        this.keyConverter = Objects.requireNonNull(keyConverter, "keyConverter");
        this.fieldConverter = Objects.requireNonNull(fieldConverter, "fieldConverter");

        if (config.getSpec().getCacheLevel().useLocal()) {
            this.localCache = new CaffeineHashCache(config.getLocal());
        }
        if (config.getSpec().getCacheLevel().useRemote()) {
            this.remoteCache = new RedisHashCache(redissonClient, jsonValueCodec);
        }
    }

    @Override
    public <T> CacheResult get(HashCacheGetCommand cmd) {
        String key = keyConverter.convert(cmd.getBizKey());
        String field = fieldConverter.convert(cmd.getBizField());

        // 1. 尝试从本地缓存读取
        if (localCache != null) {
            CacheValueHolder<T> valueHolder = localCache.getIfPresent(key, field, cmd.getValueTypeRef());
            if (valueHolder != null && valueHolder.isNotLogicExpired()) {
                return BaseCacheResult.singleHit(componentName, valueHolder, HitLevel.LOCAL);
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
                return BaseCacheResult.singleHit(componentName, holder, HitLevel.REMOTE);
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
            final Map<Object, HitLevel> hitLevelMap = new HashMap<>();
            // 本地缓存尝试
            final Map<String, CacheValueHolder<T>> localResult = localCache != null
                    ? localCache.batchGet(key, rawFields, cmd.valueTypeRef()) : Collections.emptyMap();
            List<String> missingLocalFields = new ArrayList<>(rawFields);
            for (Map.Entry<String, CacheValueHolder<T>> entry : localResult.entrySet()) {
                Object bizField = fieldConverter.revert(entry.getKey());
                CacheValueHolder<T> valueHolder = entry.getValue();
                if (null != valueHolder && valueHolder.isNotLogicExpired()) {
                    valueHolderMap.put(bizField, valueHolder);
                    hitLevelMap.put(bizField, HitLevel.LOCAL);
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

            return BaseCacheResult.batchHit(componentName, valueHolderMap, DefaultHitLevelInfo.of(hitLevelMap));
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


    @Override
    public <T> CacheResult putAll(HashCachePutAllCommand cmd) {
        Object bizKey = cmd.getBizKey();
        Map<Object, Object> valueMap = cmd.getValueMap();
        String key = keyConverter.convert(bizKey);

        // 写入远程缓存
        if (isWriteRemote(cmd.getWriteLevel()) && remoteCache != null) {
            Map<String, CacheValueHolder<T>> remoteHolderMap = typeMap2rawHolderMap(valueMap,
                    cmd.getTtl().getRemoteLogicSecs());
            remoteCache.putAll(key, remoteHolderMap, cmd.getTtl().getRemotePhysicalSecs());
        }

        // 写入本地缓存
        if (isWriteLocal(cmd.getWriteLevel()) && localCache != null) {
            Map<String, CacheValueHolder<T>> localHolderMap = typeMap2rawHolderMap(valueMap,
                    cmd.getTtl().getLocalLogicSecs());
            localCache.putAll(key, localHolderMap);
        }

        return BaseCacheResult.success(componentName);
    }

    @Override
    public   CacheResult batchRemove(HashCacheBatchRemoveCommand cmd) {
        String key = keyConverter.convert(cmd.getBizKey());
        List<String> fields = cmd.getBizFields().stream().map(fieldConverter::convert)
                .collect(Collectors.toList());
        if (localCache != null) {
            localCache.batchRemove(key, fields);
        }
        if (remoteCache != null) {
            remoteCache.batchRemove(key, fields);
        }
        return BaseCacheResult.success(componentName);
    }

    private boolean isWriteLocal(WriteLevel writeLevel) {
        return writeLevel == WriteLevel.ALL || writeLevel == WriteLevel.LOCAL;
    }

    private boolean isWriteRemote(WriteLevel writeLevel) {
        return writeLevel == WriteLevel.ALL || writeLevel == WriteLevel.REMOTE;
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

        return BaseCacheResult.success(componentName);
    }

    @SuppressWarnings("unchecked")
    private <T> Map<String, CacheValueHolder<T>> typeMap2rawHolderMap(Map<Object, ?> valueMap, long ttlSecs) {
        return valueMap.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> fieldConverter.convert(e.getKey()),
                        e -> (CacheValueHolder<T>) CacheValueHolder.wrap(e.getValue(), ttlSecs)
                ));
    }

}
