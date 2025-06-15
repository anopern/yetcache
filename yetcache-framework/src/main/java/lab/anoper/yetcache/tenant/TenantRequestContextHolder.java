package lab.anoper.yetcache.tenant;


public class TenantRequestContextHolder {
    private static final ThreadLocal<Long> tenantId = new ThreadLocal<>();

    public static Long getCurTenantId() {
        return tenantId.get();
    }

    public static void setCurTenantId(Long tenantId) {
        TenantRequestContextHolder.tenantId.set(tenantId);
    }
}
