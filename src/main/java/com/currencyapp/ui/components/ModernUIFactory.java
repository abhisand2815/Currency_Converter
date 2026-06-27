package com.currencyapp.ui.components;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernUIFactory {

    // Global Modern Color Palette (Tailwind-inspired)
    public static final Color COLOR_BG = new Color(249, 250, 251);         // #F9FAFB (slate gray-50)
    public static final Color COLOR_CARD_BG = Color.WHITE;
    public static final Color COLOR_BORDER = new Color(229, 231, 235);     // #E5E7EB (gray-200)
    public static final Color COLOR_TEXT_MAIN = new Color(17, 24, 39);     // #111827 (gray-900)
    public static final Color COLOR_TEXT_MUTED = new Color(107, 114, 128); // #6B7280 (gray-500)
    
    public static final Color COLOR_PRIMARY = new Color(59, 130, 246);      // #3B82F6 (blue-500)
    public static final Color COLOR_PRIMARY_HOVER = new Color(37, 99, 235); // #2563EB (blue-600)
    public static final Color COLOR_PRIMARY_PRESS = new Color(29, 78, 216); // #1D4ED8 (blue-700)

    public static final Color COLOR_SECONDARY = new Color(243, 244, 246);      // #F3F4F6 (gray-100)
    public static final Color COLOR_SECONDARY_HOVER = new Color(229, 231, 235); // #E5E7EB (gray-200)
    public static final Color COLOR_SECONDARY_PRESS = new Color(209, 213, 219); // #D1D5DB (gray-300)

    public static final Color COLOR_SUCCESS = new Color(16, 185, 129);       // #10B981 (emerald-500)
    public static final Color COLOR_SUCCESS_BG = new Color(209, 250, 229);    // #D1FAE5 (emerald-100)
    public static final Color COLOR_SUCCESS_TEXT = new Color(4, 120, 87);     // #047857 (emerald-700)

    public static final Color COLOR_ERROR = new Color(239, 68, 68);         // #EF4444 (red-500)
    public static final Color COLOR_ERROR_BG = new Color(254, 226, 226);     // #FEE2E2 (red-100)
    public static final Color COLOR_ERROR_TEXT = new Color(185, 28, 28);     // #B91C1C (red-700)

    // Modern Fonts
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_VALUE_LARGE = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_VALUE_SMALL = new Font("SansSerif", Font.BOLD, 14);

    /**
     * Card-like panel container with customizable rounded corners.
     */
    public static class RoundPanel extends JPanel {
        private int cornerRadius = 12;
        private Color backgroundColor = COLOR_CARD_BG;
        private Color borderColor = COLOR_BORDER;
        private int borderWidth = 1;

        public RoundPanel(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
        }

        public RoundPanel() {
            this(12);
        }

        public void setBackgroundColor(Color bg) {
            this.backgroundColor = bg;
            repaint();
        }

        public void setBorderColor(Color border) {
            this.borderColor = border;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Paint background
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

            // Paint border
            if (borderColor != null && borderWidth > 0) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(borderWidth));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            }
            g2.dispose();
        }
    }

    /**
     * Modern flat button with custom hover / pressed colors and rounded corners.
     */
    public static class ModernButton extends JButton {
        private final Color bgNormal;
        private final Color bgHover;
        private final Color bgPress;
        private final Color textNormal;
        private int radius = 8;
        private boolean isTabButton = false;
        private boolean isTabActive = false;

        public ModernButton(String text, boolean primary) {
            super(text);
            this.bgNormal = primary ? COLOR_PRIMARY : COLOR_SECONDARY;
            this.bgHover = primary ? COLOR_PRIMARY_HOVER : COLOR_SECONDARY_HOVER;
            this.bgPress = primary ? COLOR_PRIMARY_PRESS : COLOR_SECONDARY_PRESS;
            this.textNormal = primary ? Color.WHITE : COLOR_TEXT_MAIN;

            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(textNormal);
            setFont(FONT_BODY_BOLD);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Small padding border
            setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    repaint();
                }
            });
        }

        public ModernButton(String text) {
            this(text, true);
        }

        public static ModernButton createTabButton(String text) {
            ModernButton btn = new ModernButton(text, false);
            btn.isTabButton = true;
            btn.setFont(FONT_BODY_BOLD);
            btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
            return btn;
        }

        public void setTabActive(boolean active) {
            this.isTabActive = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            ButtonModel model = getModel();

            if (isTabButton) {
                if (isTabActive) {
                    g2.setColor(COLOR_PRIMARY);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Pill background
                    setForeground(Color.WHITE);
                } else {
                    if (model.isRollover()) {
                        g2.setColor(COLOR_SECONDARY_HOVER);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    }
                    setForeground(COLOR_TEXT_MUTED);
                }
            } else {
                if (model.isPressed()) {
                    g2.setColor(bgPress);
                } else if (model.isRollover()) {
                    g2.setColor(bgHover);
                } else {
                    g2.setColor(bgNormal);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                setForeground(textNormal);
            }

            super.paintComponent(g);
            g2.dispose();
        }
    }

    /**
     * Modern text field with padded inset border and focus color transitions.
     */
    public static class ModernTextField extends JTextField {
        private int radius = 8;
        private Color borderColor = COLOR_BORDER;
        private Color focusColor = COLOR_PRIMARY;

        public ModernTextField(String text, int columns) {
            super(text, columns);
            setOpaque(false);
            setFont(FONT_BODY);
            setForeground(COLOR_TEXT_MAIN);
            setCaretColor(COLOR_TEXT_MAIN);
            
            // Inset margins so text is clean
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }
                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        public ModernTextField(String text) {
            this(text, 10);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Paint background
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            // Paint border
            if (isFocusOwner()) {
                g2.setColor(focusColor);
                g2.setStroke(new BasicStroke(1.5f));
            } else {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    /**
     * Helper to style a combo box with rounded edges and clean item rendering.
     */
    public static JComboBox<String> createComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(FONT_BODY);
        combo.setBackground(Color.WHITE);
        combo.setForeground(COLOR_TEXT_MAIN);
        
        // Clean up borders
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));

        // Use custom renderer for item padding and selection background color
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                if (isSelected) {
                    label.setBackground(COLOR_PRIMARY);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(COLOR_TEXT_MAIN);
                }
                return label;
            }
        });
        
        return combo;
    }

    /**
     * Helper to create trend badges (Up/Down/No Change status tabs)
     */
    public static JPanel createTrendBadge(String text, double change) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(FONT_BODY_BOLD);
        
        if (change > 0.0001) {
            badge.setBackground(COLOR_SUCCESS_BG);
            label.setForeground(COLOR_SUCCESS_TEXT);
        } else if (change < -0.0001) {
            badge.setBackground(COLOR_ERROR_BG);
            label.setForeground(COLOR_ERROR_TEXT);
        } else {
            badge.setBackground(COLOR_SECONDARY);
            label.setForeground(COLOR_TEXT_MUTED);
        }
        
        badge.add(label);
        return badge;
    }
}
