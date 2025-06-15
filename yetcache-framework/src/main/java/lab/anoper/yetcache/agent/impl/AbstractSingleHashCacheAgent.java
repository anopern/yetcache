package lab.anoper.yetcache.agent.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lab.anoper.yetcache.agent.ISingleHashAgent;
import lab.anoper.yetcache.enums.CacheType;
import lab.anoper.yetcache.properties.BaseCacheAgentProperties;
import lab.anoper.yetcache.source.ISingleHashCacheSourceService;
import lab.anoper.yetcache.utils.CacheRetryUtils;
import lab.anoper.yetcache.utils.LockUtils;
import lab.anoper.yetcache.utils.RedisSafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.HashOperations;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @param <E> 缓存DTO
 * @author walter.yan
 * @since 2025/06/15
 */
@Slf4j
public abstract class AbstractSingleHashCacheAgent<E> extends AbstractCacheAgent<E> implements ISingleHashAgent<E> {
    protected Cache<String, Map<String, E>> cache;
    protected HashOperations<String, String, E> hashOperations;
    protected ISingleHashCacheSourceService<E> sourceService;

    public AbstractSingleHashCacheAgent(BaseCacheAgentProperties properties,
                                        ISingleHashCacheSourceService<E> sourceService) {
        super(properties);
        this.sourceService = sourceService;
    }

    @PostConstruct
    public void init() throws Exception {
        super.init();
        this.hashOperations = redisTemplate.opsForHash();
        if (properties.isLocalCacheEnabled()) {
            this.cache = initCaffeineCache();
        }
    }

    protected Cache<String, Map<String, E>> initCaffeineCache() {
        return Caffeine.newBuilder()
                .maximumSize(properties.getJvmMaxSize())
                .expireAfterWrite(properties.getLocalExpireSecs(), TimeUnit.SECONDS)
                .build();
    }

    @Override
    public List<E> listAll() {
        if (isTenantScoped()) {
            return listAll(getCurTenantId());
        } else {
            return listAll(null);
        }
    }

    @Override
    public List<E> listAll(Long tenantId) {
        String key = getKeyWithTenant(tenantId);
        if (CacheType.LOCAL_CACHE == properties.getCacheType()) {
            Map<String, E> dataMap = cache.getIfPresent(key);
            if (CollUtil.isNotEmpty(dataMap)) {
                return new ArrayList<>(dataMap.values());
            }
            return listFromSource(tenantId, false);
        }
        if (CacheType.REMOTE_CACHE == properties.getCacheType()) {
            List<E> list = listFromRedis(tenantId);
            if (CollUtil.isNotEmpty(list)) {
                return list;
            }
            return listFromSource(tenantId, false);
        }
        if (CacheType.BOTH == properties.getCacheType()) {
            Map<String, E> dataMap = cache.getIfPresent(key);
            if (CollUtil.isNotEmpty(dataMap)) {
                return new ArrayList<>(dataMap.values());
            }
            List<E> list = listFromRedis(tenantId);
            if (CollUtil.isNotEmpty(list)) {
                return list;
            }
            return listFromSource(tenantId, false);
        }
        return null;
    }

    @Override
    public E get(String bizHashKey) {
        if (isTenantScoped()) {
            return get(getCurTenantId(), bizHashKey);
        } else {
            return get(null, bizHashKey);
        }
    }

    @Override
    public E get(Long tenantId, String bizHashKey) {
        List<E> list = listAll(tenantId);
        if (CollUtil.isNotEmpty(list)) {
            return list.stream()
                    .filter(e -> StrUtil.equals(bizHashKey, getBizHashKey(e)))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public void refreshAllCache() {
        if (isTenantScoped()) {
            refreshAllCache(getCurTenantId());
        } else {
            refreshAllCache(null);
        }
    }

    @Override
    public void refreshAllCache(Long tenantId) {
        List<E> list = listFromSource(tenantId, true);
        Map<String, E> dataMap = null;
        if (CollUtil.isNotEmpty(list)) {
            dataMap = list.stream().collect(Collectors.toMap(
                    this::getBizHashKey,
                    Function.identity(),
                    (v1, v2) -> v1,
                    ConcurrentHashMap::new));
        }
        String key = getKeyWithTenant(tenantId);
        if (CacheType.LOCAL_CACHE == properties.getCacheType()) {
            if (CollUtil.isNotEmpty(list)) {
                cache.put(key, dataMap);
            } else {
                cache.invalidate(key);
            }
        }
        if (CacheType.REMOTE_CACHE == properties.getCacheType()) {
            if (CollUtil.isNotEmpty(list)) {
                RedisSafeUtils.safePutHashEntries(key, dataMap, (k, v) -> hashOperations.putAll(k, v));
            } else {
                RedisSafeUtils.safeDelete(key, (k) -> hashOperations.delete(k));
            }
        }
        if (CacheType.BOTH == properties.getCacheType()) {
            if (CollUtil.isNotEmpty(list)) {
                cache.put(key, dataMap);
                RedisSafeUtils.safePutHashEntries(key, dataMap, (k, v) -> hashOperations.putAll(k, v));
            } else {
                cache.invalidate(key);
                RedisSafeUtils.safeDelete(key, (k) -> hashOperations.delete(k));
            }
        }
    }

    @Override
    public void handleMessage(String message) {

    }

    private List<E> listFromRedis(Long tenantId) {
        String key = getKeyWithTenant(tenantId);
        Map<String, E> entries = RedisSafeUtils.safeGetHashEntries(key, (k) -> hashOperations.entries(k));
        if (CollUtil.isNotEmpty(entries)) {
            return new ArrayList<>(entries.values());
        }
        return Collections.emptyList();
    }

    private List<E> listFromSource(Long tenantId, boolean force) {
        String lockKey = getQuerySourceDistLockKey(tenantId);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (force) {
                return sourceService.queryAll(tenantId);
            }
            // 最多等 100ms 尝试拿锁。拿到锁后持有 5s，防止死锁
            if (lock.tryLock(100, 5, TimeUnit.SECONDS)) {
                // 加锁后再检查一次 Redis，防止缓存已被其他线程更新
                Map<String, E> dataMap = hashOperations.entries(properties.getCacheKey());
                if (CollUtil.isNotEmpty(dataMap)) {
                    return new ArrayList<>(dataMap.values());
                }

                // 从数据源加载
                return sourceService.queryAll(tenantId);
            } else {
                log.warn("未获取到分布式锁，lockKey: {}, 正在等待其他线程回源填充缓存", lockKey);
                Map<String, E> dataMap = CacheRetryUtils.retryRedisHashEntries(
                        () -> hashOperations.entries(properties.getCacheKey()), 3, 50);
                if (CollUtil.isNotEmpty(dataMap)) {
                    return new ArrayList<>(dataMap.values());
                }
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for lock", e);
        } finally {
            LockUtils.safeUnlock(lock);
        }
    }

    protected String getKeyWithTenant(Long tenantId) {
        if (null == tenantId) {
            return String.format("%s", properties.getCacheKey());
        }
        return String.format("%s:%d", properties.getCacheKey(), tenantId);
    }

    protected String getQuerySourceDistLockKey(Long tenantId) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:%s", QUERY_SOURCE_LOCK_KEY_PRE, getAgentId()));
        if (tenantId != null) {
            sb.append(String.format(":%s", tenantId));
        }
        return sb.toString();
    }

    protected abstract String getBizHashKey(@NotNull E e);
}
