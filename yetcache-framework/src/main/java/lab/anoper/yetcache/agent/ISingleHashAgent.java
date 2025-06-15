package lab.anoper.yetcache.agent;

import io.micrometer.core.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 单个 Hash 缓存结构的 Agent 抽象接口，适用于数据规模在 1000 个以内的场景。
 *
 * <p>该 Agent 实现适用于以下场景：
 * <ul>
 *     <li>键集合较小，可一次性拉取全部数据（通常少于1000条）；</li>
 *     <li>以某个固定 HashKey 为主键（如：dict、枚举、配置类数据）；</li>
 *     <li>支持通过 queryAll() 全量回源并设置整个 Hash 缓存；</li>
 *     <li>允许配置本地缓存和 Redis 作为一级/二级缓存结构。</li>
 * </ul>
 *
 * <p>核心特性：
 * <ul>
 *     <li>仅管理一个 HashKey 下的多个 field-value 对；</li>
 *     <li>支持被动缓存查询（get）、手动刷新、自动定时刷新（可选）；</li>
 *     <li>可结合租户信息实现多租户隔离；</li>
 * </ul>
 *
 * @param <E> Hash 缓存中单个 value 的业务对象类型（通常为 DTO/DO）
 */
public interface ISingleHashAgent<E> extends ICacheAgent {
    /**
     * 获取当前租户（默认或上下文租户）下，该 Hash 中的所有缓存对象列表。
     *
     * @return 缓存中所有字段对应的对象列表
     */
    List<E> listAll();

    /**
     * 获取指定租户下，该 Hash 中的所有缓存对象列表。
     *
     * @param tenantId 租户ID（可为 null 表示无租户或默认租户）
     * @return 缓存中所有字段对应的对象列表
     */
    List<E> listAll(@Nullable Long tenantId);

    /**
     * 获取当前租户（默认或上下文租户）下，指定字段对应的缓存对象。
     *
     * @param bizHashKey Redis Hash 中的字段名（如 code、key）
     * @return 对应字段的缓存对象，可能为 null（未命中）
     */
    E get(@NotNull String bizHashKey);

    /**
     * 获取指定租户下，指定字段对应的缓存对象。
     *
     * @param tenantId 租户ID（可为 null 表示无租户或默认租户）
     * @param bizHashKey Redis Hash 中的字段名
     * @return 对应字段的缓存对象，可能为 null（未命中）
     */
    E get(@Nullable Long tenantId, @NotNull String bizHashKey);

    /**
     * 刷新当前租户（默认或上下文租户）下该 Hash 的全部缓存数据，
     * 会从数据源全量加载并更新缓存。
     */
    void refreshAllCache();

    /**
     * 刷新指定租户下该 Hash 的全部缓存数据，
     * 会从数据源全量加载并更新缓存。
     *
     * @param tenantId 租户ID（可为 null 表示无租户或默认租户）
     */
    void refreshAllCache(@Nullable Long tenantId);
}
