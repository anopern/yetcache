package lab.anoper.yetcache.agent;

/**
 * @author walter.yan
 * @since 2025/5/24
 */
public interface ICacheAgent {
    String getId();
    boolean isTenantScoped();
}
