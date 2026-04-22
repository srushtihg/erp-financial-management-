package com.erp.financial.domain;

import java.util.Map;

@FunctionalInterface
public interface ValidationProfile {
    void validate(Map<String, Object> values);
}

