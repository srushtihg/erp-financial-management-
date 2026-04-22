package com.erp.financial.service;

import com.erp.financial.domain.UserSession;
import com.erp.financial.exception.AuthenticationException;

import java.util.List;
import java.util.Map;

public final class AuthService {
    private final GenericCrudService crudService;

    public AuthService(GenericCrudService crudService) {
        this.crudService = crudService;
    }

    public UserSession login(String username, String password) {
        List<Map<String, Object>> users = crudService.readAll("users");
        return users.stream()
                .filter(row -> username.equals(row.get("username")) && password.equals(row.get("password")))
                .findFirst()
                .map(row -> new UserSession(String.valueOf(row.get("user_id")), String.valueOf(row.get("username")), String.valueOf(row.get("role"))))
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));
    }
}

