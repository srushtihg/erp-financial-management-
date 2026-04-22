package com.erp.financial.domain;

import java.util.List;

public record TableDefinition(
        String key,
        String displayName,
        String tableName,
        String idColumn,
        List<TableField> fields,
        ValidationProfile validationProfile
) {
}

