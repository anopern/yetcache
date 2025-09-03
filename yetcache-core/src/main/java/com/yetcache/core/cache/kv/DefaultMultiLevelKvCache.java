package com.yetcache.core.cache.kv;

import com.yetcache.core.cache.CacheTtl;
import com.yetcache.core.cache.kv.command.KvCacheGetCommand;
import com.yetcache.core.cache.kv.command.KvCachePutCommand;
import com.yetcache.core.cache.kv.command.KvCacheRemoveCommand;
import com.yetcache.core.result.FreshnessInfo;
import com.yetcache.core.support.CacheValueHolder;
import com.yetcache.core.codec.JsonValueCodec;
import com.yetcache.core.config.kv.KvCacheConfig;
import com.yetcache.core.config.kv.KvCacheSpec;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.core.result.HitLevel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
@Data
@Slf4j
public class DefaultMultiLevelKvCache implements MultiLevelKvCache {
    protected String cacheName;
    private final KvCacheConfig config;
    private CaffeineKVCache localCache;
    private RedisKVCache remoteCache;

    public DefaultMultiLevelKvCache(String cacheName,
                                    KvCacheConfig config,
                                    RedissonClient rClient,
                                    JsonValueCodec jsonValueCodec) {
        this.cacheName = cacheName;
        this.config = config;
        KvCacheSpec spec = config.getSpec();
        if (spec.getCacheLevel().includesLocal()) {
            this.localCache = new CaffeineKVCache(config.getLocal());
        }
        if (spec.getCacheLevel().includesRemote()) {
            this.remoteCache = new RedisKVCache(rClient, jsonValueCodec);
        }
    }

    @Override
    public <T> BaseCacheResult<CacheValueHolder<T>> get(KvCacheGetCommand cmd) {
        log.debug("[Yetcache]DefaultMultiLevelKvCache get data, cmd: {}", cmd);
        String key = cmd.getKey();
        // 1. 尝试从本地缓存读取
        CacheValueHolder<T> localValueHolder = null;
        if (localCache != null) {
            localValueHolder = localCache.getIfPresent(key);
            log.debug("[Yetcache]DefaultMultiLevelKvCache get data from local cache, key: {}, value: {}", key, localValueHolder);
            if (localValueHolder != null && localValueHolder.isNotLogicExpired()) {
                return BaseCacheResult.singleHit(cacheName, localValueHolder, HitLevel.LOCAL, FreshnessInfo.fresh());
            }
        }

        // 2. 尝试从远程缓存读取
        CacheValueHolder<T> remoteValueHolder = null;
        if (remoteCache != null) {
            remoteValueHolder = remoteCache.get(key, cmd.valueTypeRef());
            log.debug("[Yetcache]DefaultMultiLevelKvCache get data from remote cache, key: {}, value: {}", key, remoteValueHolder);
            if (remoteValueHolder != null && remoteValueHolder.isNotLogicExpired()) {
                // 回写到本地缓存
                if (localCache != null) {
                    long now = System.currentTimeMillis();
                    long maxTtlMs = remoteValueHolder.getExpireTime() - now;
                    long ttlMs = Math.min(config.getLocal().getLogicTtlSecs() * 1000, maxTtlMs);
                    if (ttlMs > 0) {
                        int ttlSecs = (int) Math.max(1L, ttlMs / 1000L);
                        localCache.put(key, CacheValueHolder.wrap(remoteValueHolder.getValue(), ttlSecs));
                        log.debug("[Yetcache]DefaultMultiLevelKvCache got data from remote," +
                                " and write data to local cache, key: {}, value: {}", key, remoteValueHolder);
                    }
                }
                return BaseCacheResult.singleHit(cacheName, remoteValueHolder, HitLevel.REMOTE, FreshnessInfo.fresh());
            }
        }

        if (localValueHolder != null && remoteValueHolder != null) {
            CacheValueHolder<T> valueHolder = localValueHolder.getCreatedTime() >= remoteValueHolder.getCreatedTime()
                    ? localValueHolder : remoteValueHolder;
            HitLevel hitLevel = valueHolder == localValueHolder ? HitLevel.LOCAL : HitLevel.REMOTE;
            return BaseCacheResult.singleHit(cacheName, valueHolder, hitLevel, FreshnessInfo.stale());
        } else if (localValueHolder != null) {
            return BaseCacheResult.singleHit(cacheName, localValueHolder, HitLevel.LOCAL, FreshnessInfo.stale());
        } else if (remoteValueHolder != null) {
            return BaseCacheResult.singleHit(cacheName, remoteValueHolder, HitLevel.REMOTE, FreshnessInfo.stale());
        } else {
            return BaseCacheResult.miss(cacheName);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> BaseCacheResult<Void> put(KvCachePutCommand cmd) {
        log.debug("[Yetcache]DefaultMultiLevelKvCache put data, cmd: {}", cmd);
        String key = cmd.getKey();
        Object value = cmd.getValue();
        CacheTtl cacheTtl = cmd.getTtl();

        // 写入远程缓存
        if (remoteCache != null) {
            CacheValueHolder<T> valueHolder = (CacheValueHolder<T>) CacheValueHolder.wrap(value,
                    cacheTtl.getRemoteLogicSecs());
            remoteCache.put(key, valueHolder, cacheTtl.getRemotePhysicalSecs());
            log.debug("[Yetcache]DefaultMultiLevelKvCache put data to remote cache, key: {}, value: {}", key, valueHolder);
        }

        // 写入本地缓存
        if (localCache != null) {
            CacheValueHolder<T> valueHolder = (CacheValueHolder<T>) CacheValueHolder.wrap(value,
                    cacheTtl.getLocalLogicSecs());
            localCache.put(key, valueHolder);
            log.debug("[Yetcache]DefaultMultiLevelKvCache put data to local cache, key: {}, value: {}", key, valueHolder);
        }

        return BaseCacheResult.success(cacheName);
    }

    @Override
    public BaseCacheResult<Void> remove(KvCacheRemoveCommand cmd) {
        log.debug("[Yetcache]DefaultMultiLevelKvCache remove data, cmd: {}", cmd);
        String key = cmd.getKey();

        // 清除本地缓存
        if (localCache != null && (null == cmd.getCacheLevel() || cmd.getCacheLevel().includesLocal())) {
            log.debug("[Yetcache]DefaultMultiLevelKvCache remove data from local cache, key: {}", key);
            localCache.remove(key);
        }

        // 清除远程缓存
        if (remoteCache != null && (null == cmd.getCacheLevel() || cmd.getCacheLevel().includesRemote())) {
            log.debug("[Yetcache]DefaultMultiLevelKvCache remove data from remote cache, key: {}", key);
            remoteCache.remove(key);
        }

        return BaseCacheResult.success(cacheName);
    }
}
