package com.yetcache.example.properties;

import com.yetcache.properties.BaseCacheAgentProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component(value = "userCacheAgentProperties")
@ConfigurationProperties(prefix = UserCacheAgentProperties.PREFIX)
public class UserCacheAgentProperties extends BaseCacheAgentProperties {
    public static final String PREFIX = BaseCacheAgentProperties.COMMON_CACHE_PREFIX + "user";
}
