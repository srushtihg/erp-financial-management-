package com.erp.financial.ui;

import com.erp.financial.service.AppContext;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ReportPanel extends JPanel {
    private static final Color PAGE = new Color(243, 246, 251);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(26, 43, 60);
    private static final Color MUTED = new Color(105, 121, 138);
    private static final Color PRIMARY = new Color(16, 74, 122);
    private static final Color SECONDARY = new Color(39, 174, 96);
    private static final Color ACCENT = new Color(241, 140, 36);
    private static final Color DANGER = new Color(203, 83, 74);
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.of("en", "IN"));

    private final AppContext context;
    private final JPanel contentPanel = new JPanel();

    public ReportPanel(AppContext context) {
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
        header.setBackground(new Color(236, 244, 250));
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Financial Reports");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));

        JLabel subtitle = new JLabel("Summary metrics, projections, and budget-facing insights");
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 13f));

        JButton refreshButton = new JButton("Generate Summary");
        refreshButton.addActionListener(event -> refresh());

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);
        return header;
    }

    private void refresh() {
        contentPanel.removeAll();

        List<Map<String, Object>> receivables = context.crudService().readAll("accounts_receivable");
        List<Map<String, Object>> payables = context.crudService().readAll("accounts_payable");
        List<Map<String, Object>> budgets = context.crudService().readAll("budgets");
        List<Map<String, Object>> forecasts = context.crudService().readAll("forecasts");
        List<Map<String, Object>> taxes = context.crudService().readAll("tax_records");
        List<Map<String, Object>> cash = context.crudService().readAll("cash_entries");

        BigDecimal totalReceivables = sum(receivables, "amount");
        BigDecimal totalPayables = sum(payables, "net_amount");
        BigDecimal totalBudget = sum(budgets, "budget_amount");
        BigDecimal totalActual = sum(budgets, "actual_amount");
        BigDecimal projectedRevenue = sum(forecasts, "proj_revenue");
        BigDecimal projectedExpenses = sum(forecasts, "proj_expenses");
        BigDecimal projectedProfit = sum(forecasts, "proj_profit");
        BigDecimal taxLiability = sum(taxes, "tax_amount");
        BigDecimal latestCashBalance = cash.isEmpty() ? BigDecimal.ZERO : decimal(cash.get(cash.size() - 1).get("balance"));

        contentPanel.add(createSummaryCards(
                createCardMetric("Balance Sheet View", CURRENCY.format(latestCashBalance), "Cash position", SECONDARY),
                createCardMetric("Profit and Loss", CURRENCY.format(projectedProfit), "Projected profit", PRIMARY),
                createCardMetric("Budget Total", CURRENCY.format(totalBudget), "Approved budgets", new Color(125, 92, 214)),
                createCardMetric("Actual Spend", CURRENCY.format(totalActual), "Spent against budget", ACCENT)
        ));

        JPanel charts = new JPanel(new GridLayout(1, 2, 16, 16));
        charts.setOpaque(false);
        charts.setBorder(BorderFactory.createEmptyBorder(16, 24, 0, 24));

        SimpleBarChartPanel coreChart = new SimpleBarChartPanel(
                "Core Financial Totals",
                "Receivables, payables, budgets, tax, and live cash balance",
                "The reports page will populate after you add accounting records.",
                true
        );
        coreChart.setPreferredSize(new Dimension(520, 320));
        coreChart.setValues(List.of(
                new SimpleBarChartPanel.BarValue("Receivables", totalReceivables, PRIMARY),
                new SimpleBarChartPanel.BarValue("Payables", totalPayables, ACCENT),
                new SimpleBarChartPanel.BarValue("Budget", totalBudget, new Color(125, 92, 214)),
                new SimpleBarChartPanel.BarValue("Actual", totalActual, new Color(92, 107, 120)),
                new SimpleBarChartPanel.BarValue("Tax", taxLiability, DANGER),
                new SimpleBarChartPanel.BarValue("Cash", latestCashBalance, SECONDARY)
        ));

        SimpleBarChartPanel projectionChart = new SimpleBarChartPanel(
                "Planning and Forecasting",
                "Projected revenue, expenses, and profit from forecasting rows",
                "Create forecast rows to visualize projected performance.",
                true
        );
        projectionChart.setPreferredSize(new Dimension(520, 320));
        projectionChart.setValues(List.of(
                new SimpleBarChartPanel.BarValue("Projected Revenue", projectedRevenue, PRIMARY),
                new SimpleBarChartPanel.BarValue("Projected Expenses", projectedExpenses, ACCENT),
                new SimpleBarChartPanel.BarValue("Projected Profit", projectedProfit, SECONDARY)
        ));

        charts.add(coreChart);
        charts.add(projectionChart);
        contentPanel.add(charts);

        JPanel lower = new JPanel(new GridLayout(1, 2, 16, 16));
        lower.setOpaque(false);
        lower.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));
        lower.add(createNarrativePanel(totalReceivables, totalPayables, totalBudget, totalActual, projectedRevenue, projectedExpenses, projectedProfit, taxLiability, latestCashBalance));
        lower.add(createDepartmentPanel(budgets));
        contentPanel.add(lower);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createSummaryCards(JPanel... cards) {
        JPanel panel = new JPanel(new GridLayout(1, 4, 16, 16));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 0, 24));
        for (JPanel card : cards) {
            panel.add(card);
        }
        return panel;
    }

    private JPanel createCardMetric(String title, String value, String subtitle, Color stripe) {
        JPanel panel = createCard(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 6, stripe),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 13f));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(TEXT);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 22f));

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(MUTED);
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, 12f));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createNarrativePanel(BigDecimal totalReceivables,
                                        BigDecimal totalPayables,
                                        BigDecimal totalBudget,
                                        BigDecimal totalActual,
                                        BigDecimal projectedRevenue,
                                        BigDecimal projectedExpenses,
                                        BigDecimal projectedProfit,
                                        BigDecimal taxLiability,
                                        BigDecimal latestCashBalance) {
        JPanel panel = createCard(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Report Narrative");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setOpaque(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(TEXT);
        area.setFont(area.getFont().deriveFont(Font.PLAIN, 13f));
        area.setText("""
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

                Interpretation
                - Use the CRUD tabs to add live records for each subsystem.
                - The dashboard and report charts update directly from database rows.
                - Empty charts are expected until you add operational data.
                """.formatted(
                CURRENCY.format(totalReceivables),
                CURRENCY.format(totalPayables),
                CURRENCY.format(totalBudget),
                CURRENCY.format(totalActual),
                CURRENCY.format(totalBudget.subtract(totalActual)),
                CURRENCY.format(projectedRevenue),
                CURRENCY.format(projectedExpenses),
                CURRENCY.format(projectedProfit),
                CURRENCY.format(taxLiability),
                CURRENCY.format(latestCashBalance)
        ));

        panel.add(title, BorderLayout.NORTH);
        panel.add(area, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDepartmentPanel(List<Map<String, Object>> budgets) {
        JPanel panel = createCard(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Budget Department View");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        if (budgets.isEmpty()) {
            JLabel empty = new JLabel("No budget rows yet. Add budgets to see department-level bars and variance.");
            empty.setForeground(MUTED);
            empty.setFont(empty.getFont().deriveFont(Font.PLAIN, 13f));
            body.add(empty);
        } else {
            for (Map<String, Object> row : budgets) {
                BigDecimal budget = decimal(row.get("budget_amount"));
                BigDecimal actual = decimal(row.get("actual_amount"));
                body.add(createBudgetRow(String.valueOf(row.get("department")), budget, actual));
            }
        }

        panel.add(title, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBudgetRow(String department, BigDecimal budget, BigDecimal actual) {
        JPanel row = new JPanel(new BorderLayout(0, 6));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel title = new JLabel(department + "  |  Budget " + CURRENCY.format(budget) + "  |  Actual " + CURRENCY.format(actual));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 12f));

        JPanel track = new JPanel(null);
        track.setOpaque(true);
        track.setBackground(new Color(232, 238, 244));
        track.setPreferredSize(new Dimension(100, 18));

        int width = 360;
        BigDecimal safeBudget = budget.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ONE : budget;
        int fillWidth = Math.min(width, Math.max(8, actual.multiply(BigDecimal.valueOf(width))
                .divide(safeBudget, 2, RoundingMode.HALF_UP)
                .intValue()));

        JPanel fill = new JPanel();
        fill.setBackground(actual.compareTo(budget) > 0 ? DANGER : SECONDARY);
        fill.setBounds(0, 0, fillWidth, 18);
        track.add(fill);

        row.add(title, BorderLayout.NORTH);
        row.add(track, BorderLayout.CENTER);
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
