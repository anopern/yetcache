package com.yetcache.example.properties;

import com.yetcache.properties.BaseCacheAgentProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component(value = "configCommonInfoCacheAgentProperties")
@ConfigurationProperties(prefix = ConfigCommonInfoCacheAgentProperties.PREFIX)
public class ConfigCommonInfoCacheAgentProperties extends BaseCacheAgentProperties {
    public static final String PREFIX = BaseCacheAgentProperties.COMMON_CACHE_PREFIX + "config-common-info";
}
