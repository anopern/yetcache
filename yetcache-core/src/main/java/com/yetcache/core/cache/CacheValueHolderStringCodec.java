package com.yetcache.core.cache;

import cn.hutool.core.util.StrUtil;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.codec.ValueStringCodec;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
public final class CacheValueHolderStringCodec implements ValueStringCodec {
    // 用“同一种算法”的 valueCodec；这里用 Object 保持去泛型
    private final ValueStringCodec delegate;

    public CacheValueHolderStringCodec(ValueStringCodec delegate) {
        this.delegate = delegate;
    }

    @Override
    public String encode(Object holderObj) throws Exception {
        if (holderObj == null) {
            return null;
        }
        return delegate.encode(holderObj);
    }

    @Override
    public CacheValueHolder decode(String json, Type valueType) throws Exception {
        if (StrUtil.isEmpty(json)) {
            return null;
        }

        CacheValueHolder holder = (CacheValueHolder) delegate.decode(json, CacheValueHolder.class);
        if (null != holder && null != holder.getValue()) {
            Object value = delegate.convert(holder.getValue(), valueType);
            holder.setValue(value);
        }
        return holder;
    }

    @Override
    public Object convert(Object obj, Type targetType) throws Exception {
        return null;
    }
}
