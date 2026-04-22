package com.erp.financial.ui;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public final class SimpleBarChartPanel extends JPanel {
    private static final Color TEXT_PRIMARY = new Color(26, 43, 60);
    private static final Color TEXT_MUTED = new Color(104, 120, 139);
    private static final Color TRACK = new Color(232, 238, 244);
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.of("en", "IN"));
    private static final NumberFormat NUMBER = NumberFormat.getNumberInstance(Locale.of("en", "IN"));

    private final String title;
    private final String subtitle;
    private final String emptyMessage;
    private final boolean currencyMode;
    private List<BarValue> values = List.of();

    public SimpleBarChartPanel(String title, String subtitle, String emptyMessage, boolean currencyMode) {
        this.title = title;
        this.subtitle = subtitle;
        this.emptyMessage = emptyMessage;
        this.currencyMode = currencyMode;
        setOpaque(false);
    }

    public void setValues(List<BarValue> values) {
        this.values = values;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(new Color(255, 255, 255, 245));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
        g2.setColor(new Color(219, 227, 236));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);

        g2.setFont(getFont().deriveFont(Font.BOLD, 18f));
        g2.setColor(TEXT_PRIMARY);
        g2.drawString(title, 20, 32);

        g2.setFont(getFont().deriveFont(Font.PLAIN, 12f));
        g2.setColor(TEXT_MUTED);
        g2.drawString(subtitle, 20, 52);

        if (values.isEmpty() || values.stream().allMatch(value -> value.value().compareTo(BigDecimal.ZERO) <= 0)) {
            drawEmptyState(g2);
            g2.dispose();
            return;
        }

        int labelWidth = 108;
        int top = 78;
        int left = 20;
        int right = getWidth() - 22;
        int barHeight = 18;
        int gap = 18;

        BigDecimal max = values.stream()
                .map(BarValue::value)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ONE);

        FontMetrics metrics = g2.getFontMetrics();
        for (int index = 0; index < values.size(); index++) {
            BarValue item = values.get(index);
            int y = top + index * (barHeight + gap);

            g2.setColor(TEXT_PRIMARY);
            g2.drawString(item.label(), left, y + 13);

            int barX = left + labelWidth;
            int barWidth = Math.max(40, right - barX - 70);
            g2.setColor(TRACK);
            g2.fillRoundRect(barX, y, barWidth, barHeight, 14, 14);

            double ratio = item.value().divide(max, 4, RoundingMode.HALF_UP).doubleValue();
            int fillWidth = Math.max(6, (int) (barWidth * ratio));
            g2.setColor(item.color());
            g2.fillRoundRect(barX, y, fillWidth, barHeight, 14, 14);

            String valueText = format(item.value());
            g2.setColor(TEXT_MUTED);
            g2.drawString(valueText, right - metrics.stringWidth(valueText), y + 13);
        }

        g2.dispose();
    }

    private void drawEmptyState(Graphics2D g2) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2 + 12;
        g2.setColor(new Color(236, 242, 247));
        g2.fillRoundRect(centerX - 90, centerY - 28, 180, 16, 12, 12);
        g2.fillRoundRect(centerX - 90, centerY, 140, 16, 12, 12);
        g2.fillRoundRect(centerX - 90, centerY + 28, 104, 16, 12, 12);

        g2.setFont(getFont().deriveFont(Font.BOLD, 15f));
        g2.setColor(TEXT_PRIMARY);
        g2.drawString("No chart data yet", centerX - 57, centerY - 52);

        g2.setFont(getFont().deriveFont(Font.PLAIN, 12f));
        g2.setColor(TEXT_MUTED);
        drawCenteredText(g2, emptyMessage, centerX, centerY + 70);
    }

    private void drawCenteredText(Graphics2D g2, String text, int centerX, int baselineY) {
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(text, centerX - metrics.stringWidth(text) / 2, baselineY);
    }

    private String format(BigDecimal value) {
        return currencyMode ? CURRENCY.format(value) : NUMBER.format(value);
    }

    public record BarValue(String label, BigDecimal value, Color color) {
    }
}
