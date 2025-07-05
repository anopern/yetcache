//package com.yetcache.core.cache.kv;
//
//import com.yetcache.core.cache.AbstractMultiTierCache;
//import com.yetcache.core.cache.support.CacheValueHolder;
//import com.yetcache.core.support.trace.dynamichash.SourceLoadStatus;
//import com.yetcache.core.cache.loader.KVCacheLoader;
//import com.yetcache.core.config.MultiTierKVCacheConfig;
//import com.yetcache.core.config.PenetrationProtectConfig;
//import com.yetcache.core.support.key.KeyConverter;
//import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
//import com.yetcache.core.protect.RedisPenetrationProtectCache;
//import com.yetcache.core.support.util.CacheParamChecker;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RedissonClient;
//
///**
// * @author walter.yan
// * @since 2025/6/25
// */
//@EqualsAndHashCode(callSuper = true)
//@Data
//@Slf4j
//public class MultiTierKVCache<K, V> extends AbstractMultiTierCache implements KVCache<K, V> {
//    private final MultiTierKVCacheConfig config;
//    private final KVCacheLoader<K, V> cacheLoader;
//    private CaffeineKVCache<V> localCache;
//    private RedisKVCache<V> redisCache;
//    private KeyConverter<K> keyConverter;
//
//    public MultiTierKVCache(String cacheName,
//                            MultiTierKVCacheConfig config,
//                            RedissonClient rClient,
//                            KVCacheLoader<K, V> cacheLoader,
//                            KeyConverter<K> keyConverter) {
//        this.cacheName = cacheName;
//        this.config = config;
//        this.cacheLoader = cacheLoader;
//        this.keyConverter = keyConverter;
//
//        if (config.getCacheTier().useLocal()) {
//            config.getLocal().setTtlRandomPct(config.getTtlRandomPercent());
//            this.localCache = new CaffeineKVCache<>(config.getLocal());
//
//            PenetrationProtectConfig ppConfig = config.getLocal().getPenetrationProtect();
//            this.localPpCache = new CaffeinePenetrationProtectCache(ppConfig.getPrefix(), cacheName,
//                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
//        }
//
//        if (config.getCacheTier().useRemote()) {
//            config.getRemote().setTtlRandomPct(config.getTtlRandomPercent());
//            this.redisCache = new RedisKVCache<>(config.getRemote(), rClient);
//
//            PenetrationProtectConfig ppConfig = config.getRemote().getPenetrationProtect();
//            this.remotePpCache = new RedisPenetrationProtectCache(rClient, ppConfig.getPrefix(), cacheName,
//                    ppConfig.getTtlSecs(), ppConfig.getMaxSize());
//        }
//    }
//
//    @Override
//    public V get(K bizKey) {
//        KVCacheGetResult<V> getResult = getWithResult(bizKey);
//        log.debug("CacheGetResult: {}", getResult);
//        if (getResult.getValueHolder() != null) {
//            return getResult.getValueHolder().getValue();
//        }
//        return null;
//    }
//
//    @Override
//    public KVCacheGetResult<V> getWithResult(K bizKey) {
//        CacheParamChecker.failIfNull(bizKey, cacheName);
//        String key = keyConverter.convert(bizKey);
//
//        CacheValueHolder<V> localHolder;
//        CacheValueHolder<V> remoteHolder;
//
//        KVCacheGetResult<V> getResult = new KVCacheGetResult<>();
//        if (null != localPpCache) {
//            boolean blocked = localPpCache.isBlocked(key);
//            if (blocked) {
//                return getResult;
//            }
//        }
//
//        if (null != remotePpCache) {
//            boolean blocked = remotePpCache.isBlocked(key);
//            if (blocked) {
//                return getResult;
//            }
//        }
//
//        // === 1. 本地缓存 ===
//        if (localCache != null) {
//            localHolder = localCache.getIfPresent(key);
//            if (localHolder != null && localHolder.isNotLogicExpired()) {
//                getResult.setValueHolder(localHolder);
//                return getResult;
//            }
//        }
//
//        // === 2. Redis 缓存 ===
//        if (redisCache != null) {
//            remoteHolder = redisCache.getIfPresent(key);
//            if (remoteHolder != null && remoteHolder.isNotLogicExpired()) {
//                // 回写本地
//                if (localCache != null) {
//                    localCache.put(key, CacheValueHolder.wrap(remoteHolder.getValue(), config.getLocal().getTtlSecs()));
//                }
//                getResult.setValueHolder(remoteHolder);
//                return getResult;
//            }
//        }
//
//        // === 3. 回源加载 ===
//        try {
//            V loaded = cacheLoader.load(bizKey);
//            if (loaded != null) {
//                if (redisCache != null) {
//                    CacheValueHolder<V> valueHolder = CacheValueHolder.wrap(loaded, config.getRemote().getTtlSecs());
//                    redisCache.put(key, valueHolder);
//                }
//                if (localCache != null) {
//                    CacheValueHolder<V> valueHolder = CacheValueHolder.wrap(loaded, config.getLocal().getTtlSecs());
//                    localCache.put(key, valueHolder);
//                }
//                getResult.setValueHolder(new CacheValueHolder<>(loaded));
//            } else {
//                if (null != localPpCache) {
//                    localPpCache.markMiss(key);
//                }
//                if (null != remotePpCache) {
//                    remotePpCache.markMiss(key);
//                }
//            }
//        } catch (Exception e) {
//            log.warn("缓存回源加载失败，cacheName={}, bizKey={}, key={}, ", cacheName, bizKey, key, e);
//        }
//        return getResult;
//    }
//
//    @Override
//    public KVCacheRefreshResult<K, V> refresh(K bizKey) {
//        Long startMills = System.currentTimeMillis();
//        CacheParamChecker.failIfNull(bizKey, cacheName);
//        String key = keyConverter.convert(bizKey);
//        KVCacheRefreshResult<K, V> refreshResult = new KVCacheRefreshResult<>(cacheName, config.getCacheTier(),
//                bizKey, key, startMills);
//        V loaded = cacheLoader.load(bizKey);
//        if (loaded != null) {
//            if (localCache != null) {
//                localCache.put(key, CacheValueHolder.wrap(loaded, config.getLocal().getTtlSecs()));
//            }
//            if (redisCache != null) {
//                redisCache.put(key, CacheValueHolder.wrap(loaded, config.getRemote().getTtlSecs()));
//            }
//            refreshResult.setValueHolder(new CacheValueHolder<>(loaded));
//        } else {
//            refreshResult.setLoadStatus(SourceLoadStatus.NO_VALUE);
//        }
//        refreshResult.end();
//        return refreshResult;
//    }
//}
