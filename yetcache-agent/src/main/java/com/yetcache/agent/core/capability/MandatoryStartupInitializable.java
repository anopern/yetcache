package com.yetcache.agent.core.capability;

import com.yetcache.core.result.CacheAccessResult;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface MandatoryStartupInitializable {
    int getPriority();

    String getComponentName();

    <R extends CacheAccessResult<?>> R initialize();
}
