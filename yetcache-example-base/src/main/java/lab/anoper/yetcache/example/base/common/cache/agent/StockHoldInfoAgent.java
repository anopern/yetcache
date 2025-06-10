package lab.anoper.yetcache.example.base.common.cache.agent;

import lab.anoper.yetcache.agent.impl.AbstractHashCacheAgent;
import lab.anoper.yetcache.example.base.common.cache.dto.StockHoldInfoDTO;
import lab.anoper.yetcache.properties.BaseCacheAgentProperties;
import lab.anoper.yetcache.source.IHashCacheSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class StockHoldInfoAgent extends AbstractHashCacheAgent<StockHoldInfoDTO> {
    public StockHoldInfoAgent(@Qualifier("stockHoldInfoCacheAgentProperties") @Autowired BaseCacheAgentProperties properties,
                              @Qualifier("stockHoldInfoSourceService") @Autowired IHashCacheSourceService<StockHoldInfoDTO> sourceService) {
        super(properties, sourceService);
    }

    @Override
    public boolean isTenantScoped() {
        return false;
    }

    @Override
    protected String getBizKey(StockHoldInfoDTO dto) {
        return dto.getFundAccount();
    }

    @Override
    protected String getHashKey(StockHoldInfoDTO dto) {
        return String.valueOf(dto.getId());
    }
}
