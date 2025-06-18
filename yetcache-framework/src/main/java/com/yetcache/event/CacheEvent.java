package com.yetcache.event;

import com.alibaba.fastjson2.annotation.JSONType;
import com.yetcache.agent.impl.AbstractCacheAgent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

/**
 * @author walter.yan
 * @since 2025/5/24
 */

@Getter
@Setter
@JSONType(ignores = {"resolvableType"})
public class CacheEvent<E> implements ResolvableTypeProvider {
    protected String instanceId = AbstractCacheAgent.INSTANCE_ID;

    // 要通知的Agent的ID
    protected String agentId;

    // 哪个租户的数据
    protected Long tenantId;

    // 具体要更新的数据，针对更新操作时候
    protected E data;

    // 创建时间，毫秒时间戳
    protected Long version = System.currentTimeMillis();

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClass(getClass())
                .as(CacheEvent.class);
    }


//    public static <E> CacheEvent<E> buildKVUpdateEvent(@NotNull String agentId,
//                                                       @Nullable Long tenantId,
//                                                       @NotNull String bizKey,
//                                                       @NotNull E data) {
//        CacheEvent<E> event = new CacheEvent<>();
//        event.setAgentId(agentId);
//        event.setTenantId(tenantId);
//        event.setBizKey(new BizKey(bizKey));
//        event.setEventType(CacheEventTypeBak.UPDATE);
//        event.setData(data);
//        return event;
//    }
//
//    public static <E> CacheEvent<E> buildKVInvalidateEvent(@NotNull String agentId,
//                                                           @Nullable Long tenantId,
//                                                           @NotNull String bizKey) {
//        CacheEvent<E> event = new CacheEvent<>();
//        event.setAgentId(agentId);
//        event.setTenantId(tenantId);
//        event.setEventType(CacheEventTypeBak.INVALIDATE);
//        event.setBizKey(new CacheEvent.BizKey(bizKey));
//        return event;
//    }
//
//    public static <E> CacheEvent<Map<String, E>> buildHashUpdateHashEvent(@NotNull String agentId,
//                                                                          @Nullable Long tenantId,
//                                                                          @NotNull String bizKey,
//                                                                          @NotNull Map<String, E> dataMap) {
//        CacheEvent<Map<String, E>> event = new CacheEvent<>();
//        event.setAgentId(agentId);
//        event.setTenantId(tenantId);
//        event.setEventType(CacheEventTypeBak.UPDATE_ALL_FIELDS);
//        event.setBizKey(new CacheEvent.BizKey(bizKey));
//        event.setData(dataMap);
//        return event;
//    }
//
//    public static <E> CacheEvent<Map<String, E>> buildHashInvalidateAllFieldsEvent(@NotNull String agentId,
//                                                                                   @Nullable Long tenantId,
//                                                                                   @NotNull String bizKey) {
//        CacheEvent<Map<String, E>> event = new CacheEvent<>();
//        event.setAgentId(agentId);
//        event.setTenantId(tenantId);
//        event.setEventType(CacheEventTypeBak.INVALIDATE_ALL_FIELDS);
//        event.setBizKey(new CacheEvent.BizKey(bizKey));
//        return event;
//    }
//
//    public static <E> CacheEvent<E> buildHashUpdateEntryEvent(@NotNull String agentId,
//                                                                           @Nullable Long tenantId,
//                                                                           @NotNull String bizKey,
//                                                                           @NotNull String bizField,
//                                                                           @NotNull E e) {
//        CacheEvent<E> event = new CacheEvent<>();
//        event.setAgentId(agentId);
//        event.setTenantId(tenantId);
//        event.setEventType(CacheEventTypeBak.UPDATE_ENTRY);
//        event.setBizKey(new CacheEvent.BizKey(bizKey, bizField));
//        event.setData(e);
//        return event;
//    }
}

