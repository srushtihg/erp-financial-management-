package com.erp.financial.ui;

import com.erp.financial.domain.FieldType;
import com.erp.financial.domain.TableDefinition;
import com.erp.financial.domain.TableField;
import com.erp.financial.domain.UserSession;
import com.erp.financial.exception.AppException;
import com.erp.financial.service.AppContext;

import javax.swing.DefaultCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ManagementPanel extends JPanel {
    private static final Color PAGE = new Color(243, 246, 251);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(26, 43, 60);
    private static final Color MUTED = new Color(105, 121, 138);
    private static final Color BORDER = new Color(222, 230, 238);
    private static final Color HEADER = new Color(236, 244, 250);

    private final AppContext context;
    private final UserSession session;
    private final TableDefinition table;
    private final DefaultTableModel model = new DefaultTableModel();
    private final JTable tableComponent = new JTable(model);
    private final Map<String, Object> inputs = new LinkedHashMap<>();

    public ManagementPanel(AppContext context, UserSession session, TableDefinition table) {
        this.context = context;
        this.session = session;
        this.table = table;
        setLayout(new BorderLayout(12, 12));
        setBackground(PAGE);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        refresh();
    }

    private Component buildContent() {
        buildGrid();
        JPanel formPanel = buildForm();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createTableCard(), formPanel);
        splitPane.setResizeWeight(0.55);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        splitPane.setOpaque(false);
        splitPane.setBackground(PAGE);
        return splitPane;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(table.displayName());
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JLabel subtitle = new JLabel(moduleDescription());
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 13f));

        JLabel note = new JLabel("Use the examples below each field while creating or updating records.");
        note.setForeground(new Color(56, 102, 143));
        note.setFont(note.getFont().deriveFont(Font.PLAIN, 12f));

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(note);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    private void buildGrid() {
        for (TableField field : table.fields()) {
            model.addColumn(field.label());
        }

        tableComponent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableComponent.setRowHeight(28);
        tableComponent.setShowGrid(false);
        tableComponent.setIntercellSpacing(new Dimension(0, 0));
        tableComponent.getTableHeader().setReorderingAllowed(false);
        tableComponent.getTableHeader().setBackground(new Color(246, 249, 252));
        tableComponent.getTableHeader().setForeground(TEXT);
        tableComponent.setSelectionBackground(new Color(222, 236, 249));
        tableComponent.setSelectionForeground(TEXT);
        tableComponent.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });
    }

    private JPanel createTableCard() {
        JPanel card = createCard(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JLabel title = new JLabel(table.displayName() + " Records");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 17f));

        JLabel subtitle = new JLabel("Select a row to load it into the form for update or delete.");
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(3));
        text.add(subtitle);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refresh());

        heading.add(text, BorderLayout.WEST);
        heading.add(refreshButton, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(tableComponent);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));

        card.add(heading, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildForm() {
        JPanel formGrid = new JPanel(new GridLayout(0, 2, 14, 14));
        formGrid.setOpaque(false);

        for (TableField field : table.fields()) {
            JPanel fieldPanel = new JPanel();
            fieldPanel.setOpaque(false);
            fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));

            JLabel label = new JLabel(field.label() + (field.required() ? " *" : ""));
            label.setForeground(TEXT);
            label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));

            Object component = createInput(field);
            inputs.put(field.name(), component);
            JComponent input = (JComponent) component;
            input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            input.setPreferredSize(new Dimension(260, 34));

            JLabel hint = new JLabel(exampleFor(field));
            hint.setForeground(MUTED);
            hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 11f));

            fieldPanel.add(label);
            fieldPanel.add(Box.createVerticalStrut(4));
            fieldPanel.add(input);
            fieldPanel.add(Box.createVerticalStrut(4));
            fieldPanel.add(hint);
            formGrid.add(fieldPanel);
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        JButton createButton = new JButton("Create");
        createButton.addActionListener(event -> createRecord());
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(event -> updateRecord());
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(event -> deleteRecord());

        boolean editable = session.canEdit() && !"audit_logs".equals(table.key());
        createButton.setEnabled(editable);
        updateButton.setEnabled(editable);
        deleteButton.setEnabled(editable);

        buttons.add(createButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);

        JPanel notes = new JPanel();
        notes.setOpaque(false);
        notes.setLayout(new BoxLayout(notes, BoxLayout.Y_AXIS));
        JLabel note1 = new JLabel("Date format: YYYY-MM-DD");
        note1.setForeground(MUTED);
        note1.setFont(note1.getFont().deriveFont(Font.PLAIN, 12f));
        JLabel note2 = new JLabel("Tip: Create sample rows in each module to populate the dashboard charts.");
        note2.setForeground(MUTED);
        note2.setFont(note2.getFont().deriveFont(Font.PLAIN, 12f));
        notes.add(note1);
        notes.add(Box.createVerticalStrut(4));
        notes.add(note2);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(notes, BorderLayout.WEST);
        footer.add(buttons, BorderLayout.EAST);

        JPanel card = createCard(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Data Entry Form");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 17f));
        JLabel subtitle = new JLabel("Each field includes an example placeholder so you know what to enter.");
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12f));
        heading.add(title);
        heading.add(Box.createVerticalStrut(3));
        heading.add(subtitle);

        card.add(heading, BorderLayout.NORTH);
        card.add(formGrid, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        enableAutoCalculation();
        return card;
    }

    private Object createInput(TableField field) {
        return switch (field.type()) {
            case ENUM -> new JComboBox<>(field.options().toArray(String[]::new));
            case PASSWORD -> new PromptPasswordField(exampleTextFor(field));
            default -> new PromptTextField(exampleTextFor(field));
        };
    }

    private void refresh() {
        model.setRowCount(0);
        List<Map<String, Object>> rows = context.crudService().readAll(table.key());
        for (Map<String, Object> row : rows) {
            Object[] values = table.fields().stream()
                    .map(field -> row.get(field.name()))
                    .toArray();
            model.addRow(values);
        }
    }

    private void fillFormFromSelection() {
        int row = tableComponent.getSelectedRow();
        if (row < 0) {
            return;
        }
        for (int index = 0; index < table.fields().size(); index++) {
            TableField field = table.fields().get(index);
            Object value = model.getValueAt(row, index);
            Object component = inputs.get(field.name());
            if (component instanceof JTextField textField) {
                textField.setText(value == null ? "" : String.valueOf(value));
            } else if (component instanceof JPasswordField passwordField) {
                passwordField.setText(value == null ? "" : String.valueOf(value));
            } else if (component instanceof JComboBox<?> comboBox) {
                comboBox.setSelectedItem(value);
            }
        }
    }

    private void createRecord() {
        save(true);
    }

    private void updateRecord() {
        save(false);
    }

    private void save(boolean create) {
        try {
            Map<String, Object> values = collectValues();
            if (create) {
                context.crudService().create(table.key(), values, session.userId());
            } else {
                context.crudService().update(table.key(), values, session.userId());
            }
            refresh();
            JOptionPane.showMessageDialog(this, table.displayName() + " saved successfully.");
        } catch (AppException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        int row = tableComponent.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }
        Object id = model.getValueAt(row, 0);
        try {
            context.crudService().delete(table.key(), id, session.userId());
            refresh();
        } catch (AppException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Map<String, Object> collectValues() {
        Map<String, Object> values = new LinkedHashMap<>();
        for (TableField field : table.fields()) {
            Object component = inputs.get(field.name());
            Object value;
            if (component instanceof JTextField textField) {
                value = convert(field, textField.getText().trim());
            } else if (component instanceof JPasswordField passwordField) {
                value = new String(passwordField.getPassword()).trim();
            } else if (component instanceof JComboBox<?> comboBox) {
                value = comboBox.getSelectedItem();
            } else {
                value = null;
            }
            values.put(field.name(), value);
        }
        return values;
    }

    private JPanel createCard(BorderLayout layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createLineBorder(BORDER));
        return panel;
    }

    private String moduleDescription() {
        return switch (table.key()) {
            case "users" -> "Manage application users and role-based access for the subsystem.";
            case "accounts_payable" -> "Capture vendor invoices, due dates, taxes, and payment progress.";
            case "accounts_receivable" -> "Track customer invoices, payment status, and revenue recognition.";
            case "ledger_entries" -> "Post debit and credit entries to the general ledger.";
            case "assets" -> "Maintain asset value, category, and depreciation details.";
            case "cash_entries" -> "Record inflow, outflow, and running cash balances.";
            case "budgets" -> "Store department budgets, actual spend, and planning notes.";
            case "forecasts" -> "Enter projected revenue, expenses, and profit estimates.";
            case "tax_records" -> "Track tax base amounts, rates, liabilities, and filing status.";
            case "audit_logs" -> "View audit trail records generated automatically from create, update, and delete actions.";
            default -> "Manage financial subsystem data.";
        };
    }

    private String exampleFor(TableField field) {
        if ("accounts_payable".equals(table.key()) && "net_amount".equals(field.name())) {
            return "Auto-calculated from Amount and Tax %.";
        }
        if ("tax_records".equals(table.key()) && "tax_amount".equals(field.name())) {
            return "Auto-calculated from Base Amount and Tax Rate.";
        }
        if (field.type() == FieldType.ENUM) {
            return "Choose one of: " + String.join(", ", field.options());
        }
        if (!field.editable()) {
            return "Auto-generated or read-only field.";
        }
        return "Example: " + exampleTextFor(field);
    }

    private String exampleTextFor(TableField field) {
        return switch (table.key() + ":" + field.name()) {
            case "users:user_id" -> "U10";
            case "users:username" -> "finance_admin";
            case "users:password" -> "secure123";
            case "accounts_payable:invoice_id" -> "AP001";
            case "accounts_payable:vendor_name" -> "Dell India";
            case "accounts_payable:invoice_number" -> "DELL-1001";
            case "accounts_payable:invoice_date" -> "2026-04-01";
            case "accounts_payable:due_date" -> "2026-04-25";
            case "accounts_payable:amount" -> "45000";
            case "accounts_payable:tax_percent" -> "18";
            case "accounts_payable:net_amount" -> "53100";
            case "accounts_receivable:invoice_no" -> "AR001";
            case "accounts_receivable:customer_name" -> "ABC Retail";
            case "accounts_receivable:invoice_number" -> "ABC-9001";
            case "accounts_receivable:issue_date" -> "2026-04-03";
            case "accounts_receivable:amount" -> "72000";
            case "ledger_entries:entry_id" -> "GL001";
            case "ledger_entries:entry_date" -> "2026-04-03";
            case "ledger_entries:account_name" -> "Sales Revenue";
            case "ledger_entries:debit" -> "0";
            case "ledger_entries:credit" -> "72000";
            case "ledger_entries:balance" -> "72000";
            case "ledger_entries:description" -> "Invoice posted";
            case "assets:asset_id" -> "AS001";
            case "assets:asset_name" -> "Office Laptop";
            case "assets:category" -> "IT Equipment";
            case "assets:purchase_date" -> "2026-01-10";
            case "assets:purchase_value" -> "80000";
            case "assets:annual_depreciation" -> "16000";
            case "assets:current_value" -> "64000";
            case "cash_entries:entry_id" -> "CASH001";
            case "cash_entries:amount" -> "30000";
            case "cash_entries:balance" -> "30000";
            case "budgets:budget_id" -> "BUD001";
            case "budgets:department" -> "Finance";
            case "budgets:fiscal_year" -> "2026";
            case "budgets:budget_amount" -> "150000";
            case "budgets:actual_amount" -> "60000";
            case "budgets:notes" -> "Annual finance operations";
            case "forecasts:forecast_id" -> "FC001";
            case "forecasts:period" -> "Q2-2026";
            case "forecasts:revenue_growth" -> "12";
            case "forecasts:expense_growth" -> "8";
            case "forecasts:proj_revenue" -> "250000";
            case "forecasts:proj_expenses" -> "180000";
            case "forecasts:proj_profit" -> "70000";
            case "tax_records:tax_id" -> "TAX001";
            case "tax_records:tax_type" -> "GST";
            case "tax_records:applicable_period" -> "APR-2026";
            case "tax_records:base_amount" -> "100000";
            case "tax_records:tax_rate" -> "18";
            case "tax_records:tax_amount" -> "18000";
            case "tax_records:description" -> "Monthly GST";
            case "audit_logs:user_id" -> "U1";
            case "audit_logs:action" -> "CREATE";
            case "audit_logs:module" -> "Accounts Payable";
            case "audit_logs:details" -> "Created AP001";
            case "audit_logs:timestamp" -> "2026-04-22 12:30:00";
            default -> switch (field.type()) {
                case DATE -> "2026-04-22";
                case DECIMAL -> "1000";
                case INTEGER -> "1";
                case PASSWORD -> "secure123";
                case ENUM -> field.options().isEmpty() ? "" : field.options().get(0);
                default -> field.label();
            };
        };
    }

    private void enableAutoCalculation() {
        if ("accounts_payable".equals(table.key())) {
            JTextField amountField = textInput("amount");
            JTextField taxPercentField = textInput("tax_percent");
            JTextField netAmountField = textInput("net_amount");
            if (amountField != null && taxPercentField != null && netAmountField != null) {
                netAmountField.setEditable(false);
                netAmountField.setBackground(new Color(246, 249, 252));
                attachListener(amountField, this::recalculateNetAmount);
                attachListener(taxPercentField, this::recalculateNetAmount);
                recalculateNetAmount();
            }
        }

        if ("tax_records".equals(table.key())) {
            JTextField baseAmountField = textInput("base_amount");
            JTextField taxRateField = textInput("tax_rate");
            JTextField taxAmountField = textInput("tax_amount");
            if (baseAmountField != null && taxRateField != null && taxAmountField != null) {
                taxAmountField.setEditable(false);
                taxAmountField.setBackground(new Color(246, 249, 252));
                attachListener(baseAmountField, this::recalculateTaxAmount);
                attachListener(taxRateField, this::recalculateTaxAmount);
                recalculateTaxAmount();
            }
        }
    }

    private void recalculateNetAmount() {
        JTextField amountField = textInput("amount");
        JTextField taxPercentField = textInput("tax_percent");
        JTextField netAmountField = textInput("net_amount");
        if (amountField == null || taxPercentField == null || netAmountField == null) {
            return;
        }
        BigDecimal amount = decimalOrZero(amountField.getText());
        BigDecimal taxPercent = decimalOrZero(taxPercentField.getText());
        BigDecimal net = amount.add(amount.multiply(taxPercent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
        netAmountField.setText(formatDecimal(net));
    }

    private void recalculateTaxAmount() {
        JTextField baseAmountField = textInput("base_amount");
        JTextField taxRateField = textInput("tax_rate");
        JTextField taxAmountField = textInput("tax_amount");
        if (baseAmountField == null || taxRateField == null || taxAmountField == null) {
            return;
        }
        BigDecimal baseAmount = decimalOrZero(baseAmountField.getText());
        BigDecimal taxRate = decimalOrZero(taxRateField.getText());
        BigDecimal tax = baseAmount.multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        taxAmountField.setText(formatDecimal(tax));
    }

    private JTextField textInput(String fieldName) {
        Object component = inputs.get(fieldName);
        if (component instanceof JTextField textField) {
            return textField;
        }
        return null;
    }

    private void attachListener(JTextField field, Runnable action) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        });
    }

    private BigDecimal decimalOrZero(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            return BigDecimal.ZERO;
        }
    }

    private String formatDecimal(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    private Object convert(TableField field, String value) {
        if (value.isBlank()) {
            return switch (field.type()) {
                case DECIMAL -> "0";
                case INTEGER -> "";
                default -> value;
            };
        }
        return switch (field.type()) {
            case DATE -> LocalDate.parse(value);
            case DECIMAL -> value;
            case INTEGER -> Integer.parseInt(value);
            default -> value;
        };
    }
}
