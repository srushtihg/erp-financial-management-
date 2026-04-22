package com.erp.financial.service;

import com.erp.financial.domain.TableDefinition;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class DemoDataService {
    private final GenericCrudService crudService;
    private final Map<String, TableDefinition> tables;

    public DemoDataService(GenericCrudService crudService, Map<String, TableDefinition> tables) {
        this.crudService = crudService;
        this.tables = tables;
    }

    public void loadPresentationData(String userId) {
        upsert("users", mapOf(
                "user_id", "U10",
                "username", "finance_admin",
                "password", "secure123",
                "role", "ADMIN"
        ), userId);

        upsert("accounts_payable", mapOf(
                "invoice_id", "AP001",
                "vendor_name", "Dell India",
                "invoice_number", "DELL-1001",
                "invoice_date", LocalDate.of(2026, 4, 1),
                "due_date", LocalDate.of(2026, 4, 25),
                "amount", "45000",
                "tax_percent", "18",
                "net_amount", "53100",
                "status", "Pending"
        ), userId);
        upsert("accounts_payable", mapOf(
                "invoice_id", "AP002",
                "vendor_name", "AWS Cloud Services",
                "invoice_number", "AWS-2004",
                "invoice_date", LocalDate.of(2026, 4, 6),
                "due_date", LocalDate.of(2026, 4, 28),
                "amount", "76000",
                "tax_percent", "18",
                "net_amount", "89680",
                "status", "Approved"
        ), userId);

        upsert("accounts_receivable", mapOf(
                "invoice_no", "AR001",
                "customer_name", "ABC Retail",
                "invoice_number", "ABC-9001",
                "issue_date", LocalDate.of(2026, 4, 3),
                "amount", "72000",
                "payment_status", "Pending",
                "revenue_status", "Recognized"
        ), userId);
        upsert("accounts_receivable", mapOf(
                "invoice_no", "AR002",
                "customer_name", "Zenith Logistics",
                "invoice_number", "ZEN-9012",
                "issue_date", LocalDate.of(2026, 4, 7),
                "amount", "94000",
                "payment_status", "Partially Paid",
                "revenue_status", "Recognized"
        ), userId);

        upsert("ledger_entries", mapOf(
                "entry_id", "GL001",
                "entry_date", LocalDate.of(2026, 4, 3),
                "account_name", "Sales Revenue",
                "debit", "0",
                "credit", "72000",
                "balance", "72000",
                "description", "Receivable invoice posted"
        ), userId);
        upsert("ledger_entries", mapOf(
                "entry_id", "GL002",
                "entry_date", LocalDate.of(2026, 4, 8),
                "account_name", "Vendor Expense",
                "debit", "53100",
                "credit", "0",
                "balance", "18900",
                "description", "Payable invoice accrued"
        ), userId);

        upsert("assets", mapOf(
                "asset_id", "AS001",
                "asset_name", "Office Laptop Fleet",
                "category", "IT Equipment",
                "purchase_date", LocalDate.of(2026, 1, 10),
                "purchase_value", "80000",
                "annual_depreciation", "16000",
                "current_value", "64000",
                "depreciation_method", "Straight Line"
        ), userId);

        upsert("cash_entries", mapOf(
                "entry_id", "CASH001",
                "type", "Inflow",
                "amount", "60000",
                "balance", "60000"
        ), userId);
        upsert("cash_entries", mapOf(
                "entry_id", "CASH002",
                "type", "Outflow",
                "amount", "18000",
                "balance", "42000"
        ), userId);

        upsert("budgets", mapOf(
                "budget_id", "BUD001",
                "department", "Finance",
                "fiscal_year", "2026",
                "budget_amount", "150000",
                "actual_amount", "60000",
                "notes", "Annual finance operations"
        ), userId);
        upsert("budgets", mapOf(
                "budget_id", "BUD002",
                "department", "Operations",
                "fiscal_year", "2026",
                "budget_amount", "220000",
                "actual_amount", "91000",
                "notes", "Operations and logistics spend"
        ), userId);

        upsert("forecasts", mapOf(
                "forecast_id", "FC001",
                "period", "Q2-2026",
                "revenue_growth", "12",
                "expense_growth", "8",
                "proj_revenue", "250000",
                "proj_expenses", "180000",
                "proj_profit", "70000"
        ), userId);
        upsert("forecasts", mapOf(
                "forecast_id", "FC002",
                "period", "Q3-2026",
                "revenue_growth", "14",
                "expense_growth", "9",
                "proj_revenue", "290000",
                "proj_expenses", "205000",
                "proj_profit", "85000"
        ), userId);

        upsert("tax_records", mapOf(
                "tax_id", "TAX001",
                "tax_type", "GST",
                "applicable_period", "APR-2026",
                "base_amount", "100000",
                "tax_rate", "18",
                "tax_amount", "18000",
                "description", "Monthly GST filing",
                "filing_status", "Pending"
        ), userId);
        upsert("tax_records", mapOf(
                "tax_id", "TAX002",
                "tax_type", "TDS",
                "applicable_period", "APR-2026",
                "base_amount", "55000",
                "tax_rate", "10",
                "tax_amount", "5500",
                "description", "Vendor payment withholding",
                "filing_status", "Filed"
        ), userId);
    }

    private void upsert(String key, Map<String, Object> values, String userId) {
        TableDefinition table = tables.get(key);
        Object id = values.get(table.idColumn());
        boolean exists = crudService.readAll(key).stream()
                .anyMatch(row -> Objects.equals(String.valueOf(row.get(table.idColumn())), String.valueOf(id)));

        if (exists) {
            crudService.update(key, values, userId);
        } else {
            crudService.create(key, values, userId);
        }
    }

    private Map<String, Object> mapOf(Object... values) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            map.put(String.valueOf(values[i]), values[i + 1]);
        }
        return map;
    }
}
