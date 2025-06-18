package com.yetcache.utils;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author walter.yan
 * @since 2025/5/22
 */
@Slf4j
public class RedisSafeUtils {
    /**
     * 安全地写入缓存，自动捕获异常并记录日志
     *
     * @param key           缓存Key
     * @param valueConsumer 负责写缓存的函数，通常是 Redis 操作 lambda，例如 (key, value) -> valueOperations.set(key, value, expire, unit)
     * @param <T>           缓存值类型
     * @param value         缓存值
     */
    public static <T> void safeSet(String key, T value, CacheSetter<T> valueConsumer) {
        try {
            valueConsumer.set(key, value);
        } catch (Exception e) {
            log.error("写入缓存失败，key: {}", key, e);
            // 可扩展：这里可以加告警、埋点等
        }
    }

    /**
     * 带过期时间的缓存写入函数式接口
     *
     * @param <T> 缓存值类型
     */
    @FunctionalInterface
    public interface CacheSetter<T> {
        void set(String key, T value) throws Exception;
    }

    /**
     * 安全地写入 Hash 缓存，自动捕获异常并记录日志
     *
     * @param key        Redis Hash 的主 key
     * @param hashKey    Hash 的 field（子 key）
     * @param value      要写入的值
     * @param hashSetter 负责写入的函数式接口，通常是 lambda，例如 (redisKey, hashKey, value) -> hashOperations.put(redisKey, hashKey, value)
     * @param <T>        值的类型
     */
    public static <T> void safeHashSet(String key, String hashKey, T value, HashCacheSetter<T> hashSetter) {
        try {
            hashSetter.set(key, hashKey, value);
        } catch (Exception e) {
            log.error("写入 Hash 缓存失败，redisKey: {}, hashKey: {}", key, hashKey, e);
            // 可扩展：告警、监控等
        }
    }

    @Deprecated
    public static <T> void safeHashSetAll(String key, Map<String, T> dataMap, HashBatchCacheSetter<T> hashSetter) {
        if (CollUtil.isEmpty(dataMap)) {
            return;
        }
        try {
            hashSetter.setAll(key, dataMap);
        } catch (Exception e) {
            log.error("批量写入 Hash 缓存失败，redisKey: {}, fieldSize: {}", key, dataMap.size(), e);
            // 可扩展：报警、监控
        }
    }


    /**
     * Redis Hash 写入函数式接口
     *
     * @param <T> 值的类型
     */
    @FunctionalInterface
    public interface HashCacheSetter<T> {
        void set(String key, String hashKey, T value) throws Exception;
    }

    @FunctionalInterface
    public interface HashBatchCacheSetter<T> {
        void setAll(String key, Map<String, T> values);
    }

    /**
     * 安全地删除缓存（String 类型），自动捕获异常并记录日志
     *
     * @param key          Redis 的 Key
     * @param deleteAction 删除操作，例如 redisTemplate.delete(key)
     */
    public static void safeDelete(String key, CacheDeleteAction deleteAction) {
        try {
            deleteAction.delete(key);
        } catch (Exception e) {
            log.error("删除缓存失败，key: {}", key, e);
            // 可扩展告警逻辑
        }
    }

    @FunctionalInterface
    public interface CacheDeleteAction {
        void delete(String key) throws Exception;
    }

    /**
     * 安全地删除 Hash 缓存字段，自动捕获异常并记录日志
     *
     * @param key          Redis 的主 key（hash key）
     * @param hashKeys     要删除的字段列表
     * @param deleteAction 删除操作，例如 (redisKey, keys) -> hashOperations.delete(redisKey, keys.toArray(new String[0]))
     */
    public static void safeHashDelete(String key, Collection<String> hashKeys, HashDeleteAction deleteAction) {
        if (CollUtil.isEmpty(hashKeys)) {
            return;
        }
        try {
            deleteAction.delete(key, hashKeys);
        } catch (Exception e) {
            log.error("删除 Hash 缓存字段失败，redisKey: {}, hashKeys: {}", key, hashKeys, e);
            // 可扩展告警逻辑
        }
    }

    /**
     * Redis Hash 删除函数式接口
     */
    @FunctionalInterface
    public interface HashDeleteAction {
        void delete(String redisKey, Collection<String> hashKeys) throws Exception;
    }

