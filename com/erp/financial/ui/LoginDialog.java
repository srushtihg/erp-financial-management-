package com.erp.financial.ui;

import com.erp.financial.domain.UserSession;
import com.erp.financial.exception.AppException;
import com.erp.financial.service.AppContext;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

public final class LoginDialog extends JDialog {
    private final AppContext context;
    private final JTextField usernameField = new JTextField("admin");
    private final JPasswordField passwordField = new JPasswordField("admin123");
    private UserSession session;

    public LoginDialog(AppContext context) {
        super((Frame) null, "Financial Management Login", true);
        this.context = context;
        setSize(360, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUi();
    }

    public UserSession showDialog() {
        setVisible(true);
        return session;
    }

    private void buildUi() {
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.add(new JLabel("Username"));
        form.add(usernameField);
        form.add(new JLabel("Password"));
        form.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(event -> authenticate());

        add(form, BorderLayout.CENTER);
        add(loginButton, BorderLayout.SOUTH);
    }

    private void authenticate() {
        try {
            session = context.authService().login(usernameField.getText().trim(), new String(passwordField.getPassword()));
            context.auditService().log(session.userId(), "LOGIN", "Authentication", "User logged in");
            dispose();
        } catch (AppException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}

