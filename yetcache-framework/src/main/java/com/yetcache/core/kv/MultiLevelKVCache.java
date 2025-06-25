package com.yetcache.core.kv;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public abstract class MultiLevelKVCache<K, V> implements KVCache<K, V> {
    protected KVCacheConfig<K, V> cacheConfig;
    protected EmbeddedKVCache<K, V> embeddedCache;
    protected RemoteKVCache<K, V> remoteCache;
    protected KVCacheLoader<K, V> cacheLoader;

    public MultiLevelKVCache(KVCacheConfig<K, V> cacheConfig,
                             EmbeddedKVCache<K, V> embeddedCache,
                             RemoteKVCache<K, V> remoteCache) {
        this.cacheConfig = cacheConfig;
        this.embeddedCache = embeddedCache;
        this.remoteCache = remoteCache;
    }

    @Override
    public KVCacheGetResult<K, V> getWithResult(K key) {
        switch (cacheConfig.getCacheType()) {
            case LOCAL:
                return tryGetOrLoad(key, true, false);
            case REMOTE:
                return tryGetOrLoad(key, false, true);
            case BOTH:
                KVCacheGetResult<K, V> r = embeddedCache.getWithResult(key);
                if (r.getValue() != null) {
                    return r;
                }
                r = remoteCache.getWithResult(key);
                if (r.getValue() != null) {
                    embeddedCache.put(key, r.getValue());
                    return r;
                }
                return tryLoadOnly(key, true, true);
            default:
                throw new IllegalArgumentException("Unsupported cache type: " + cacheConfig.getCacheType());
        }
    }

    private KVCacheGetResult<K, V> tryGetOrLoad(K key, boolean putLocal, boolean putRemote) {
        KVCacheGetResult<K, V> r = putLocal ? embeddedCache.getWithResult(key) : remoteCache.getWithResult(key);
        if (r.getValue() != null) {
            return r;
        }
        return tryLoadOnly(key, putLocal, putRemote);
    }

    private KVCacheGetResult<K, V> tryLoadOnly(K key, boolean putLocal, boolean putRemote) {
        V v = cacheLoader.load(key);
        if (v != null) {
            if (putRemote) {
                remoteCache.put(key, v);
            }
            if (putLocal) {
                embeddedCache.put(key, v);
            }
        }
        return new KVCacheGetResult<>(v);
    }
}
