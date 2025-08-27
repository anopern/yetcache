package com.yetcache.core.cache.kv;

import com.yetcache.core.cache.CacheTtl;
import com.yetcache.core.cache.command.kv.KvCacheGetCommand;
import com.yetcache.core.cache.command.kv.KvCachePutCommand;
import com.yetcache.core.cache.command.kv.KvCacheRemoveCommand;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.loader.KVCacheLoader;
import com.yetcache.core.codec.JsonValueCodec;
import com.yetcache.core.config.kv.MultiLevelKVCacheConfig;
import com.yetcache.core.config.kv.MultiLevelKVCacheSpec;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.core.result.CacheResult;
import com.yetcache.core.result.HitLevel;
import com.yetcache.core.support.key.KeyConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
@Data
@Slf4j
public class MultiTierKVCache implements KVCache {
    protected String cacheName;
    private final MultiLevelKVCacheConfig config;
    private CaffeineKVCache localCache;
    private RedisKVCache remoteCache;
    private KeyConverter keyConverter;
    private final KVCacheLoader<?, ?> cacheLoader;

    public MultiTierKVCache(String cacheName,
                            MultiLevelKVCacheConfig config,
                            RedissonClient rClient,
                            KeyConverter keyConverter,
                            KVCacheLoader<?, ?> cacheLoader,
                            JsonValueCodec jsonValueCodec) {
        this.cacheName = cacheName;
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.keyConverter = keyConverter;
        MultiLevelKVCacheSpec spec = config.getSpec();
        if (spec.getCacheLevel().useLocal()) {
            this.localCache = new CaffeineKVCache(config.getLocal());
        }
        if (spec.getCacheLevel().useRemote()) {
            this.remoteCache = new RedisKVCache(rClient, jsonValueCodec);
        }
    }

    @Override
    public <T> CacheResult get(KvCacheGetCommand cmd) {
        String key = keyConverter.convert(cmd.getBizKey());
        // 1. 尝试从本地缓存读取
        if (localCache != null) {
            CacheValueHolder<T> valueHolder = localCache.getIfPresent(key);
            if (valueHolder != null && valueHolder.isNotLogicExpired()) {
                return BaseCacheResult.singleHit(cacheName, valueHolder, HitLevel.LOCAL);
            }
        }

        // 2. 尝试从远程缓存读取
        if (remoteCache != null) {
            CacheValueHolder<T> holder = remoteCache.get(key, cmd.valueTypeRef());
            if (holder != null && holder.isNotLogicExpired()) {
                // 回写到本地缓存
                if (localCache != null) {
                    localCache.put(key, holder);
                }
                return BaseCacheResult.singleHit(cacheName, holder, HitLevel.REMOTE);
            }
        }

        // 3. 所有缓存都 miss
        return BaseCacheResult.miss(cacheName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CacheResult put(KvCachePutCommand cmd) {
        Object bizKey = cmd.getBizKey();
        Object value = cmd.getValue();
        String key = keyConverter.convert(bizKey);
        CacheTtl cacheTtl = cmd.getTtl();

        // 写入远程缓存
        if (remoteCache != null) {
            CacheValueHolder<T> valueHolder = (CacheValueHolder<T>) CacheValueHolder.wrap(value,
                    cacheTtl.getRemoteLogicSecs());
            remoteCache.put(key, valueHolder, cacheTtl.getRemotePhysicalSecs());
        }

        // 写入本地缓存
        if (localCache != null) {
            CacheValueHolder<T> valueHolder = (CacheValueHolder<T>) CacheValueHolder.wrap(value,
                    cacheTtl.getLocalLogicSecs());
            localCache.put(key, valueHolder);
        }

        return BaseCacheResult.success(cacheName);
    }

    @Override
    public CacheResult remove(KvCacheRemoveCommand cmd) {
        String key = keyConverter.convert(cmd.getBizKey());

        // 清除本地缓存
        if (localCache != null) {
            localCache.remove(key);
        }

        // 清除远程缓存
        if (remoteCache != null) {
            remoteCache.remove(key);
        }

        return BaseCacheResult.success(cacheName);
    }
}
