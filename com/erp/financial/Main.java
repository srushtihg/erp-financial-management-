package com.erp.financial;

import com.erp.financial.config.DatabaseConfig;
import com.erp.financial.db.ConnectionFactory;
import com.erp.financial.db.DatabaseBootstrap;
import com.erp.financial.domain.UserSession;
import com.erp.financial.service.AppContext;
import com.erp.financial.ui.LoginDialog;
import com.erp.financial.ui.MainFrame;

import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseConfig config = DatabaseConfig.load();
            ConnectionFactory connectionFactory = new ConnectionFactory(config);
            new DatabaseBootstrap(connectionFactory, config).initialize();

            AppContext context = new AppContext(connectionFactory);
            LoginDialog loginDialog = new LoginDialog(context);
            UserSession session = loginDialog.showDialog();
            if (session == null) {
                return;
            }

            MainFrame frame = new MainFrame(context, session);
            frame.setVisible(true);
        });
    }
}

