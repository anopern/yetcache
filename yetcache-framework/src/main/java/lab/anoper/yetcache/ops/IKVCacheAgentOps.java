package lab.anoper.yetcache.ops;

/**
 * KV 类型缓存的统一运维接口（如：一级缓存、对象缓存、简单配置类缓存）。
 * 支持通过 agentName 路由调用实际实现类（结合注册中心），适合调度/运维平台使用。
 * <p>
 *
 * @author walter.yan
 * @since 2025/5/25
 */
public interface IKVCacheAgentOps {

    /**
     * 查看一个或多个业务 key 的缓存值
     *
     * @param agentName 缓存代理的名称（注册标识）
     * @param tenantId  租户ID（可为空）
     * @param bizKeys   业务 key（支持多个）
     * @return JSON 格式的 key-value 结果
     */
    String view(String agentName, Long tenantId, String... bizKeys);

    /**
     * 刷新一个或多个业务 key 的缓存（重新加载）
     *
     * @param agentName 缓存代理名称
     * @param tenantId  租户ID
     * @param bizKeys   要刷新的 key
     */
    void refresh(String agentName, Long tenantId, String... bizKeys);

    /**
     * 使一个或多个业务 key 失效（缓存失效）
     *
     * @param agentName 缓存代理名称
     * @param tenantId  租户ID
     * @param bizKeys   要驱逐的 key
     */
    void evict(String agentName, Long tenantId, String... bizKeys);
}