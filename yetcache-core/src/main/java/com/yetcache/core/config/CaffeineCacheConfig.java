package com.yetcache.core.config;

import com.yetcache.core.config.BaseCacheConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationPropertiesBinding
public class CaffeineCacheConfig extends BaseCacheConfig {
    protected Integer maxSize;
}
