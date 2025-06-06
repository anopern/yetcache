package lab.anoper.yetcache.properties;

import lab.anoper.yetcache.enums.TenantCheckLevel;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/5/21
 */
@Data
public abstract class BaseCacheAgentProperties {

    public static final String COMMON_CACHE_PREFIX = "common.cache-agent.";

    // Redis相关配置，单位秒，默认7天
    private Integer redisExpireSecs = 3600 * 2;

    // Caffeine相关配置，单位秒，默认1天
    private Integer caffeineExpireSecs = 60 * 5;

    // 空对象过期时间，防止缓存穿透，默认5分钟
    private Integer emptyObjExpireSecs = 60;

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
    private String id;

    // Redis key前缀
    private String keyPrefix;

    /**
     * 租户检查级别
     * 建议改成枚举类型 TenantCheckLevel
     */
    private TenantCheckLevel tenantCheckLevel;

    // 热键缓存key前缀
    private String hotkeyCacheKeyPrefix;

    // 热键缓存 JVM 过期时间，单位秒
    private Integer hotkeyJvmExpireSecs = 3600 * 24;

    // 热键缓存 Redis 过期时间，单位秒
    private Integer hotkeyRedisExpireSecs = 3600 * 24 * 2;

    // 热键缓存最大数量
    private Integer hotkeyJvmMaxSize = 10000;

    // 消息最大延迟秒数
    private Integer messageMaxDelaySecs = 10;
}