package com.yetcache.core.support.key;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public interface KeyConverter <K>{
      String convert(K bizKey);

    K revert(String key);
}
