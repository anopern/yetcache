package com.yetcache.core.support.key;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public class DefaultKeyConverter implements KeyConverter {
    protected final String keyPrefix;
    protected final boolean useHashTag;

    public DefaultKeyConverter(String keyPrefix, boolean useHashTag) {
        this.keyPrefix = keyPrefix;
        this.useHashTag = useHashTag;
    }

    @Override
    public String convert(Object bizKey) {
        StringBuilder sb = new StringBuilder(keyPrefix);
        if (null != bizKey) {
            String bizKeyStr = String.valueOf(bizKey);
            sb.append(":");
            if (useHashTag) {
                sb.append("{").append(bizKeyStr).append("}");
            } else {
                sb.append(bizKeyStr);
            }
        }

        return sb.toString();
    }
}
