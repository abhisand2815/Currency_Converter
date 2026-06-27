package com.currencyapp.ui;

import com.currencyapp.service.CurrencyService;
import com.currencyapp.util.NumberFormatter;
import com.currencyapp.ui.components.ModernUIFactory;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
    private final StatusDot dot;
    private final JLabel statusLabel;
    private final JLabel timeLabel;

    public StatusBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 8));
        setBackground(ModernUIFactory.COLOR_SECONDARY);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUIFactory.COLOR_BORDER));

        dot = new StatusDot();
        statusLabel = new JLabel("Initializing...");
        statusLabel.setFont(ModernUIFactory.FONT_BODY_BOLD);

        JLabel divider = new JLabel("|");
        divider.setForeground(ModernUIFactory.COLOR_BORDER);

        timeLabel = new JLabel("Last Updated: N/A");
        timeLabel.setFont(ModernUIFactory.FONT_BODY);
        timeLabel.setForeground(ModernUIFactory.COLOR_TEXT_MUTED);

        add(dot);
        add(statusLabel);
        add(divider);
        add(timeLabel);

        // Register listener for status updates
        CurrencyService.getInstance().addStatusListener(this::updateStatus);
        
        // Initial update
        updateStatus();
    }

    public void updateStatus() {
        EventQueue.invokeLater(() -> {
            boolean online = CurrencyService.getInstance().isOnline();
            dot.setOnline(online);
            statusLabel.setText(online ? "API: Live" : "API: Offline (fallback rates)");
            statusLabel.setForeground(online ? ModernUIFactory.COLOR_SUCCESS_TEXT : ModernUIFactory.COLOR_ERROR_TEXT);
            
            var lastUpdate = CurrencyService.getInstance().getLastUpdated();
            timeLabel.setText("Last Updated: " + NumberFormatter.formatDateTime(lastUpdate));
            
            revalidate();
            repaint();
        });
    }

    private static class StatusDot extends JComponent {
        private boolean online = false;

        public StatusDot() {
            // Set size for layout manager
            setPreferredSize(new Dimension(10, 10));
        }

        public void setOnline(boolean online) {
            this.online = online;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int d = 10;
            // Draw a subtle outer shadow or border for the status indicator
            if (online) {
                g2d.setColor(ModernUIFactory.COLOR_SUCCESS);
            } else {
                g2d.setColor(ModernUIFactory.COLOR_ERROR);
            }
            g2d.fillOval(0, 0, d, d);
            
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.drawOval(0, 0, d - 1, d - 1);
            g2d.dispose();
        }
    }
}
