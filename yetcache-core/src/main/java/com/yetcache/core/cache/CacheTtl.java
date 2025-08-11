package com.yetcache.core.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/11
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CacheTtl {
    private Long localLogicSecs;
    private Long localPhysicalSecs;
    private Long remoteLogicSecs;
    private Long remotePhysicalSecs;
}
