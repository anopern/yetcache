package com.yetcache.core.cache;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
public class CacheValueHolderCodec  implements ValueCodec {
    private final ValueCodec delegate;

    public CacheValueHolderCodec(ValueCodec delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] encode(Object holder, Type valueType) throws Exception {
        Type holderType = Types.holderType(valueType);
        return delegate.encode(holder, holderType);
    }

    @Override
    public Object decode(byte[] bytes, Type valueType) throws Exception {
        Type holderType = Types.holderType(valueType);
        return delegate.decode(bytes, holderType);
    }
}