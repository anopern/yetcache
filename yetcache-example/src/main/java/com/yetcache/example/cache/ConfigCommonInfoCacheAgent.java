package com.yetcache.example.cache;

import com.yetcache.agent.AbstractConfigCacheAgent;
import com.yetcache.core.cache.flathash.FlatHashCacheLoader;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.field.TypeFieldConverter;
import com.yetcache.example.entity.ConfigCommonInfo;
import com.yetcache.example.enums.EnumCaches;
import io.micrometer.core.instrument.MeterRegistry;


/**
 * @author walter.yan
 * @since 2025/7/12
 */
public class ConfigCommonInfoCacheAgent extends AbstractConfigCacheAgent<String, ConfigCommonInfo> {

    public ConfigCommonInfoCacheAgent(MultiTierFlatHashCacheConfig config,
                                      FlatHashCacheLoader<String, ConfigCommonInfo> cacheLoader,
                                      MeterRegistry meterRegistry) {
        super(config, cacheLoader, meterRegistry);
    }

    @Override
    public String getName() {
        return EnumCaches.CONFIG_COMMON_INFO_CACHE.getName();
    }

    @Override
    protected FieldConverter<String> getFieldConverter() {
        return new TypeFieldConverter<>(String.class);
    }
}
