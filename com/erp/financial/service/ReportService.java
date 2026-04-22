package com.erp.financial.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class ReportService {
    private final GenericCrudService crudService;

    public ReportService(GenericCrudService crudService) {
        this.crudService = crudService;
    }

    public String buildSummary() {
        List<Map<String, Object>> receivables = crudService.readAll("accounts_receivable");
        List<Map<String, Object>> payables = crudService.readAll("accounts_payable");
        List<Map<String, Object>> budgets = crudService.readAll("budgets");
        List<Map<String, Object>> forecasts = crudService.readAll("forecasts");
        List<Map<String, Object>> taxes = crudService.readAll("tax_records");
        List<Map<String, Object>> cash = crudService.readAll("cash_entries");

        BigDecimal totalReceivables = sum(receivables, "amount");
        BigDecimal totalPayables = sum(payables, "net_amount");
        BigDecimal totalBudget = sum(budgets, "budget_amount");
        BigDecimal totalActual = sum(budgets, "actual_amount");
        BigDecimal totalProjectedRevenue = sum(forecasts, "proj_revenue");
        BigDecimal totalProjectedExpenses = sum(forecasts, "proj_expenses");
        BigDecimal totalTax = sum(taxes, "tax_amount");
        BigDecimal latestCashBalance = cash.isEmpty() ? BigDecimal.ZERO : decimal(cash.get(cash.size() - 1).get("balance"));

        return """
                Financial Summary

                Accounts Receivable Total: %s
                Accounts Payable Total: %s
                Budget Total: %s
                Actual Spend Total: %s
                Budget Variance: %s
                Projected Revenue: %s
                Projected Expenses: %s
                Projected Profit: %s
                Tax Liability: %s
                Latest Cash Balance: %s
                """.formatted(
                totalReceivables,
                totalPayables,
                totalBudget,
                totalActual,
                totalBudget.subtract(totalActual),
                totalProjectedRevenue,
                totalProjectedExpenses,
                totalProjectedRevenue.subtract(totalProjectedExpenses),
                totalTax,
                latestCashBalance
        );
    }

    public String buildDashboardMetrics() {
        return """
                KPI Snapshot

                Users: %d
                Payables: %d
                Receivables: %d
                Ledger Entries: %d
                Assets: %d
                Cash Entries: %d
                Budgets: %d
                Forecasts: %d
                Taxes: %d
                Audit Logs: %d
                """.formatted(
                crudService.readAll("users").size(),
                crudService.readAll("accounts_payable").size(),
                crudService.readAll("accounts_receivable").size(),
                crudService.readAll("ledger_entries").size(),
                crudService.readAll("assets").size(),
                crudService.readAll("cash_entries").size(),
                crudService.readAll("budgets").size(),
                crudService.readAll("forecasts").size(),
                crudService.readAll("tax_records").size(),
                crudService.readAll("audit_logs").size()
        );
    }

    private BigDecimal sum(List<Map<String, Object>> rows, String field) {
        return rows.stream()
                .map(row -> decimal(row.get(field)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal decimal(Object value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(value));
    }
}

