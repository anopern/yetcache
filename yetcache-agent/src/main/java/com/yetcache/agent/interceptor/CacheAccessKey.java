package com.yetcache.agent.interceptor;

import lombok.Getter;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
@Getter
public class CacheAccessKey {
    private final Object bizKey;
    private final Object bizField;

    public CacheAccessKey(@NotNull Object bizKey, @Nullable Object bizField) {
        this.bizKey = bizKey;
        this.bizField = bizField;
    }

    public static <K, F> CacheAccessKey batch(Map<K, List<F>> keyMap) {
        int totalFields = keyMap.values().stream().mapToInt(List::size).sum();
        String summaryKey = "BATCH-" + keyMap.size() + "keys-" + totalFields + "fields";
        return new CacheAccessKey(summaryKey, null);
    }

    @Override
    public String toString() {
        if (null != bizField) {
            return bizKey + "_" + bizField;
        }
        return bizKey.toString();
    }
}
