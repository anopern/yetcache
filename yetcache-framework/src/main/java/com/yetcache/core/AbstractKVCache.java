package com.yetcache.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public abstract class AbstractKVCache<K, V> implements KVCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractKVCache.class);

}
