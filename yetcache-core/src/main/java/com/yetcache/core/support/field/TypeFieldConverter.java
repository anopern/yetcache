package com.yetcache.core.support.field;

/**
 * 通用类型转换器，支持基本类型的字符串 <-> 业务字段的双向转换
 *
 * @author walter.yan
 * @since 2025/7/12
 */
public class TypeFieldConverter implements FieldConverter {

    private final Class<?> fieldType;

    public TypeFieldConverter(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public String convert(Object bizField) {
        return bizField == null ? null : bizField.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T revert(String field) {
        if (field == null) {
            return null;
        }

        if (fieldType == String.class) {
            return (T) field;
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return (T) Integer.valueOf(field);
        } else if (fieldType == Long.class || fieldType == long.class) {
            return (T) Long.valueOf(field);
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return (T) Boolean.valueOf(field);
        } else if (fieldType == Double.class || fieldType == double.class) {
            return (T) Double.valueOf(field);
        } else if (fieldType == Float.class || fieldType == float.class) {
            return (T) Float.valueOf(field);
        } else {
            throw new UnsupportedOperationException("Unsupported field type: " + fieldType.getName());
        }
    }
}
