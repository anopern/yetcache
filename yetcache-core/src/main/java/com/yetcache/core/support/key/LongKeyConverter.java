package com.yetcache.core.support.key;

/**
 * @author walter.yan
 * @since 2025/8/16
 */
public class LongKeyConverter extends AbstractKeyConverter {
    public LongKeyConverter(String keyPrefix, boolean useHashTag) {
        super(keyPrefix, useHashTag);
    }

    @Override
    public <T> String convert(T bizKey) {
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

    @Override
    @SuppressWarnings("unchecked")
    public <T> T revert(String key) {
        if (key == null) return null;

        String s = key;

        // 1) 去前缀
        if (keyPrefix != null && !keyPrefix.isEmpty()) {
            if (!s.startsWith(keyPrefix)) {
                throw new IllegalArgumentException("Key does not start with expected prefix: " + keyPrefix + ", key=" + key);
            }
            s = s.substring(keyPrefix.length());
        }

        // 2) 去分隔符
        if (s.startsWith(":")) {
            s = s.substring(1);
        }
        if (s.isEmpty()) return null;

        // 3) 处理 hashtag
        if (useHashTag) {
            int l = s.indexOf('{');
            int r = (l >= 0) ? s.indexOf('}', l + 1) : -1;
            if (l >= 0 && r > l) {
                s = s.substring(l + 1, r);
            }
        }

        s = s.trim();
        if (s.isEmpty()) return null;

        // 4) 解析为 Long（LongKeyConverter 的契约）
        try {
            Long v = Long.valueOf(s);
            return (T) v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Long bizKey in key: " + key, e);
        }
    }
}
