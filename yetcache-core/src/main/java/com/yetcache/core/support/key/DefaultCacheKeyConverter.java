package com.yetcache.core.support.key;

import cn.hutool.core.util.StrUtil;

import java.util.function.Supplier;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class DefaultCacheKeyConverter<K> implements CacheKeyConverter<K> {

    private final String keyPrefix;
    private final boolean useTenant;
    private final boolean useHashTag;
    private final Supplier<String> tenantSupplier;

    public DefaultCacheKeyConverter(String keyPrefix,
                                    boolean useTenant,
                                    boolean useHashTag,
                                    Supplier<String> tenantSupplier) {
        this.keyPrefix = keyPrefix;
        this.useTenant = useTenant;
        this.useHashTag = useHashTag;
        this.tenantSupplier = tenantSupplier;
    }

    @Override
    public String convert(K bizKey) {
        if (bizKey == null) {
            throw new IllegalArgumentException("Cache key cannot be null");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(keyPrefix);

        if (useTenant) {
            String tenant = tenantSupplier.get();
            if (StrUtil.isNotBlank(tenant)) {
                sb.append(":").append(tenant);
            }
        }

        sb.append(":");

        String bizKeyStr = String.valueOf(bizKey); // 仅此处做 key.toString
        if (useHashTag) {
            sb.append("{").append(bizKeyStr).append("}");
        } else {
            sb.append(bizKeyStr);
        }

        return sb.toString();
    }
}
