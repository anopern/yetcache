package lab.anoper.yetcache.example.properties;

import lab.anoper.yetcache.properties.BaseCacheAgentProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component(value = "stockHoldInfoCacheAgentProperties")
@ConfigurationProperties(prefix = StockHoldInfoCacheAgentProperties.PREFIX)
public class StockHoldInfoCacheAgentProperties extends BaseCacheAgentProperties {
    public static final String PREFIX = BaseCacheAgentProperties.COMMON_CACHE_PREFIX + "stock-hold-info";
}
