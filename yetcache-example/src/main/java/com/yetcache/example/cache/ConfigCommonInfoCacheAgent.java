package com.yetcache.example.cache;

import com.yetcache.agent.AbstractConfigCacheAgent;
import com.yetcache.core.cache.flathash.MultiTierFlatHashCache;
import com.yetcache.core.cache.flathash.MultiTierFlatHashCacheBehaviorEnhancer;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;
import com.yetcache.core.support.field.FieldConverter;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
public class ConfigCommonInfoCacheAgent extends AbstractConfigCacheAgent<String,  String> {

    protected ConfigCommonInfoCacheAgent(MultiTierFlatHashCacheConfig config, List<MultiTierFlatHashCacheBehaviorEnhancer<String, String>> multiTierFlatHashCacheBehaviorEnhancers) {
        super(config, multiTierFlatHashCacheBehaviorEnhancers);
    }

    @Override
    protected MultiTierFlatHashCache<String, String> buildCore(MultiTierFlatHashCacheConfig config) {
        return null;
    }

    @Override
    protected String getName() {
        return null;
    }

    @Override
    protected FieldConverter<String> getFieldConverter() {
        return null;
    }
}
