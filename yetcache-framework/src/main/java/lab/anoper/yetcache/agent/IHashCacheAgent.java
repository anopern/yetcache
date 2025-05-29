package lab.anoper.yetcache.agent;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author walter.yan
 * @since 2025/5/21
 */
public interface IHashCacheAgent<E> extends ICacheAgent {
//    /**
//     * 获取单个字段数据（缓存未命中时自动回源并写回缓存）
//     */
//    E get(@NotNull Object bizKey, @NotNull Object bizHashKey);

    /**
     * 获取整个 hash key 对应的所有数据（通常对应一个业务 ID）
     * ⚠️ 要求必须返回源数据完整快照，不允许残缺。
     */
    List<E> list(@NotNull Object bizKey);

    /**
     * 获取整个 hash key 对应的所有数据（通常对应一个业务 ID）
     * ⚠️ 要求必须返回源数据完整快照，不允许残缺。
     */
    List<E> list(@Nullable Long tenantId, @NotNull Object bizKey);

    /**
     * 删除整个 key 的 JVM + Redis 缓存（用于强制刷新或业务侧主动清理）
     */
    void evictAllCache(@NotNull String bizKey);

    /**
     * 删除整个 key 的 JVM + Redis 缓存（用于强制刷新或业务侧主动清理）
     */
    void evictAllCache(@Nullable Long tenantId, @NotNull Object bizKey);

    /**
     * 更新某条缓存数据，同时更新 Redis 与 JVM 缓存。
     * 适用于 MQ/监听触发的增量更新。
     */
    void updateCache(@NotNull E data);

    /**
     * 强制刷新整个 key 的缓存（会触发回源）
     */
    void refreshAllCache(@NotNull Object bizKey);

    /**
     * 强制刷新整个 key 的缓存（会触发回源）
     */
    void refreshAllCache(@Nullable Long tenantId, @NotNull Object bizKey);
}
