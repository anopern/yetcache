package com.yetcache.core.support.key;

import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
@Data
public abstract class AbstractKeyConverter<K> implements KeyConverter<K> {
    protected final String keyPrefix;
    protected final boolean useHashTag;
}
