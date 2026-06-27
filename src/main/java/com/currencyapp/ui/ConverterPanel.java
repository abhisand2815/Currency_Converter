package com.currencyapp.ui;

import com.currencyapp.model.Currency;
import com.currencyapp.service.CurrencyService;
import com.currencyapp.util.NumberFormatter;
import com.currencyapp.ui.components.ModernUIFactory;
import com.currencyapp.ui.components.ModernUIFactory.RoundPanel;
import com.currencyapp.ui.components.ModernUIFactory.ModernButton;
import com.currencyapp.ui.components.ModernUIFactory.ModernTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ConverterPanel extends JPanel {
    private final ModernTextField amountField;
    private final ModernTextField fromFilterField;
    private final ModernTextField toFilterField;
    private final JComboBox<String> fromChoice;
    private final JComboBox<String> toChoice;
    private final JComboBox<String> precisionChoice;
    private final JCheckBox autoRefreshCheckbox;
    private final JLabel resultLabel;
    private final JLabel rateLabel;
    
    private final CurrencyService currencyService;
    private Timer refreshTimer;

    public ConverterPanel() {
        this.currencyService = CurrencyService.getInstance();
        
        setLayout(new GridBagLayout());
        setBackground(ModernUIFactory.COLOR_BG);
        GridBagConstraints gbcOuter = new GridBagConstraints();
        gbcOuter.fill = GridBagConstraints.BOTH;
        gbcOuter.weightx = 1.0;
        gbcOuter.weighty = 1.0;
        gbcOuter.insets = new Insets(10, 10, 10, 10);

        // Card Container
        RoundPanel cardPanel = new RoundPanel(16);
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        add(cardPanel, gbcOuter);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Header Title
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Real-Time Currency Converter", JLabel.CENTER);
        titleLabel.setFont(ModernUIFactory.FONT_TITLE);
        titleLabel.setForeground(ModernUIFactory.COLOR_PRIMARY);
        cardPanel.add(titleLabel, gbc);

        // Amount Input
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(ModernUIFactory.FONT_BODY_BOLD);
        amountLabel.setForeground(ModernUIFactory.COLOR_TEXT_MAIN);
        cardPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        amountField = new ModernTextField("1.00", 15);
        cardPanel.add(amountField, gbc);

        // From Currency Section
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel fromLabel = new JLabel("From Currency:");
        fromLabel.setFont(ModernUIFactory.FONT_BODY_BOLD);
        fromLabel.setForeground(ModernUIFactory.COLOR_TEXT_MAIN);
        cardPanel.add(fromLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPanel fromFilterPanel = new JPanel(new BorderLayout(10, 0));
        fromFilterPanel.setOpaque(false);
        fromFilterField = new ModernTextField("", 6);
        fromFilterField.setToolTipText("Filter by code/name...");
        fromChoice = ModernUIFactory.createComboBox();
        fromFilterPanel.add(fromFilterField, BorderLayout.WEST);
        fromFilterPanel.add(fromChoice, BorderLayout.CENTER);
        cardPanel.add(fromFilterPanel, gbc);

        // To Currency Section
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel toLabel = new JLabel("To Currency:");
        toLabel.setFont(ModernUIFactory.FONT_BODY_BOLD);
        toLabel.setForeground(ModernUIFactory.COLOR_TEXT_MAIN);
        cardPanel.add(toLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPanel toFilterPanel = new JPanel(new BorderLayout(10, 0));
        toFilterPanel.setOpaque(false);
        toFilterField = new ModernTextField("", 6);
        toFilterField.setToolTipText("Filter by code/name...");
        toChoice = ModernUIFactory.createComboBox();
        toFilterPanel.add(toFilterField, BorderLayout.WEST);
        toFilterPanel.add(toChoice, BorderLayout.CENTER);
        cardPanel.add(toFilterPanel, gbc);

        // Precision Configuration
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel precisionLabel = new JLabel("Decimal Precision:");
        precisionLabel.setFont(ModernUIFactory.FONT_BODY_BOLD);
        precisionLabel.setForeground(ModernUIFactory.COLOR_TEXT_MAIN);
        cardPanel.add(precisionLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        precisionChoice = ModernUIFactory.createComboBox();
        for (int i = 2; i <= 6; i++) {
            precisionChoice.addItem(i + " Decimal Places");
        }
        precisionChoice.setSelectedItem("4 Decimal Places"); // Default
        cardPanel.add(precisionChoice, gbc);

        // Action Buttons Row (Convert, Swap, Auto Refresh)
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        actionsPanel.setOpaque(false);
        
        ModernButton convertBtn = new ModernButton("Convert", true);
        ModernButton swapBtn = new ModernButton("Swap Currencies", false);

        autoRefreshCheckbox = new JCheckBox("Auto-Refresh (60s)");
        autoRefreshCheckbox.setFont(ModernUIFactory.FONT_BODY_BOLD);
        autoRefreshCheckbox.setForeground(ModernUIFactory.COLOR_TEXT_MUTED);
        autoRefreshCheckbox.setFocusPainted(false);
        autoRefreshCheckbox.setOpaque(false);

        actionsPanel.add(convertBtn);
        actionsPanel.add(swapBtn);
        actionsPanel.add(autoRefreshCheckbox);
        cardPanel.add(actionsPanel, gbc);

        // Conversion Result Display
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        
        // Wrap result in a beautiful custom badge
        RoundPanel resultCard = new RoundPanel(10);
        resultCard.setBackgroundColor(ModernUIFactory.COLOR_SUCCESS_BG);
        resultCard.setBorderColor(ModernUIFactory.COLOR_SUCCESS);
        resultCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        resultCard.setLayout(new BorderLayout());
        
        resultLabel = new JLabel("Enter amount and click Convert", JLabel.CENTER);
        resultLabel.setFont(ModernUIFactory.FONT_VALUE_LARGE);
        resultLabel.setForeground(ModernUIFactory.COLOR_SUCCESS_TEXT);
        resultCard.add(resultLabel, BorderLayout.CENTER);
        
        cardPanel.add(resultCard, gbc);

        // Live Rate Display Label
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        rateLabel = new JLabel("Select base and convert to see live rates.", JLabel.CENTER);
        rateLabel.setFont(ModernUIFactory.FONT_BODY);
        rateLabel.setForeground(ModernUIFactory.COLOR_TEXT_MUTED);
        cardPanel.add(rateLabel, gbc);

        // Initialize choices
        refreshChoices("");

        // Setup Filter Actions
        DocumentListener fromFilterListener = new DocumentListener() {
            private void update() {
                filterChoice(fromChoice, fromFilterField.getText(), "USD");
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        };
        fromFilterField.getDocument().addDocumentListener(fromFilterListener);

        DocumentListener toFilterListener = new DocumentListener() {
            private void update() {
                filterChoice(toChoice, toFilterField.getText(), "EUR");
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        };
        toFilterField.getDocument().addDocumentListener(toFilterListener);

        // Set default selections
        setChoiceSelection(fromChoice, "USD");
        setChoiceSelection(toChoice, "EUR");

        // Set up action listeners
        convertBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });

        swapBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSwap();
            }
        });

        autoRefreshCheckbox.addActionListener(e -> {
            if (autoRefreshCheckbox.isSelected()) {
                startAutoRefreshTimer();
            } else {
                stopAutoRefreshTimer();
            }
        });
    }

    private void refreshChoices(String filter) {
        List<Currency> list = currencyService.getCurrencies();
        
        fromChoice.removeAllItems();
        toChoice.removeAllItems();
        for (Currency c : list) {
            fromChoice.addItem(c.toString());
            toChoice.addItem(c.toString());
        }
    }

    private void filterChoice(JComboBox<String> choice, String filter, String defaultCode) {
        String currentSelected = (String) choice.getSelectedItem();
        String selectedCode = currentSelected != null ? currentSelected.substring(0, 3) : defaultCode;
        
        choice.removeAllItems();
        List<Currency> list = currencyService.getCurrencies();
        for (Currency c : list) {
            if (filter.isEmpty() || c.toString().toLowerCase().contains(filter.toLowerCase())) {
                choice.addItem(c.toString());
            }
        }
        
        // Re-select if possible
        setChoiceSelection(choice, selectedCode);
        if (choice.getItemCount() > 0 && choice.getSelectedIndex() < 0) {
            choice.setSelectedIndex(0);
        }
    }

    private void setChoiceSelection(JComboBox<String> choice, String code) {
        for (int i = 0; i < choice.getItemCount(); i++) {
            if (choice.getItemAt(i).startsWith(code)) {
                choice.setSelectedIndex(i);
                return;
            }
        }
    }

    private void performConversion() {
        String fromItem = (String) fromChoice.getSelectedItem();
        String toItem = (String) toChoice.getSelectedItem();

        if (fromItem == null || toItem == null) {
            resultLabel.setText("Please select currencies");
            return;
        }

        String fromCode = fromItem.substring(0, 3);
        String toCode = toItem.substring(0, 3);
        
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount < 0) {
                resultLabel.setText("Amount cannot be negative");
                return;
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Invalid amount format");
            return;
        }

        resultLabel.setText("Fetching rates...");

        int precision = precisionChoice.getSelectedIndex() + 2;

        currencyService.getRates(fromCode, new CurrencyService.RateCallback() {
            @Override
            public void onResult(Map<String, Double> rates, LocalDateTime timestamp, boolean isLive) {
                EventQueue.invokeLater(() -> {
                    Double rate = rates.get(toCode);
                    if (rate != null) {
                        double result = amount * rate;
                        String formattedResult = NumberFormatter.formatRate(result, precision);
                        String formattedRate = NumberFormatter.formatRate(rate, precision);
                        
                        resultLabel.setText(amountField.getText().trim() + " " + fromCode + " = " + formattedResult + " " + toCode);
                        rateLabel.setText("1 " + fromCode + " = " + formattedRate + " " + toCode + " (" + (isLive ? "Live" : "Cached") + ")");
                    } else {
                        resultLabel.setText("Unsupported target currency: " + toCode);
                    }
                });
            }

            @Override
            public void onError(String message) {
                EventQueue.invokeLater(() -> resultLabel.setText("Error: " + message));
            }
        });
    }

    private void performSwap() {
        String fromItem = (String) fromChoice.getSelectedItem();
        String toItem = (String) toChoice.getSelectedItem();

        if (fromItem == null || toItem == null) return;

        String fromCode = fromItem.substring(0, 3);
        String toCode = toItem.substring(0, 3);

        // Reset filters to ensure the full list is available for selections
        fromFilterField.setText("");
        toFilterField.setText("");
        
        filterChoice(fromChoice, "", fromCode);
        filterChoice(toChoice, "", toCode);

        // Select swapped values
        setChoiceSelection(fromChoice, toCode);
        setChoiceSelection(toChoice, fromCode);

        // Trigger conversion
        performConversion();
    }

    private synchronized void startAutoRefreshTimer() {
        stopAutoRefreshTimer();
        refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                EventQueue.invokeLater(() -> performConversion());
            }
        }, 60000, 60000);
    }

    private synchronized void stopAutoRefreshTimer() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }
    }

    public void cleanup() {
        stopAutoRefreshTimer();
    }
}
