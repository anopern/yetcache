package com.yetcache.core.support.key;


/**
 * @author walter.yan
 * @since 2025/7/1
 */
public class DefaultBizKeyPartConverter<K> implements BizKeyPartConverter<K> {
    @Override
    public String convert(K bizKey) {
        return String.valueOf(bizKey);
    }
}
