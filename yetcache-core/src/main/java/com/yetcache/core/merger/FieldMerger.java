package com.yetcache.core.merger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

/**
 * 通用字段合并工具：实现字段级非 null 合并，支持结构递归，带最大深度控制
 *
 * @author walter.yan
 * @since 2025/7/6
 */
public class FieldMerger {

    public static <T> T mergeNonNullFields(T base, T override, int maxDepth) {
        return mergeNonNullFieldsInternal(base, override, maxDepth, 0);
    }

    @SuppressWarnings("unchecked")
    private static <T> T mergeNonNullFieldsInternal(T base, T override, int maxDepth, int currentDepth) {
        if (base == null && override == null) return null;
        if (base == null) return deepClone(override);
        if (override == null) return deepClone(base);
        if (currentDepth > maxDepth) return deepClone(base);

        T result = deepClone(base);
        Field[] fields = result.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);
            try {
                Object overrideValue = field.get(override);
                if (overrideValue != null) {
                    Object baseValue = field.get(result);
                    if (isMergeablePojo(field.getType())) {
                        Object mergedValue = mergeNonNullFieldsInternal(baseValue, overrideValue, maxDepth, currentDepth + 1);
                        field.set(result, mergedValue);
                    } else {
                        field.set(result, overrideValue);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to merge field: " + field.getName(), e);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T deepClone(T obj) {
        if (obj == null) return null;
        try {
            Constructor<T> ctor = (Constructor<T>) obj.getClass().getConstructor(obj.getClass());
            return ctor.newInstance(obj);
        } catch (Exception e) {
            throw new RuntimeException("Object must support copy constructor: " + obj.getClass(), e);
        }
    }

    private static boolean isMergeablePojo(Class<?> type) {
        return !(type.isPrimitive() ||
                type.isEnum() ||
                type.equals(String.class) ||
                Number.class.isAssignableFrom(type) ||
                Boolean.class.equals(type) ||
                Collection.class.isAssignableFrom(type) ||
                Map.class.isAssignableFrom(type));
    }
}

