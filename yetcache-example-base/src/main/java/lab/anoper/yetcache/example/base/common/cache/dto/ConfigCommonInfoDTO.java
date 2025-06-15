package lab.anoper.yetcache.example.base.common.cache.dto;

import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/15
 */
@Data
public class ConfigCommonInfoDTO {
    private Long id;
    private Long tenantId;
    private String code;
    private String value;
}
