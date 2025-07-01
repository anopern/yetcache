package com.yetcache.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

/**
 * @author walter.yan
 * @since 2025/6/27
 */

@Data
@ConfigurationPropertiesBinding
public class BaseCacheConfig {
    protected Long ttlSecs;
    protected Double ttlRandomPercent;
    protected PenetrationProtectConfig penetrationProtect;
}
