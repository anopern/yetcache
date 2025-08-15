package com.yetcache.agent.broadcast.command;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yetcache.core.cache.JacksonValueStringCodec;
import com.yetcache.core.codec.ValueStringCodec;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
public final class CacheUpdateCommandStringCodec implements ValueStringCodec {
    // 用“同一种算法”的 valueCodec；这里用 Object 保持去泛型
    private final ValueStringCodec delegate;

    public CacheUpdateCommandStringCodec(ValueStringCodec delegate) {
        this.delegate = delegate;
    }

    @Override
    public String encode(Object holderObj) throws Exception {
        if (holderObj == null) return null;
        return delegate.encode(holderObj);
    }

    @Override
    public CacheUpdateCommand decode(String json, Type valueType) throws Exception {
        if (StrUtil.isEmpty(json)) {
            return null;
        }

        CacheUpdateCommand cmd = (CacheUpdateCommand) delegate.decode(json, CacheUpdateCommand.class);
        if (null != cmd && null != cmd.getPayload()) {
            Map<Object, Object> bizKeyValueMap = cmd.getPayload().getBizKeyValueMap();
            if (CollUtil.isNotEmpty(bizKeyValueMap)) {
                for (Map.Entry<Object, Object> entry : bizKeyValueMap.entrySet()) {
                    entry.setValue(decode(entry.getValue(), valueType));
                }
            }
            Map<Object, Object> bizFieldValueMap = cmd.getPayload().getBizFieldValueMap();
            if (CollUtil.isNotEmpty(bizFieldValueMap)) {
                for (Map.Entry<Object, Object> entry : bizFieldValueMap.entrySet()) {
                    entry.setValue(decode(entry.getValue(), valueType));
                }
            }
        }
        return cmd;
    }

    private Object decode(Object raw, Type valueType) {
        return ((JacksonValueStringCodec) delegate)
                .getObjectMapper()
                .convertValue(raw, ((JacksonValueStringCodec) delegate).getObjectMapper()
                        .getTypeFactory()
                        .constructType(valueType));
    }
}
