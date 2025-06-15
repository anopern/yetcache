package lab.anoper.yetcache.example.base.common.cache.agent;

import lab.anoper.yetcache.agent.impl.AbstractMultiHashCacheAgent;
import lab.anoper.yetcache.example.base.common.cache.dto.StockHoldInfoDTO;
import lab.anoper.yetcache.properties.BaseCacheAgentProperties;
import lab.anoper.yetcache.source.IMultiHashCacheSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class StockHoldInfoCacheAgent extends AbstractMultiHashCacheAgent<StockHoldInfoDTO> {
    public StockHoldInfoCacheAgent(@Qualifier("stockHoldInfoCacheAgentProperties") @Autowired BaseCacheAgentProperties properties,
                                   @Qualifier("stockHoldInfoSourceService") @Autowired IMultiHashCacheSourceService<StockHoldInfoDTO> sourceService) {
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
