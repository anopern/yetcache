package lab.anoper.yetcache.example.base.common.cache.agent;

import lab.anoper.yetcache.agent.impl.AbstractSingleHashCacheAgent;
import lab.anoper.yetcache.example.base.common.cache.dto.ConfigCommonInfoDTO;
import lab.anoper.yetcache.properties.BaseCacheAgentProperties;
import lab.anoper.yetcache.source.ISingleHashCacheSourceService;
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
    protected String getBizHashKey(ConfigCommonInfoDTO dto) {
        return dto.getCode();
    }
}
