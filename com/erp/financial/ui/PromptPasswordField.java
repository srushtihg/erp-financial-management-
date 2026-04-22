package com.erp.financial.ui;

import javax.swing.JPasswordField;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

public final class PromptPasswordField extends JPasswordField {
    private final String prompt;

    public PromptPasswordField(String prompt) {
        this.prompt = prompt;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (getPassword().length > 0 || prompt == null || prompt.isBlank()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(new Color(151, 163, 176));
        g2.setFont(getFont());
        Insets insets = getInsets();
        g2.drawString(prompt, insets.left + 4, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
        g2.dispose();
    }
}
