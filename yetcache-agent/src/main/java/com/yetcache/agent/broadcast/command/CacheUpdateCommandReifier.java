//package com.yetcache.agent.broadcast.command;
//
//import com.yetcache.agent.broadcast.command.playload.HashPlayload;
//import com.yetcache.agent.broadcast.command.playload.KvPlayload;
//import com.yetcache.core.codec.*;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.Optional;
//
///**
// * @author walter.yan
// * @since 2025/8/15
// */
//@Slf4j
//public class CacheUpdateCommandReifier extends AbstractWrapperReifier<CacheUpdateCommand> {
//    private final TypeRefRegistry typeRefRegistry;
//
//    public CacheUpdateCommandReifier(TypeRefRegistry typeRefRegistry, JsonTypeConverter jsonTypeConverter) {
//        super(jsonTypeConverter);
//        this.typeRefRegistry = typeRefRegistry;
//    }
//
//    @Override
//    public Class<CacheUpdateCommand> targetType() {
//        return CacheUpdateCommand.class;
//    }
//
//    @Override
//    public CacheUpdateCommand reify(CacheUpdateCommand wrapper, ReifyContext ctx) throws Exception {
//        if (null == wrapper || null == wrapper.getDescriptor() || null == wrapper.getPayload()) {
//            return wrapper;
//        }
//        CommandDescriptor descriptor = wrapper.getDescriptor();
//        Optional<CacheShape> cacheShape = CacheShape.fromName(descriptor.getShape());
//        if (!cacheShape.isPresent()) {
//            log.debug("[YetCache] No cache shape for: {}", descriptor.getShape());
//            return wrapper;
//        }
//        if (cacheShape.get() == CacheShape.KV) {
//            KvPlayload payload = (KvPlayload) wrapper.getPayload();
//            String keyTypeId = payload.getKeyTypeId();
//            String valueTypeId = payload.getValueTypeId();
//            Optional<TypeRef<?>> bizKeyTypeRefOpt = typeRefRegistry.get(keyTypeId);
//            Optional<TypeRef<?>> bizValTypeRefOpt = typeRefRegistry.get(valueTypeId);
//            if (!bizKeyTypeRefOpt.isPresent()) {
//                log.error("[YetCache] No type ref for: {}", descriptor.getKeyTypeId());
//                return wrapper;
//            }
//            if (!bizValTypeRefOpt.isPresent()) {
//                log.error("[YetCache] No type ref for: {}", descriptor.getValueTypeId());
//                return wrapper;
//            }
//            for (KvPlayload.KeyValue kv : payload.getKeyValues()) {
//                Object bizKey = reifySlot(kv.getKey(), bizKeyTypeRefOpt.get());
//                Object value = reifySlot(kv.getValue(), bizValTypeRefOpt.get());
//                kv.setKey(bizKey);
//                kv.setValue(value);
//            }
//        } else if (cacheShape.get() == CacheShape.HASH) {
//            HashPlayload payload = (HashPlayload) wrapper.getPayload();
//            String keyTypeId = payload.getKeyTypeId();
//            String fieldTypeId = payload.getFieldTypeId();
//            String valueTypeId = payload.getValueTypeId();
//            Optional<TypeRef<?>> bizKeyTypeRefOpt = typeRefRegistry.get(keyTypeId);
//            Optional<TypeRef<?>> bizFieldTypeRefOpt = typeRefRegistry.get(fieldTypeId);
//            Optional<TypeRef<?>> bizValTypeRefOpt = typeRefRegistry.get(valueTypeId);
//            if (!bizKeyTypeRefOpt.isPresent()) {
//                log.error("[YetCache] No type ref for: {}", descriptor.getKeyTypeId());
//                return wrapper;
//            }
//            if (!bizFieldTypeRefOpt.isPresent()) {
//                log.error("[YetCache] No type ref for: {}", descriptor.getFieldTypeId());
//                return wrapper;
//            }
//            if (!bizValTypeRefOpt.isPresent()) {
//                log.error("[YetCache] No type ref for: {}", descriptor.getValueTypeId());
//                return wrapper;
//            }
//            Object bizKey = reifySlot(payload.getKey(), bizKeyTypeRefOpt.get());
//            payload.setKey(bizKey);
//            for (HashPlayload.FieldValue fieldValue : payload.getFieldValues()) {
//                Object bizField = reifySlot(fieldValue.getField(), bizFieldTypeRefOpt.get());
//                Object value = reifySlot(fieldValue.getValue(), bizValTypeRefOpt.get());
//                fieldValue.setField(bizField);
//                fieldValue.setValue(value);
//            }
//        }
//        return wrapper;
//    }
//}
