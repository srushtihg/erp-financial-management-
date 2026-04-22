package com.erp.financial.ui;

import com.erp.financial.domain.TableDefinition;
import com.erp.financial.domain.UserSession;
import com.erp.financial.service.AppContext;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Map;

public final class MainFrame extends JFrame {
    public MainFrame(AppContext context, UserSession session) {
        super("ERP Financial Management Subsystem - " + session.username() + " (" + session.role() + ")");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1460, 860);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(243, 246, 251));

        add(buildTopBanner(session), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard", new DashboardPanel(context));

        for (Map.Entry<String, TableDefinition> entry : context.tables().entrySet()) {
            tabs.addTab(entry.getValue().displayName(), new ManagementPanel(context, session, entry.getValue()));
        }

        tabs.addTab("Reports", new ReportPanel(context));
        tabs.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTopBanner(UserSession session) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 252, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(219, 227, 236)),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel title = new JLabel("ERP Financial Management Subsystem");
        title.setForeground(new Color(26, 43, 60));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JLabel sessionLabel = new JLabel("Logged in as " + session.username() + " | Role: " + session.role());
        sessionLabel.setForeground(new Color(105, 121, 138));
        sessionLabel.setFont(sessionLabel.getFont().deriveFont(Font.PLAIN, 13f));

        panel.add(title, BorderLayout.WEST);
        panel.add(sessionLabel, BorderLayout.EAST);
        return panel;
    }
}
