package com.yetcache.enums;

import lombok.Getter;

/**
 * @author walter.yan
 * @since 2025/5/25
 */
@Getter
public enum CacheType {
    LOCAL_CACHE("Local Cache"),
    REMOTE_CACHE("Remote Cache"),
    BOTH("Both local and remote Cache"),
    ;

    private final String desc;

    CacheType(String desc) {
        this.desc = desc;
    }
}
