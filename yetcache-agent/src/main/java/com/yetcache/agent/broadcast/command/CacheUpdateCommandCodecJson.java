package com.yetcache.agent.broadcast.command;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yetcache.core.codec.jackson.JacksonJsonValueCodec;
import com.yetcache.core.codec.JsonValueCodec;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
public final class CacheUpdateCommandCodecJson implements JsonValueCodec {
    // 用“同一种算法”的 valueCodec；这里用 Object 保持去泛型
    private final JsonValueCodec delegate;

    public CacheUpdateCommandCodecJson(JsonValueCodec delegate) {
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
        return ((JacksonJsonValueCodec) delegate)
                .getObjectMapper()
                .convertValue(raw, ((JacksonJsonValueCodec) delegate).getObjectMapper()
                        .getTypeFactory()
                        .constructType(valueType));
    }
}
