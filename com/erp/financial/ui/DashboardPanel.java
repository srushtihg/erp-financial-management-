package com.erp.financial.ui;

import com.erp.financial.service.AppContext;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class DashboardPanel extends JPanel {
    private static final Color PAGE = new Color(243, 246, 251);
    private static final Color CARD = Color.WHITE;
    private static final Color PRIMARY = new Color(16, 74, 122);
    private static final Color SECONDARY = new Color(39, 174, 96);
    private static final Color ACCENT = new Color(241, 140, 36);
    private static final Color DANGER = new Color(203, 83, 74);
    private static final Color TEXT = new Color(26, 43, 60);
    private static final Color MUTED = new Color(105, 121, 138);
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.of("en", "IN"));

    private final AppContext context;
    private final JPanel contentPanel = new JPanel();

    public DashboardPanel(AppContext context) {
        this.context = context;
        setLayout(new BorderLayout());
        setBackground(PAGE);

        add(buildHeader(), BorderLayout.NORTH);

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(PAGE);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(10, 52, 88));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Financial Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));

        JLabel subtitle = new JLabel("Accounts, budgets, forecasts, cash flow, reporting, and audit visibility");
        subtitle.setForeground(new Color(213, 229, 244));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 13f));

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);

        JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.addActionListener(event -> refresh());

        JButton demoDataButton = new JButton("Load Demo Data");
        demoDataButton.addActionListener(event -> loadDemoData());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(demoDataButton);
        buttonPanel.add(refreshButton);

        header.add(textPanel, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);
        return header;
    }

    private void loadDemoData() {
        try {
            context.demoDataService().loadPresentationData("U1");
            refresh();
            JOptionPane.showMessageDialog(this, "Demo data loaded. Dashboard is now presentation-ready.");
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Demo Data Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refresh() {
        contentPanel.removeAll();

        List<Map<String, Object>> users = context.crudService().readAll("users");
        List<Map<String, Object>> payables = context.crudService().readAll("accounts_payable");
        List<Map<String, Object>> receivables = context.crudService().readAll("accounts_receivable");
        List<Map<String, Object>> ledgerEntries = context.crudService().readAll("ledger_entries");
        List<Map<String, Object>> budgets = context.crudService().readAll("budgets");
        List<Map<String, Object>> forecasts = context.crudService().readAll("forecasts");
        List<Map<String, Object>> cashEntries = context.crudService().readAll("cash_entries");
        List<Map<String, Object>> taxRecords = context.crudService().readAll("tax_records");
        List<Map<String, Object>> auditLogs = context.crudService().readAll("audit_logs");

        BigDecimal receivableTotal = sum(receivables, "amount");
        BigDecimal payableTotal = sum(payables, "net_amount");
        BigDecimal budgetTotal = sum(budgets, "budget_amount");
        BigDecimal actualTotal = sum(budgets, "actual_amount");
        BigDecimal projectedProfit = sum(forecasts, "proj_profit");
        BigDecimal taxTotal = sum(taxRecords, "tax_amount");
        BigDecimal latestCashBalance = cashEntries.isEmpty()
                ? BigDecimal.ZERO
                : decimal(cashEntries.get(cashEntries.size() - 1).get("balance"));

        contentPanel.add(createMetricsPanel(
                createMetricCard("Cash Balance", CURRENCY.format(latestCashBalance), "Latest balance from cash management", SECONDARY),
                createMetricCard("Receivables", CURRENCY.format(receivableTotal), "Outstanding invoiced revenue", PRIMARY),
                createMetricCard("Payables", CURRENCY.format(payableTotal), "Vendor liabilities and dues", ACCENT),
                createMetricCard("Budget Variance", CURRENCY.format(budgetTotal.subtract(actualTotal)), "Budget minus actual allocation", new Color(125, 92, 214)),
                createMetricCard("Projected Profit", CURRENCY.format(projectedProfit), "From forecasting projections", new Color(0, 135, 155)),
                createMetricCard("Tax Liability", CURRENCY.format(taxTotal), "Current total tax amount", DANGER)
        ));

        JPanel chartRow = new JPanel(new GridLayout(1, 2, 16, 16));
        chartRow.setOpaque(false);
        chartRow.setBorder(BorderFactory.createEmptyBorder(16, 24, 0, 24));
        chartRow.add(createModuleVolumeChart(users, payables, receivables, ledgerEntries, budgets, forecasts, cashEntries, taxRecords, auditLogs));
        chartRow.add(createExposureChart(receivableTotal, payableTotal, budgetTotal, actualTotal, latestCashBalance, taxTotal));
        contentPanel.add(chartRow);

        JPanel lowerRow = new JPanel(new GridLayout(1, 2, 16, 16));
        lowerRow.setOpaque(false);
        lowerRow.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));
        lowerRow.add(createModuleOverview(users, payables, receivables, ledgerEntries, budgets, forecasts, cashEntries, taxRecords, auditLogs));
        lowerRow.add(createAuditPanel(auditLogs, budgets, forecasts, payables, receivables));
        contentPanel.add(lowerRow);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createMetricsPanel(JPanel... cards) {
        JPanel panel = new JPanel(new GridLayout(2, 3, 16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 0, 24));
        for (JPanel card : cards) {
            panel.add(card);
        }
        return panel;
    }

    private JPanel createMetricCard(String title, String value, String caption, Color stripe) {
        JPanel panel = createCard(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, stripe),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 13f));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(TEXT);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 22f));

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setForeground(MUTED);
        captionLabel.setFont(captionLabel.getFont().deriveFont(Font.PLAIN, 12f));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        panel.add(captionLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createModuleVolumeChart(List<Map<String, Object>> users,
                                           List<Map<String, Object>> payables,
                                           List<Map<String, Object>> receivables,
                                           List<Map<String, Object>> ledgerEntries,
                                           List<Map<String, Object>> budgets,
                                           List<Map<String, Object>> forecasts,
                                           List<Map<String, Object>> cashEntries,
                                           List<Map<String, Object>> taxRecords,
                                           List<Map<String, Object>> auditLogs) {
        SimpleBarChartPanel chart = new SimpleBarChartPanel(
                "Operational Coverage",
                "Shows how much data is present across each financial component",
                "Add records in the CRUD tabs to populate the dashboard charts.",
                false
        );
        chart.setPreferredSize(new java.awt.Dimension(520, 320));
        chart.setValues(List.of(
                new SimpleBarChartPanel.BarValue("Payables", BigDecimal.valueOf(payables.size()), ACCENT),
                new SimpleBarChartPanel.BarValue("Receivables", BigDecimal.valueOf(receivables.size()), PRIMARY),
                new SimpleBarChartPanel.BarValue("Ledger", BigDecimal.valueOf(ledgerEntries.size()), new Color(0, 135, 155)),
                new SimpleBarChartPanel.BarValue("Budgets", BigDecimal.valueOf(budgets.size()), new Color(125, 92, 214)),
                new SimpleBarChartPanel.BarValue("Forecasts", BigDecimal.valueOf(forecasts.size()), SECONDARY),
                new SimpleBarChartPanel.BarValue("Cash", BigDecimal.valueOf(cashEntries.size()), new Color(73, 92, 116)),
                new SimpleBarChartPanel.BarValue("Taxes", BigDecimal.valueOf(taxRecords.size()), DANGER),
                new SimpleBarChartPanel.BarValue("Audit Logs", BigDecimal.valueOf(auditLogs.size()), new Color(88, 112, 133)),
                new SimpleBarChartPanel.BarValue("Users", BigDecimal.valueOf(users.size()), new Color(72, 187, 120))
        ));
        return chart;
    }

    private JPanel createExposureChart(BigDecimal receivableTotal,
                                       BigDecimal payableTotal,
                                       BigDecimal budgetTotal,
                                       BigDecimal actualTotal,
                                       BigDecimal latestCashBalance,
                                       BigDecimal taxTotal) {
        SimpleBarChartPanel chart = new SimpleBarChartPanel(
                "Financial Exposure Snapshot",
                "High-level values aligned to the deliverable modules",
                "Add transactions or budget rows to see real financial comparisons.",
                true
        );
        chart.setPreferredSize(new java.awt.Dimension(520, 320));
        chart.setValues(List.of(
                new SimpleBarChartPanel.BarValue("Receivable", receivableTotal, PRIMARY),
                new SimpleBarChartPanel.BarValue("Payable", payableTotal, ACCENT),
                new SimpleBarChartPanel.BarValue("Budget", budgetTotal, new Color(125, 92, 214)),
                new SimpleBarChartPanel.BarValue("Actual", actualTotal, new Color(102, 119, 138)),
                new SimpleBarChartPanel.BarValue("Cash", latestCashBalance, SECONDARY),
                new SimpleBarChartPanel.BarValue("Tax", taxTotal, DANGER)
        ));
        return chart;
    }

    private JPanel createModuleOverview(List<Map<String, Object>> users,
                                        List<Map<String, Object>> payables,
                                        List<Map<String, Object>> receivables,
                                        List<Map<String, Object>> ledgerEntries,
                                        List<Map<String, Object>> budgets,
                                        List<Map<String, Object>> forecasts,
                                        List<Map<String, Object>> cashEntries,
                                        List<Map<String, Object>> taxRecords,
                                        List<Map<String, Object>> auditLogs) {
        JPanel panel = createCard(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Subsystem Module Overview");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel subtitle = new JLabel("Minimal wireframe content from the deliverable, with live record counts");
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        addModuleRow(list, "Accounts Payable", payables.size(), "Vendor invoices, dues, and payment status");
        addModuleRow(list, "Accounts Receivable", receivables.size(), "Customer invoices and revenue recognition status");
        addModuleRow(list, "General Ledger", ledgerEntries.size(), "Core accounting entries and balances");
        addModuleRow(list, "Cash Management", cashEntries.size(), "Cash inflow, outflow, and balance snapshots");
        addModuleRow(list, "Budgeting", budgets.size(), "Department planning and variance tracking");
        addModuleRow(list, "Forecasting", forecasts.size(), "Projected revenue, expenses, and profit");
        addModuleRow(list, "Tax Management", taxRecords.size(), "Tax amount and filing status");
        addModuleRow(list, "Data & Audit", auditLogs.size() + users.size(), "Users plus audit trail coverage");

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(4));
        top.add(subtitle);

        panel.add(top, BorderLayout.NORTH);
        panel.add(list, BorderLayout.CENTER);
        return panel;
    }

    private void addModuleRow(JPanel list, String label, int count, String description) {
        JPanel row = new JPanel(new BorderLayout(10, 4));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel title = new JLabel(label + " (" + count + ")");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));

        JLabel details = new JLabel(description);
        details.setForeground(MUTED);
        details.setFont(details.getFont().deriveFont(Font.PLAIN, 12f));

        row.add(title, BorderLayout.NORTH);
        row.add(details, BorderLayout.CENTER);
        list.add(row);
        list.add(new JSeparator());
    }

    private JPanel createAuditPanel(List<Map<String, Object>> auditLogs,
                                    List<Map<String, Object>> budgets,
                                    List<Map<String, Object>> forecasts,
                                    List<Map<String, Object>> payables,
                                    List<Map<String, Object>> receivables) {
        JPanel panel = createCard(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Readiness and Activity");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(createInsightRow("Budget planning", budgets.isEmpty() ? "No departments entered yet" : budgets.size() + " budget rows available"));
        body.add(createInsightRow("Forecast coverage", forecasts.isEmpty() ? "No projections entered yet" : forecasts.size() + " forecast rows available"));
        body.add(createInsightRow("Transaction flow", payables.size() + receivables.size() == 0
                ? "Receivables and payables are both empty"
                : (payables.size() + receivables.size()) + " transaction records captured"));

        JLabel auditTitle = new JLabel("Recent Audit Events");
        auditTitle.setForeground(TEXT);
        auditTitle.setFont(auditTitle.getFont().deriveFont(Font.BOLD, 14f));
        auditTitle.setBorder(BorderFactory.createEmptyBorder(14, 0, 6, 0));
        body.add(auditTitle);

        if (auditLogs.isEmpty()) {
            body.add(createInsightRow("Audit trail", "No audit log entries yet. Create, update, or delete records to generate them."));
        } else {
            List<Map<String, Object>> reversed = new ArrayList<>(auditLogs);
            Collections.reverse(reversed);
            for (Map<String, Object> row : reversed.stream().limit(6).toList()) {
                String event = row.get("module") + " - " + row.get("action");
                String detail = row.get("details") + " | " + row.get("timestamp");
                body.add(createInsightRow(event, detail));
            }
        }

        panel.add(title, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInsightRow(String heading, String detail) {
        JPanel row = new JPanel(new BorderLayout(0, 2));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JLabel headingLabel = new JLabel(heading);
        headingLabel.setForeground(TEXT);
        headingLabel.setFont(headingLabel.getFont().deriveFont(Font.BOLD, 13f));

        JLabel detailLabel = new JLabel(detail);
        detailLabel.setForeground(MUTED);
        detailLabel.setFont(detailLabel.getFont().deriveFont(Font.PLAIN, 12f));

        row.add(headingLabel, BorderLayout.NORTH);
        row.add(detailLabel, BorderLayout.CENTER);
        return row;
    }

    private JPanel createCard(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 230, 238)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        return panel;
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
