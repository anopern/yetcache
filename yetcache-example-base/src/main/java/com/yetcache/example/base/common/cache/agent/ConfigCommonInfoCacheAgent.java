package com.yetcache.example.base.common.cache.agent;

import com.yetcache.properties.BaseCacheAgentProperties;
import com.yetcache.agent.impl.AbstractSingleHashCacheAgent;
import com.yetcache.example.base.common.cache.dto.ConfigCommonInfoDTO;
import com.yetcache.source.ISingleHashCacheSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
@Component
public class ConfigCommonInfoCacheAgent extends AbstractSingleHashCacheAgent<ConfigCommonInfoDTO> {

    public ConfigCommonInfoCacheAgent(@Qualifier("configCommonInfoCacheAgentProperties") @Autowired BaseCacheAgentProperties properties,
                                      @Qualifier("configCommonInfoSourceService") @Autowired ISingleHashCacheSourceService<ConfigCommonInfoDTO> sourceService) {
        super(properties, sourceService);
    }

    @Override
    public boolean isTenantScoped() {
        return true;
    }

    @Override
    protected String getHashKey(ConfigCommonInfoDTO dto) {
        return dto.getCode();
    }
}
