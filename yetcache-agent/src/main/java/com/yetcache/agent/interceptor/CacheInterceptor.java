package com.yetcache.agent.interceptor;

import com.yetcache.agent.core.StructureType;
import com.yetcache.core.result.Result;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheInterceptor {

    /**
     * 唯一标识
     */
    String id();

    /**
     * 是否启用
     */
    boolean enabled();

    int getOrder();

    /**
     * 支持的行为类型
     */
    boolean supportBehavior(BehaviorType type);

    /**
     * 支持的结构类型
     */
    boolean supportStructure(StructureType type);

    /**
     * 支持的组合key（用于注册中心归类）
     */
    default List<StructureBehaviorKey> supportedKeys() {
        List<StructureBehaviorKey> keys = new ArrayList<>();
        for (BehaviorType b : BehaviorType.values()) {
            for (StructureType s : StructureType.values()) {
                if (supportBehavior(b) && supportStructure(s)) {
                    keys.add(new StructureBehaviorKey(s, b));
                }
            }
        }
        return keys;
    }

    /**
     * 拦截器主逻辑
     */
    Object invoke(C ctx, InvocationChain<C, T, R> chain) throws Throwable;
}