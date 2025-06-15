package lab.anoper.yetcache.agent;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author walter.yan
 * @since 2025/5/21
 */
public interface IMultiHashCacheAgent<E> extends ICacheAgent {

    /**
     * 获取整个 hash key 对应的所有数据（通常对应一个业务 ID）
     * ⚠️ 要求必须返回源数据完整快照，不允许残缺。
     */
    List<E> list(@NotNull String bizKey);

    /**
     * 获取整个 hash key 对应的所有数据（通常对应一个业务 ID）
     * ⚠️ 要求必须返回源数据完整快照，不允许残缺。
     */
    List<E> list(@Nullable Long tenantId, @NotNull String bizKey);

    /**
     * 删除整个 key 的 JVM + Redis 缓存（用于强制刷新或业务侧主动清理）
     */
    void evictAllCache(@NotNull String bizKey);

    /**
     * 删除整个 key 的 JVM + Redis 缓存（用于强制刷新或业务侧主动清理）
     */
    void evictAllCache(@Nullable Long tenantId, @NotNull String bizKey);

    /**
     * 强制刷新整个 key 的缓存（会触发回源）
     */
    void refreshAllCache(@NotNull String bizKey);

    /**
     * 强制刷新整个 key 的缓存（会触发回源）
     */
    void refreshAllCache(@Nullable Long tenantId, @NotNull String bizKey);
}
