package lab.anoper.yetcache.enums;

import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/5/25
 */
@Getter
public enum CacheType {
    JVM_CACHE("JVM Cache"),
    REDIS_CACHE("Redis Cache"),
    ;

    private final String label;

    CacheType(String label) {
        this.label = label;
    }
}
