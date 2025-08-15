package com.yetcache.core.codec;

import com.yetcache.core.cache.support.CacheValueHolder;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
public class CacheValueHolderReifier implements WrapperReifier<CacheValueHolder> {
    @Override
    public Class<CacheValueHolder> targetType() {
        return CacheValueHolder.class;
    }

    @Override
    public CacheValueHolder reify(CacheValueHolder holder, TypeRef<?> valueType, JsonTypeConverter converter) throws Exception {
        if (null == holder) {
            return null;
        }
        Object raw = holder.getValue();
        if (!valueType.isInstance(raw)) {
            Object typed = converter.convert(raw, valueType.getType());
            holder.setValue(typed);
        }
        return holder;
    }
}
