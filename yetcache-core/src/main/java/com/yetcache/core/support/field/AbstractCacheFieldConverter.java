package com.yetcache.core.support.field;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public class AbstractCacheFieldConverter<K> implements CacheFieldConverter<K> {
    @Override
    public String convert(K bizField) {
        if (bizField == null) {
            throw new IllegalArgumentException("FieldKey is null in CacheFieldConverter");
        }
        return String.valueOf(bizField);
    }

    @Override
    @SuppressWarnings("unchecked")
    public K reverse(String fieldStr) {
        // 默认无法还原，建议子类覆盖（如 Enum 支持 name() <-> valueOf）
        throw new UnsupportedOperationException("Reverse conversion not supported by default");
    }
}
