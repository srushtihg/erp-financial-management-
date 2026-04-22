package com.erp.financial.domain;

public record UserSession(String userId, String username, String role) {
    public boolean canEdit() {
        return !"AUDITOR".equalsIgnoreCase(role);
    }
}

