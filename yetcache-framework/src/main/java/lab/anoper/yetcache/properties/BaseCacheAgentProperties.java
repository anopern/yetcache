package lab.anoper.yetcache.properties;

import lab.anoper.yetcache.enums.TenantCheckLevel;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/5/21
 */
@Data
public abstract class BaseCacheAgentProperties {

    public static final String COMMON_CACHE_PREFIX = "yetcache.agent.";

    // Redis相关配置，单位秒，默认7天
    private Integer remoteExpireSecs = 3600 * 2;

    // Caffeine相关配置，单位秒，默认1天
    private Integer localExpireSecs = 60 * 5;

    // 空对象过期时间，防止缓存穿透，默认5分钟
    private Integer penetrationProtectTtlSecs = 60;

    // MQ相关配置
    private String mqExchange = "commonCacheAgentCommonExchange";

    // 是否启动时从源头加载热点数据
    private boolean loadFromSourceOnStartup = true;

    // 是否启用本地缓存
    private boolean localCacheEnabled = true;

    // 是否启用Redis缓存
    private boolean redisCacheEnabled = true;

    // 本地缓存最大数量
    private Integer jvmMaxSize = 1000;

    // 启动顺序，数值越大越靠后
    private Integer order = 1;

    // 缓存Agent名称
    private String agentId;

    /**
     * 本地和远程缓存使用的主键（或前缀）：
     * <ul>
     *     <li>在 KV 和 MultiHash 模式下，作为业务缓存 Key 的前缀（如 user:profile）</li>
     *     <li>在 SingleHash 模式下，作为整个 Redis Hash 的唯一 Key（如 dict:currency）</li>
     * </ul>
     * 本字段同时用于远程缓存与本地缓存的 key 构造。
     */
    private String cacheKey;

    /**
     * 租户校验级别（建议使用枚举类型 TenantCheckLevel）：
     * <ul>
     *     <li>NONE：不检查租户</li>
     *     <li>OPTIONAL：若提供租户则校验，否则使用默认租户</li>
     *     <li>REQUIRED：必须提供租户信息，否则抛异常</li>
     * </ul>
     */
    private TenantCheckLevel tenantCheckLevel;

    // 消息最大延迟秒数
    private Integer messageMaxDelaySecs = 10;

//    // 热键缓存key前缀
//    private String hotkeyCacheKeyPrefix;
//
//
//    // 热键缓存 JVM 过期时间，单位秒
//    private Integer hotkeyJvmExpireSecs = 3600 * 24;
//
//    // 热键缓存 Redis 过期时间，单位秒
//    private Integer hotkeyRedisExpireSecs = 3600 * 24 * 2;
//
//    // 热键缓存最大数量
//    private Integer hotkeyJvmMaxSize = 10000;
//

}