package com.erp.financial.domain;

import com.erp.financial.exception.ValidationException;
import com.erp.financial.util.ValidationRules;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TableRegistry {
    private TableRegistry() {
    }

    public static Map<String, TableDefinition> all() {
        Map<String, TableDefinition> tables = new LinkedHashMap<>();

        tables.put("users", new TableDefinition(
                "users",
                "Users",
                "users",
                "user_id",
                List.of(
                        TableField.text("user_id", "User ID", true, true),
                        TableField.text("username", "Username", true, true),
                        TableField.password("password", "Password", true, true),
                        TableField.enumeration("role", "Role", true, true, List.of("ADMIN", "ACCOUNTANT", "AUDITOR"))
                ),
                values -> {
                    ValidationRules.requireText(values, "user_id", "User ID");
                    ValidationRules.requireText(values, "username", "Username");
                    ValidationRules.requireText(values, "password", "Password");
                    ValidationRules.requireChoice(values, "role", "Role", List.of("ADMIN", "ACCOUNTANT", "AUDITOR"));
                }
        ));

        tables.put("accounts_payable", new TableDefinition(
                "accounts_payable",
                "Accounts Payable",
                "accounts_payable",
                "invoice_id",
                List.of(
                        TableField.text("invoice_id", "Invoice ID", true, true),
                        TableField.text("vendor_name", "Vendor Name", true, true),
                        TableField.text("invoice_number", "Invoice Number", true, true),
                        TableField.date("invoice_date", "Invoice Date", true, true),
                        TableField.date("due_date", "Due Date", true, true),
                        TableField.decimal("amount", "Amount", true, true),
                        TableField.decimal("tax_percent", "Tax %", true, true),
                        TableField.decimal("net_amount", "Net Amount", true, true),
                        TableField.enumeration("status", "Status", true, true, List.of("Pending", "Approved", "Paid"))
                ),
                values -> {
                    ValidationRules.requireText(values, "invoice_id", "Invoice ID");
                    ValidationRules.requireText(values, "vendor_name", "Vendor Name");
                    ValidationRules.requireText(values, "invoice_number", "Invoice Number");
                    ValidationRules.requireDate(values, "invoice_date", "Invoice Date");
                    ValidationRules.requireDate(values, "due_date", "Due Date");
                    ValidationRules.requirePositiveDecimal(values, "amount", "Amount");
                    ValidationRules.requireNonNegativeDecimal(values, "tax_percent", "Tax %");
                    ValidationRules.requirePositiveDecimal(values, "net_amount", "Net Amount");
                    ValidationRules.requireChoice(values, "status", "Status", List.of("Pending", "Approved", "Paid"));
                    if (ValidationRules.date(values, "due_date").isBefore(ValidationRules.date(values, "invoice_date"))) {
                        throw new ValidationException("Due date cannot be earlier than invoice date.");
                    }
                }
        ));

        tables.put("accounts_receivable", new TableDefinition(
                "accounts_receivable",
                "Accounts Receivable",
                "accounts_receivable",
                "invoice_no",
                List.of(
                        TableField.text("invoice_no", "Invoice No", true, true),
                        TableField.text("customer_name", "Customer Name", true, true),
                        TableField.text("invoice_number", "Invoice Number", true, true),
                        TableField.date("issue_date", "Issue Date", true, true),
                        TableField.decimal("amount", "Amount", true, true),
                        TableField.enumeration("payment_status", "Payment Status", true, true, List.of("Pending", "Paid", "Partially Paid")),
                        TableField.enumeration("revenue_status", "Revenue Status", true, true, List.of("Pending", "Recognized"))
                ),
                values -> {
                    ValidationRules.requireText(values, "invoice_no", "Invoice No");
                    ValidationRules.requireText(values, "customer_name", "Customer Name");
                    ValidationRules.requireText(values, "invoice_number", "Invoice Number");
                    ValidationRules.requireDate(values, "issue_date", "Issue Date");
                    ValidationRules.requirePositiveDecimal(values, "amount", "Amount");
                    ValidationRules.requireChoice(values, "payment_status", "Payment Status", List.of("Pending", "Paid", "Partially Paid"));
                    ValidationRules.requireChoice(values, "revenue_status", "Revenue Status", List.of("Pending", "Recognized"));
                }
        ));

        tables.put("ledger_entries", new TableDefinition(
                "ledger_entries",
                "General Ledger",
                "ledger_entries",
                "entry_id",
                List.of(
                        TableField.text("entry_id", "Entry ID", true, true),
                        TableField.date("entry_date", "Entry Date", true, true),
                        TableField.text("account_name", "Account Name", true, true),
                        TableField.decimal("debit", "Debit", true, true),
                        TableField.decimal("credit", "Credit", true, true),
                        TableField.decimal("balance", "Balance", true, true),
                        TableField.text("description", "Description", false, true)
                ),
                values -> {
                    ValidationRules.requireText(values, "entry_id", "Entry ID");
                    ValidationRules.requireDate(values, "entry_date", "Entry Date");
                    ValidationRules.requireText(values, "account_name", "Account Name");
                    ValidationRules.requireNonNegativeDecimal(values, "debit", "Debit");
                    ValidationRules.requireNonNegativeDecimal(values, "credit", "Credit");
                    ValidationRules.requireNonNegativeDecimal(values, "balance", "Balance");
                    BigDecimal debit = ValidationRules.decimal(values, "debit");
                    BigDecimal credit = ValidationRules.decimal(values, "credit");
                    if (debit.compareTo(BigDecimal.ZERO) == 0 && credit.compareTo(BigDecimal.ZERO) == 0) {
                        throw new ValidationException("Either debit or credit must be greater than zero.");
                    }
                }
        ));

        tables.put("assets", new TableDefinition(
                "assets",
                "Assets",
                "assets",
                "asset_id",
                List.of(
                        TableField.text("asset_id", "Asset ID", true, true),
                        TableField.text("asset_name", "Asset Name", true, true),
                        TableField.text("category", "Category", true, true),
                        TableField.date("purchase_date", "Purchase Date", true, true),
                        TableField.decimal("purchase_value", "Purchase Value", true, true),
                        TableField.decimal("annual_depreciation", "Annual Depreciation", true, true),
                        TableField.decimal("current_value", "Current Value", true, true),
                        TableField.enumeration("depreciation_method", "Depreciation Method", true, true, List.of("Straight Line", "Written Down Value"))
                ),
                values -> {
                    ValidationRules.requireText(values, "asset_id", "Asset ID");
                    ValidationRules.requireText(values, "asset_name", "Asset Name");
                    ValidationRules.requireText(values, "category", "Category");
                    ValidationRules.requireDate(values, "purchase_date", "Purchase Date");
                    ValidationRules.requirePositiveDecimal(values, "purchase_value", "Purchase Value");
                    ValidationRules.requireNonNegativeDecimal(values, "annual_depreciation", "Annual Depreciation");
                    ValidationRules.requireNonNegativeDecimal(values, "current_value", "Current Value");
                    ValidationRules.requireChoice(values, "depreciation_method", "Depreciation Method", List.of("Straight Line", "Written Down Value"));
                    if (ValidationRules.decimal(values, "current_value").compareTo(ValidationRules.decimal(values, "purchase_value")) > 0) {
                        throw new ValidationException("Current value cannot exceed purchase value.");
                    }
                }
        ));

        tables.put("cash_entries", new TableDefinition(
                "cash_entries",
                "Cash Management",
                "cash_entries",
                "entry_id",
                List.of(
                        TableField.text("entry_id", "Entry ID", true, true),
                        TableField.enumeration("type", "Type", true, true, List.of("Inflow", "Outflow")),
                        TableField.decimal("amount", "Amount", true, true),
                        TableField.decimal("balance", "Balance", true, true)
                ),
                values -> {
                    ValidationRules.requireText(values, "entry_id", "Entry ID");
                    ValidationRules.requireChoice(values, "type", "Type", List.of("Inflow", "Outflow"));
                    ValidationRules.requirePositiveDecimal(values, "amount", "Amount");
                    ValidationRules.requireNonNegativeDecimal(values, "balance", "Balance");
                }
        ));

        tables.put("budgets", new TableDefinition(
                "budgets",
                "Budgeting",
                "budgets",
                "budget_id",
                List.of(
                        TableField.text("budget_id", "Budget ID", true, true),
                        TableField.text("department", "Department", true, true),
                        TableField.text("fiscal_year", "Fiscal Year", true, true),
                        TableField.decimal("budget_amount", "Budget Amount", true, true),
                        TableField.decimal("actual_amount", "Actual Amount", true, true),
                        TableField.text("notes", "Notes", false, true)
                ),
                values -> {
                    ValidationRules.requireText(values, "budget_id", "Budget ID");
                    ValidationRules.requireText(values, "department", "Department");
                    ValidationRules.requireText(values, "fiscal_year", "Fiscal Year");
                    ValidationRules.requirePositiveDecimal(values, "budget_amount", "Budget Amount");
                    ValidationRules.requireNonNegativeDecimal(values, "actual_amount", "Actual Amount");
                }
        ));

        tables.put("forecasts", new TableDefinition(
                "forecasts",
                "Forecasting",
                "forecasts",
                "forecast_id",
                List.of(
                        TableField.text("forecast_id", "Forecast ID", true, true),
                        TableField.text("period", "Period", true, true),
                        TableField.decimal("revenue_growth", "Revenue Growth", true, true),
                        TableField.decimal("expense_growth", "Expense Growth", true, true),
                        TableField.decimal("proj_revenue", "Projected Revenue", true, true),
                        TableField.decimal("proj_expenses", "Projected Expenses", true, true),
                        TableField.decimal("proj_profit", "Projected Profit", true, true)
                ),
                values -> {
                    ValidationRules.requireText(values, "forecast_id", "Forecast ID");
                    ValidationRules.requireText(values, "period", "Period");
                    ValidationRules.requireNonNegativeDecimal(values, "proj_revenue", "Projected Revenue");
                    ValidationRules.requireNonNegativeDecimal(values, "proj_expenses", "Projected Expenses");
                }
        ));

        tables.put("tax_records", new TableDefinition(
                "tax_records",
                "Tax Management",
                "tax_records",
                "tax_id",
                List.of(
                        TableField.text("tax_id", "Tax ID", true, true),
                        TableField.text("tax_type", "Tax Type", true, true),
                        TableField.text("applicable_period", "Applicable Period", true, true),
                        TableField.decimal("base_amount", "Base Amount", true, true),
                        TableField.decimal("tax_rate", "Tax Rate", true, true),
                        TableField.decimal("tax_amount", "Tax Amount", true, true),
                        TableField.text("description", "Description", false, true),
                        TableField.enumeration("filing_status", "Filing Status", true, true, List.of("Pending", "Filed"))
                ),
                values -> {
                    ValidationRules.requireText(values, "tax_id", "Tax ID");
                    ValidationRules.requireText(values, "tax_type", "Tax Type");
                    ValidationRules.requireText(values, "applicable_period", "Applicable Period");
                    ValidationRules.requirePositiveDecimal(values, "base_amount", "Base Amount");
                    ValidationRules.requireNonNegativeDecimal(values, "tax_rate", "Tax Rate");
                    ValidationRules.requireNonNegativeDecimal(values, "tax_amount", "Tax Amount");
                    ValidationRules.requireChoice(values, "filing_status", "Filing Status", List.of("Pending", "Filed"));
                    if (ValidationRules.decimal(values, "tax_rate").compareTo(new BigDecimal("100")) > 0) {
                        throw new ValidationException("Tax rate cannot exceed 100.");
                    }
                }
        ));

        tables.put("audit_logs", new TableDefinition(
                "audit_logs",
                "Audit Logs",
                "audit_logs",
                "log_id",
                List.of(
                        TableField.integer("log_id", "Log ID", false, false),
                        TableField.text("user_id", "User ID", false, true),
                        TableField.text("action", "Action", false, true),
                        TableField.text("module", "Module", false, true),
                        TableField.text("details", "Details", false, true),
                        TableField.text("timestamp", "Timestamp", false, false)
                ),
                values -> {
                }
        ));

        return tables;
    }
}

