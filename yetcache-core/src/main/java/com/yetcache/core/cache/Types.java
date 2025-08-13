package com.yetcache.core.cache;

import com.yetcache.core.cache.support.CacheValueHolder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/13
 */
public final class Types {
    public static ParameterizedType holderType(Type valueType) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{valueType};
            }

            @Override
            public Type getRawType() {
                return CacheValueHolder.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
}
