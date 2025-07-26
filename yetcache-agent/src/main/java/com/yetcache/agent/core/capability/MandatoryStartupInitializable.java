package com.yetcache.agent.core.capability;

import com.yetcache.core.result.Result;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface MandatoryStartupInitializable {
    int getPriority();

    String getComponentName();

    <R extends Result<?>> R initialize();
}
