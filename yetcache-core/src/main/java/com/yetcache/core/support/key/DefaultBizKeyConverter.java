package com.yetcache.core.support.key;


/**
 * @author walter.yan
 * @since 2025/7/1
 */
public class DefaultBizKeyConverter<K> implements BizKeyConverter<K> {
    @Override
    public String convert(K bizKey) {
        return String.valueOf(bizKey);
    }
}
