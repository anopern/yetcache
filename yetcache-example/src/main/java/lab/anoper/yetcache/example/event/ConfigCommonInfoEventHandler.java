package lab.anoper.yetcache.example.event;

import lab.anoper.yetcache.example.base.common.cache.agent.ConfigCommonInfoCacheAgent;
import lab.anoper.yetcache.example.domain.entity.ConfigCommonInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
@Component
public class ConfigCommonInfoEventHandler {
    private static final Logger log = LoggerFactory.getLogger(AccAccountUpdateEventHandler.class);

    @Autowired
    private ConfigCommonInfoCacheAgent configCommonInfoCacheAgent;

    @EventListener
    public void onUpdateEvent(ConfigCommonInfo entity) {
        configCommonInfoCacheAgent.refreshAllCache(entity.getTenantId());
    }
}
