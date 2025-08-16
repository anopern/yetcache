package com.yetcache.core.codec;

import com.yetcache.core.cache.support.CacheValueHolder;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
public class CacheValueHolderReifier extends AbstractWrapperReifier<CacheValueHolder> {
    private final TypeRefRegistry typeRefMap = new TypeRefRegistry();

    public CacheValueHolderReifier(JsonTypeConverter jsonTypeConverter) {
        super(jsonTypeConverter);
    }

    @Override
    public Class<CacheValueHolder> targetType() {
        return CacheValueHolder.class;
    }

    @Override
    public CacheValueHolder reify(CacheValueHolder wrapper, ReifyContext ctx) throws Exception {
        if (null != wrapper) {
            wrapper.setValue(reifySlot(wrapper.getValue(), ctx.get("value")));
        }
        return null;
    }

}
