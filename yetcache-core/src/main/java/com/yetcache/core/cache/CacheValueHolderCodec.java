package com.yetcache.core.cache;

import cn.hutool.core.util.StrUtil;
import com.yetcache.core.cache.support.CacheValueHolder;
import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
public final class CacheValueHolderCodec implements ValueCodec {
    // 用“同一种算法”的 valueCodec；这里用 Object 保持去泛型
    private final ValueCodec delegate;

    public CacheValueHolderCodec(ValueCodec delegate) {
        this.delegate = delegate;
    }

    @Override
    public String encode(Object holderObj) throws Exception {
        if (holderObj == null) return null;
        return delegate.encode(holderObj);
    }

    @Override
    public CacheValueHolder decode(String json, Type valueType) throws Exception {
        if (StrUtil.isEmpty(json)) {
            return null;
        }

        CacheValueHolder holder = (CacheValueHolder) delegate.decode(json, CacheValueHolder.class);
        if (null != holder && null != holder.getValue()) {
            Object value = ((JacksonValueCodec) delegate)
                    .getObjectMapper()
                    .convertValue(holder.getValue(),
                            ((JacksonValueCodec) delegate).getObjectMapper()
                                    .getTypeFactory()
                                    .constructType(valueType));
            holder.setValue(value);
        }
        return holder;
    }
}
