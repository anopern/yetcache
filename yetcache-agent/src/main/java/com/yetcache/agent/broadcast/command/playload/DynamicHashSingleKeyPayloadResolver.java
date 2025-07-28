//package com.yetcache.agent.broadcast.command.playload;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yetcache.agent.broadcast.command.descriptor.CommandDescriptor;
//import com.yetcache.agent.broadcast.command.playload.data.DynamicHashData;
//import com.yetcache.agent.core.CacheAgentMethod;
//import com.yetcache.agent.core.CacheStructureType;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
///**
// * @author walter.yan
// * @since 2025/7/26
// */
//public class DynamicHashSingleKeyPayloadResolver<K, F, V> implements PayloadResolver<DynamicHashData<K, F, V>> {
//    private final Class<K> keyClass;
//    private final Class<F> fieldClass;
//    private final Class<V> valueClass;
//    private final ObjectMapper mapper;
//
//    public DynamicHashSingleKeyPayloadResolver(
//            Class<K> keyClass,
//            Class<F> fieldClass,
//            Class<V> valueClass,
//            ObjectMapper mapper
//    ) {
//        this.keyClass = keyClass;
//        this.fieldClass = fieldClass;
//        this.valueClass = valueClass;
//        this.mapper = mapper;
//    }
//
//    @Override
//    public boolean supports(CommandDescriptor descriptor) {
//        return descriptor.getStructureType() == CacheStructureType.DYNAMIC_HASH
//                && descriptor.getAction() == CacheAgentMethod.BATCH_REFRESH;
//    }
//
//    @Override
//    public DynamicHashData<K, F, V> resolve(Map<String, Object> payload) {
//        K key = mapper.convertValue(payload.get("bizKey"), keyClass);
//
//        Map<String, Object> rawData = mapper.convertValue(
//                payload.get("data"),
//                new TypeReference<>() {
//                }
//        );
//
//        Map<F, V> fieldValueMap = new LinkedHashMap<>();
//        for (Map.Entry<String, Object> entry : rawData.entrySet()) {
//            F field = mapper.convertValue(entry.getKey(), fieldClass);
//            V value = mapper.convertValue(entry.getValue(), valueClass);
//            fieldValueMap.put(field, value);
//        }
//
//        return new DynamicHashData<>(key, fieldValueMap);
//    }
//
//    @Override
//    public Map<String, Object> serialize(DynamicHashData<K, F, V> value) {
//        return Map.of(
//                "bizKey", value.getBizKey(),
//                "data", value.getValueMap()
//        );
//    }
//}
//
