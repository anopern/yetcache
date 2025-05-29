package lab.anoper.yetcache.utils;

import lab.anoper.yetcache.enums.TenantCheckLevel;

/**
 * @author walter.yan
 * @since 2025/5/24
 */
public class TenantIdUtils {
    /**
     * 获取当前租户ID
     * <p>
     * 此方法根据当前的缓存类型和租户检查级别来确定如何处理租户ID
     * 它首先获取当前的缓存类型，然后根据缓存类型的租户检查级别
     * 来决定是否需要抛出异常、使用默认租户ID还是将租户ID设置为null
     *
     * @return 当前租户ID，可能根据租户检查逻辑返回实际租户ID、默认租户ID或null
     * @throws IllegalArgumentException 如果租户检查级别为MUST且未提供租户ID时抛出
     * @throws IllegalArgumentException 如果不支持当前的租户检查级别时抛出
     */
    public static Long getCurTenantId(TenantCheckLevel tenantCheckLevel) {
        // 尝试获取当前请求的租户ID
        Long tenantId = TenantRequestContextHolder.getTenantId();

        // 根据当前缓存类型的租户检查级别处理租户ID
        if (tenantCheckLevel == TenantCheckLevel.MUST) {
            // 如果租户检查级别为MUST且未提供租户ID，则抛出异常
            if (null == tenantId) {
                throw new IllegalArgumentException("当前上下文租户ID为空，但是");
            }
        } else if (tenantCheckLevel == TenantCheckLevel.OPTIONAL) {
            // 如果租户检查级别为OPTIONAL且未提供租户ID，则使用默认租户ID
            if (null == tenantId) {
                tenantId = TenantConstants.DEFAULT_TENANT_ID;
            }
        } else if (tenantCheckLevel == TenantCheckLevel.NONE) {
            // 如果租户检查级别为NOT，则将租户ID设置为null
            tenantId = null;
        } else {
            // 如果不支持当前的租户检查级别，则抛出异常
            throw new IllegalArgumentException("暂时未支持其他租户校验级别");
        }

        // 返回处理后的租户ID
        return tenantId;
    }
}
