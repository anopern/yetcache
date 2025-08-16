package com.yetcache.core.codec;

import org.springframework.lang.Nullable;

/**
 * @author walter.yan
 * @since 2025/8/16
 */
public interface ReifyContext {
    @Nullable
    TypeRef<?> get(String slot);

    ReifyContext with(String slot, TypeRef<?> typeRef);

    static ReifyContext of() {
        return new DefaultReifyContext();
    }
}
