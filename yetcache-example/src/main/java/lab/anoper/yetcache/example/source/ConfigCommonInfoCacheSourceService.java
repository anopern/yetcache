package lab.anoper.yetcache.example.source;

import lab.anoper.yetcache.example.assember.ConfigCommonInfoConverter;
import lab.anoper.yetcache.example.base.common.cache.dto.ConfigCommonInfoDTO;
import lab.anoper.yetcache.example.event.AccAccountUpdateEventHandler;
import lab.anoper.yetcache.example.service.IConfigCommonInfoService;
import lab.anoper.yetcache.source.impl.AbstractSingleHashCacheSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
@Component("configCommonInfoSourceService")
public class ConfigCommonInfoCacheSourceService extends AbstractSingleHashCacheSourceService<ConfigCommonInfoDTO> {
    private static final Logger log = LoggerFactory.getLogger(AccAccountUpdateEventHandler.class);

    @Autowired
    private IConfigCommonInfoService configCommonInfoService;

    @Override
    public List<ConfigCommonInfoDTO> queryAll(Long tenantId) {
        if (null == tenantId) {
            log.error("租户ID不能为空！");
            return null;
        }
        return ConfigCommonInfoConverter.INSTANCE.doListToDTOList(configCommonInfoService.listByTenantId(tenantId));
    }
}
