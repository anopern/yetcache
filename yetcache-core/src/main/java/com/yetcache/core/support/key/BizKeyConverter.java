package com.yetcache.core.support.key;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
public interface BizKeyConverter<K> {
    String convert(K bizKey);
}
