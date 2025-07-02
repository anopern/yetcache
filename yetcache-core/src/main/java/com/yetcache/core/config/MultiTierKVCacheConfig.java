package com.yetcache.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationPropertiesBinding
public class MultiTierKVCacheConfig extends BaseMultiTierCacheConfig {
    protected String keyPrefix;
}
