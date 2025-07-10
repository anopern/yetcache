package com.yetcache.core.cache.flathash;

import com.yetcache.core.metrics.HitTier;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/9
 */
@Data
public class FlatHashAccessTrace {
    protected HitTier hitTier;
}
