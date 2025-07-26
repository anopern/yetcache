//package com.yetcache.example.cache.agent;
//
//import com.yetcache.agent.core.structure.flathash.FlatHashCacheLoader;
//import com.yetcache.agent.core.structure.flathash.AbstractListableFlatHashAgent;
//import com.yetcache.agent.result.FlatHashCacheAgentResult;
//import com.yetcache.core.config.flathash.FlatHashCacheConfig;
//import com.yetcache.core.support.field.FieldConverter;
//import com.yetcache.core.support.field.TypeFieldConverter;
//import com.yetcache.example.entity.ConfigCommonInfo;
//import io.micrometer.core.instrument.MeterRegistry;
//
//
///**
// * @author walter.yan
// * @since 2025/7/12
// */
//public class ConfigCommonInfoCacheAgent extends AbstractListableFlatHashAgent<String, ConfigCommonInfo> {
//
//    public ConfigCommonInfoCacheAgent(String cacheAgentName,
//                                      FlatHashCacheConfig config,
//                                      FlatHashCacheLoader<String, ConfigCommonInfo> cacheLoader,
//                                      MeterRegistry registry) {
//        super(cacheAgentName, config, cacheLoader, registry);
//    }
//
//    @Override
//    protected FieldConverter<String> getFieldConverter() {
//        return new TypeFieldConverter<>(String.class);
//    }
//}
