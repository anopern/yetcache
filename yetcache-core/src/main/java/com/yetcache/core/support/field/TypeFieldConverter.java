package com.yetcache.core.support.field;

/**
 * 通用类型转换器，支持基本类型的字符串 <-> 业务字段的双向转换
 *
 * @author walter.yan
 * @since 2025/7/12
 */
public class TypeFieldConverter<F> implements FieldConverter<F> {

    private final Class<F> fieldType;

    public TypeFieldConverter(Class<F> fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public String convert(F bizField) {
        return bizField == null ? null : bizField.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public F reverse(String field) {
        if (field == null) {
            return null;
        }

        if (fieldType == String.class) {
            return (F) field;
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return (F) Integer.valueOf(field);
        } else if (fieldType == Long.class || fieldType == long.class) {
            return (F) Long.valueOf(field);
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return (F) Boolean.valueOf(field);
        } else if (fieldType == Double.class || fieldType == double.class) {
            return (F) Double.valueOf(field);
        } else if (fieldType == Float.class || fieldType == float.class) {
            return (F) Float.valueOf(field);
        } else {
            throw new UnsupportedOperationException("Unsupported field type: " + fieldType.getName());
        }
    }
}