    /**
     * 安全地获取缓存值（String类型KV），自动捕获异常并记录日志
     *
     * @param key       缓存Key
     * @param getAction 负责获取缓存的函数式接口，通常是 lambda，例如 (key) -> valueOperations.get(key)
     * @param <T>       缓存值类型
     * @return 获取到的值，异常时返回null
     */
    public static <T> T safeGet(String key, CacheGetter<T> getAction) {
        try {
            return getAction.get(key);
        } catch (Exception e) {
            log.error("获取缓存失败，key: {}", key, e);
            // 可扩展告警逻辑
            return null;
        }
    }

    /**
     * 缓存获取函数式接口
     *
     * @param <T> 缓存值类型
     */
    @FunctionalInterface
    public interface CacheGetter<T> {
        T get(String key) throws Exception;
    }


    @FunctionalInterface
    public interface HashBatchPutAction<T> {
        void put(String key, Map<String, T> dataMap, int expireSec, int batchSize) throws Exception;
    }

    /**
     * 安全地批量写入 Redis Hash 缓存，自动捕获异常并记录日志
     *
     * @param key       Redis 的 Hash Key
     * @param dataMap   要写入的数据（field -> value）
     * @param expireSec 过期时间（秒）
     * @param batchSize 批处理大小
     * @param putAction 实际执行写入逻辑的函数式接口
     * @param <T>       缓存值类型
     */
    public static <T> void safeBatchPutHash(
            String key,
            Map<String, T> dataMap,
            int expireSec,
            int batchSize,
            HashBatchPutAction<T> putAction) {
        if (CollUtil.isEmpty(dataMap)) {
            return;
        }
        try {
            putAction.put(key, dataMap, expireSec, batchSize);
        } catch (Exception e) {
            log.error("批量写入 Hash 缓存失败，key: {}, size: {}", key, dataMap.size(), e);
            // 可扩展告警、埋点等
        }
    }


    @FunctionalInterface
    public interface HashBatchGetAction<T> {
        Map<String, T> get(String key, int batchSize) throws Exception;
    }

    /**
     * 安全地从 Redis 批量获取 Hash 数据，自动捕获异常并记录日志
     *
     * @param key       Redis 的 Hash Key
     * @param batchSize 批量读取大小
     * @param getAction 获取动作的函数式接口
     * @param <T>       数据类型
     * @return 获取的数据 Map，失败时返回空 Map
     */
    public static <T> Map<String, T> safeBatchGetHash(
            String key,
            int batchSize,
            HashBatchGetAction<T> getAction) {
        try {
            return getAction.get(key, batchSize);
        } catch (Exception e) {
            log.error("批量读取 Hash 缓存失败，key: {}, batchSize: {}", key, batchSize, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 安全地从 Redis 获取整个 Hash 的所有键值对，自动捕获异常并记录日志
     *
     * @param key     Redis 的 Hash Key
     * @param getFunc 获取操作的函数式接口（通常是 RedisTemplate 的 entries 方法）
     * @param <T>     值类型
     * @return Hash 中的所有键值对，失败时返回空 Map
     */
    public static <T> Map<String, T> safeGetHashEntries(String key, Function<String, Map<String, T>> getFunc) {
        try {
            return getFunc.apply(key);
        } catch (Exception e) {
            log.error("读取 Redis Hash 缓存失败，key: {}", key, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 安全地替换整个 Redis Hash（删除原 Hash 后重新写入），适合配置等场景。
     *
     * @param key            Redis Hash key
     * @param newEntries     新的 field-value 映射
     * @param hashOperations Spring 的 Hash 操作
     * @param redisTemplate  RedisTemplate
     */
    public static <T> void safeReplaceHash(String key,
                                           Map<String, T> newEntries,
                                           HashOperations<String, String, T> hashOperations,
                                           RedisTemplate<String, T> redisTemplate) {
        try {
            redisTemplate.delete(key); // 删除整个旧 hash
            if (CollUtil.isNotEmpty(newEntries)) {
                hashOperations.putAll(key, newEntries); // 写入新内容
            }
        } catch (Exception e) {
            log.error("替换 Redis Hash 失败，key: {}, newSize: {}", key, newEntries.size(), e);
        }
    }
}
