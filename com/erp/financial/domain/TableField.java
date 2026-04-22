package com.erp.financial.domain;

import java.util.List;

public record TableField(
        String name,
        String label,
        FieldType type,
        boolean required,
        boolean editable,
        List<String> options
) {
    public static TableField text(String name, String label, boolean required, boolean editable) {
        return new TableField(name, label, FieldType.TEXT, required, editable, List.of());
    }

    public static TableField password(String name, String label, boolean required, boolean editable) {
        return new TableField(name, label, FieldType.PASSWORD, required, editable, List.of());
    }

    public static TableField date(String name, String label, boolean required, boolean editable) {
        return new TableField(name, label, FieldType.DATE, required, editable, List.of());
    }

    public static TableField decimal(String name, String label, boolean required, boolean editable) {
        return new TableField(name, label, FieldType.DECIMAL, required, editable, List.of());
    }

    public static TableField integer(String name, String label, boolean required, boolean editable) {
        return new TableField(name, label, FieldType.INTEGER, required, editable, List.of());
    }

    public static TableField enumeration(String name, String label, boolean required, boolean editable, List<String> options) {
        return new TableField(name, label, FieldType.ENUM, required, editable, options);
    }
}

