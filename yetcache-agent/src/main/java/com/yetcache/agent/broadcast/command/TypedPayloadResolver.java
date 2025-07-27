package com.yetcache.agent.broadcast.command;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.agent.broadcast.command.playload.data.DynamicHashData;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashCacheAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/27
 */
@Slf4j
public class TypedPayloadResolver {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <K, F, V> DynamicHashData<K, F, V> resolveByAgent(ExecutableCommand cmd,
                                                                    DynamicHashCacheAgent<?, ?, ?> agent) {
        ResolvableType type = ResolvableType.forClass(DynamicHashCacheAgent.class, agent.getClass());
        Class<?> kClass = type.resolveGeneric(0);
        Class<?> fClass = type.resolveGeneric(1);
        Class<?> vClass = type.resolveGeneric(2);

        if (kClass == null || fClass == null || vClass == null) {
            throw new IllegalStateException("无法解析泛型类型");
        }

        JavaType javaType = OBJECT_MAPPER.getTypeFactory()
                .constructParametricType(DynamicHashData.class, kClass, fClass, vClass);

        Object raw = cmd.getPayloadRaw("data");
        return OBJECT_MAPPER.convertValue(raw, javaType);
    }

    public static <K, F, V> Map<String, Object> serialize(K bizKey, Map<F, V> valueMap) {
        DynamicHashData<K, F, V> data = new DynamicHashData<>(bizKey, valueMap);
        Map<String, Object> playload = new HashMap<>();
        playload.put("data", data);
        return playload;
    }
}
