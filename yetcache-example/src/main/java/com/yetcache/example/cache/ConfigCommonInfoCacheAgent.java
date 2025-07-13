package com.yetcache.example.cache;

import com.yetcache.agent.FlatHashCacheLoader;
import com.yetcache.agent.flathash.AbstractListableFlatHashAgent;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.field.TypeFieldConverter;
import com.yetcache.example.entity.ConfigCommonInfo;
import io.micrometer.core.instrument.MeterRegistry;


/**
 * @author walter.yan
 * @since 2025/7/12
 */
public class ConfigCommonInfoCacheAgent extends AbstractListableFlatHashAgent<String, ConfigCommonInfo> {

    public ConfigCommonInfoCacheAgent(String cacheAgentName,
                                      MultiTierFlatHashCacheConfig config,
                                      FlatHashCacheLoader<String, ConfigCommonInfo> cacheLoader,
                                      MeterRegistry registry) {
        super(cacheAgentName, config, cacheLoader, registry);
    }

    @Override
    protected FieldConverter<String> getFieldConverter() {
        return new TypeFieldConverter<>(String.class);
    }
}
