package com.yetcache.core.result;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/8/31
 */
@Data
@AllArgsConstructor
public class FreshnessInfo {
    private Freshness freshness;

    public boolean isFresh() {
        return freshness == Freshness.FRESH;
    }

    public static FreshnessInfo fresh() {
        return new FreshnessInfo(Freshness.FRESH);
    }

    public static FreshnessInfo stale() {
        return new FreshnessInfo(Freshness.STALE);
    }
}
