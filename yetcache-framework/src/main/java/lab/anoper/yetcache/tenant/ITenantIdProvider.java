package lab.anoper.yetcache.tenant;

import java.util.Set;

/**
 * @author walter.yan
 * @since 2025/5/24
 */
public interface ITenantIdProvider {
    Set<Long> listAllTenantIds();
}
