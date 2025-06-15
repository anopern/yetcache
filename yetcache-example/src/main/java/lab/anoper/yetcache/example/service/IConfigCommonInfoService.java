package lab.anoper.yetcache.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import lab.anoper.yetcache.example.base.common.cache.dto.ConfigCommonInfoDTO;
import lab.anoper.yetcache.example.domain.entity.ConfigCommonInfo;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
public interface IConfigCommonInfoService extends IService<ConfigCommonInfo> {
    List<ConfigCommonInfo> listByTenantId(Long tenantId);

    void updateOrCreate(ConfigCommonInfoDTO dto);
}
