package com.yetcache.core.codec;

import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/8/16
 */
public class DefaultReifyContext implements ReifyContext {
    private final Map<String, TypeRef<?>> slots = new HashMap<>();

    public TypeRef<?> get(String slot) {
        return slots.get(slot);
    }

    public ReifyContext with(String slot, TypeRef<?> t) {
        slots.put(slot, t);
        return this;
    }
}
