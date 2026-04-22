package com.erp.financial.service;

import com.erp.financial.db.ConnectionFactory;
import com.erp.financial.domain.TableDefinition;
import com.erp.financial.domain.TableRegistry;

import java.util.Map;

public final class AppContext {
    private final Map<String, TableDefinition> tables;
    private final GenericCrudService crudService;
    private final AuthService authService;
    private final ReportService reportService;
    private final AuditService auditService;
    private final DemoDataService demoDataService;

    public AppContext(ConnectionFactory connectionFactory) {
        this.tables = TableRegistry.all();
        this.auditService = new AuditService(connectionFactory);
        this.crudService = new GenericCrudService(connectionFactory, tables, auditService);
        this.authService = new AuthService(crudService);
        this.reportService = new ReportService(crudService);
        this.demoDataService = new DemoDataService(crudService, tables);
    }

    public Map<String, TableDefinition> tables() {
        return tables;
    }

    public GenericCrudService crudService() {
        return crudService;
    }

    public AuthService authService() {
        return authService;
    }

    public ReportService reportService() {
        return reportService;
    }

    public AuditService auditService() {
        return auditService;
    }

    public DemoDataService demoDataService() {
        return demoDataService;
    }
}
