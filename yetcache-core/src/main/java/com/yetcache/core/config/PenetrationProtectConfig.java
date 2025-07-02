package com.yetcache.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
@ConfigurationPropertiesBinding
public class PenetrationProtectConfig {
    protected String prefix = "__pt__";
    protected Boolean enabled;
    protected Long ttlSecs;
    protected Long maxSize;
}
