package com.yetcache.agent.broadcast.receiver.handler;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.agent.broadcast.command.CacheCommand;
import com.yetcache.agent.broadcast.command.playload.HashPlayload;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.CacheAgent;
import com.yetcache.agent.core.structure.hash.BaseHashCacheAgent;
import com.yetcache.agent.core.structure.hash.HashCacheAgent;
import com.yetcache.agent.interceptor.BehaviorType;
import com.yetcache.agent.interceptor.StructureBehaviorKey;
import com.yetcache.agent.regitry.CacheAgentRegistryHub;
import com.yetcache.core.codec.JsonTypeConverter;
import com.yetcache.core.codec.TypeRef;
import com.yetcache.core.codec.TypeRefRegistry;
import com.yetcache.core.config.CacheLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author walter.yan
 * @since 2025/7/16
 * 处理 DynamicHash 结构的 REFRESH_ALL 广播命令
 */
@Slf4j
@AllArgsConstructor
public class HashCacheAgentPutAllHandler implements CacheBroadcastHandler {
    protected final CacheAgentRegistryHub cacheAgentRegistryHub;
    private final TypeRefRegistry typeRefRegistry;
    private final JsonTypeConverter jsonTypeConverter;

    @Override
    public boolean supports(StructureBehaviorKey sbKey) {
        if (null == sbKey) {
            return false;
        }
        return StructureType.HASH.equals(sbKey.getStructureType())
                && BehaviorType.PUT_ALL.equals(sbKey.getBehaviorType());
    }

    @Override
    public void handle(CacheCommand cmd) {
        if (null == cmd.getPayload()) {
            log.error("缓存代理类 {} 缓存更新命令 {} 缺少 payload", cmd.getDescriptor().getComponentName(), cmd);
            return;
        }
        HashPlayload playload = (HashPlayload) cmd.getPayload();
        String key = playload.getKey();
        List<HashPlayload.FieldValue> fieldValues = playload.getFieldValues();
        if (null == key || CollUtil.isEmpty(fieldValues)) {
            log.error("缓存代理类 {} 缓存更新命令 {} payload 缺少 bizKey 或 valueMap", cmd.getDescriptor().getComponentName(), cmd);
            return;
        }
        Optional<CacheAgent> agentOpt = cacheAgentRegistryHub.find(cmd.getDescriptor().getComponentName());
        if (!agentOpt.isPresent()) {
            log.error("缓存代理类 {} 不存在", cmd.getDescriptor().getComponentName());
            return;
        }
        CacheAgent agent = agentOpt.get();
        if (agent instanceof HashCacheAgent) {
            BaseHashCacheAgent hashAgent = (BaseHashCacheAgent) agent;
            if (hashAgent.cacheLevel() == CacheLevel.LOCAL || hashAgent.cacheLevel() == CacheLevel.BOTH) {
                Map<String, Object> typedFieldValueMap = resolveTypedFieldValueMap(cmd);
                hashAgent.putAllToLocal(key, typedFieldValueMap);
            }
        } else {
            log.error("缓存代理类 {} 不是 DynamicHashCacheAgent 类型", agent.getClass().getName());
        }
    }

    private <T> Map<String, T> resolveTypedFieldValueMap(CacheCommand cmd) {
        String componentName = cmd.getDescriptor().getComponentName();
        HashPlayload playload = (HashPlayload) cmd.getPayload();
        try {
            TypeRef<?> typeRef = typeRefRegistry.get(playload.getValueTypeId());
            if (null == typeRef) {
                log.error("缓存代理类 {} 缓存更新命令 payload 缺少 valueTypeId, playload: {}", componentName, playload);
                return Collections.emptyMap();
            }
            Map<String, T> typedFieldValueMap = new HashMap<>();
            for (HashPlayload.FieldValue fieldValue : playload.getFieldValues()) {
                typedFieldValueMap.put(fieldValue.getField(),
                        jsonTypeConverter.convert(fieldValue.getValue(), typeRef.getType()));
            }
            return typedFieldValueMap;
        } catch (Exception e) {
            log.error("缓存代理类 {} 缓存更新命令payload {} 转换 value 失败", componentName, playload, e);
            return Collections.emptyMap();
        }
    }
}
