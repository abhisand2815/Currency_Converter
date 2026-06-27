package com.currencyapp.ui;

import com.currencyapp.model.Currency;
import com.currencyapp.model.RateSnapshot;
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

public class ComparePanel extends JPanel {
    private final JComboBox<String> baseChoice;
    private final ModernTextField baseFilter;
    private final JComboBox<String>[] targetChoices = new JComboBox[5];
    private final ModernTextField[] targetFilters = new ModernTextField[5];

    // Grid Table Labels
    private final JLabel[] tableCodes = new JLabel[5];
    private final JLabel[] tableNames = new JLabel[5];
    private final JLabel[] tableRates = new JLabel[5];
    private final JPanel[] tableChangesContainer = new JPanel[5];

    private final CurrencyService currencyService;

    public ComparePanel() {
        this.currencyService = CurrencyService.getInstance();
        setLayout(new BorderLayout(15, 15));
        setBackground(ModernUIFactory.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Top Base Selector Card Panel
        RoundPanel topPanel = new RoundPanel(12);
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 12));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel baseLabel = new JLabel("Base Currency:");
        baseLabel.setFont(ModernUIFactory.FONT_BODY_BOLD);
        baseLabel.setForeground(ModernUIFactory.COLOR_TEXT_MAIN);
        
        baseFilter = new ModernTextField("", 5);
        baseChoice = ModernUIFactory.createComboBox();

        topPanel.add(baseLabel);
        topPanel.add(baseFilter);
        topPanel.add(baseChoice);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel holding target selectors & comparison table
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        // Target Selectors row card
        RoundPanel targetsCard = new RoundPanel(12);
        targetsCard.setLayout(new GridLayout(1, 5, 15, 0));
        targetsCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        String[] defaultTargets = {"EUR", "GBP", "INR", "JPY", "CAD"};
        List<Currency> currencies = currencyService.getCurrencies();

        for (int i = 0; i < 5; i++) {
            JPanel targetCol = new JPanel(new GridBagLayout());
            targetCol.setOpaque(false);
            
            targetFilters[i] = new ModernTextField("", 4);
            targetFilters[i].setToolTipText("Filter...");
            targetChoices[i] = ModernUIFactory.createComboBox();
            
            // Populate targets
            for (Currency c : currencies) {
                targetChoices[i].addItem(c.getCode());
            }
            
            // Choose defaults
            setChoiceSelection(targetChoices[i], defaultTargets[i]);

            // Add filter listener
            final int index = i;
            DocumentListener dl = new DocumentListener() {
                private void update() {
                    filterChoice(targetChoices[index], targetFilters[index].getText(), defaultTargets[index], false);
                }
                public void insertUpdate(DocumentEvent e) { update(); }
                public void removeUpdate(DocumentEvent e) { update(); }
                public void changedUpdate(DocumentEvent e) { update(); }
            };
            targetFilters[i].getDocument().addDocumentListener(dl);

            JLabel title = new JLabel("Target " + (i + 1), JLabel.CENTER);
            title.setFont(new Font("SansSerif", Font.BOLD, 11));
            title.setForeground(ModernUIFactory.COLOR_TEXT_MUTED);
            
            GridBagConstraints colGbc = new GridBagConstraints();
            colGbc.fill = GridBagConstraints.HORIZONTAL;
            colGbc.gridx = 0; colGbc.weightx = 1.0;
            colGbc.insets = new Insets(2, 0, 2, 0);
            
            colGbc.gridy = 0;
            targetCol.add(title, colGbc);
            colGbc.gridy = 1;
            targetCol.add(targetFilters[i], colGbc);
            colGbc.gridy = 2;
            targetCol.add(targetChoices[i], colGbc);
            
            targetsCard.add(targetCol);
        }

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weighty = 0.2;
        centerPanel.add(targetsCard, gbc);

        // Table Card Panel
        RoundPanel tableCard = new RoundPanel(12);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel tablePanel = new JPanel(new GridLayout(6, 4, 10, 10));
        tablePanel.setOpaque(false);

        // Header Labels
        tablePanel.add(createHeaderLabel("CURRENCY CODE"));
        tablePanel.add(createHeaderLabel("CURRENCY NAME"));
        tablePanel.add(createHeaderLabel("EXCHANGE RATE"));
        tablePanel.add(createHeaderLabel("24H CHANGE"));

        // Rows
        for (int i = 0; i < 5; i++) {
            tableCodes[i] = createDataLabel("", true);
            tableNames[i] = createDataLabel("", false);
            tableRates[i] = createDataLabel("", true);
            
            tableChangesContainer[i] = new JPanel(new BorderLayout());
            tableChangesContainer[i].setOpaque(false);

            tablePanel.add(tableCodes[i]);
            tablePanel.add(tableNames[i]);
            tablePanel.add(tableRates[i]);
            tablePanel.add(tableChangesContainer[i]);
        }

        tableCard.add(tablePanel, BorderLayout.CENTER);

        gbc.gridy = 1;
        gbc.weighty = 0.8;
        centerPanel.add(tableCard, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Actions Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setOpaque(false);
        ModernButton refreshBtn = new ModernButton("Compare & Refresh", true);
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Populate base choice
        for (Currency c : currencies) {
            baseChoice.addItem(c.toString());
        }
        setChoiceSelection(baseChoice, "USD");

        // Filter listener for base
        DocumentListener baseFilterListener = new DocumentListener() {
            private void update() {
                filterChoice(baseChoice, baseFilter.getText(), "USD", true);
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        };
        baseFilter.getDocument().addDocumentListener(baseFilterListener);

        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performComparison();
            }
        });

        // Run initial comparison
        performComparison();
    }

    private JLabel createHeaderLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(ModernUIFactory.COLOR_TEXT_MUTED);
        return l;
    }

    private JLabel createDataLabel(String text, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(bold ? ModernUIFactory.FONT_BODY_BOLD : ModernUIFactory.FONT_BODY);
        l.setForeground(ModernUIFactory.COLOR_TEXT_MAIN);
        return l;
    }

    private void filterChoice(JComboBox<String> choice, String filter, String defaultCode, boolean useFullString) {
        String currentSelected = (String) choice.getSelectedItem();
        String selectedCode = currentSelected != null ? (currentSelected.length() >= 3 ? currentSelected.substring(0, 3) : currentSelected) : defaultCode;

        choice.removeAllItems();
        List<Currency> list = currencyService.getCurrencies();
        for (Currency c : list) {
            if (filter.isEmpty() || c.toString().toLowerCase().contains(filter.toLowerCase())) {
                choice.addItem(useFullString ? c.toString() : c.getCode());
            }
        }
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

    private void performComparison() {
        String baseItem = (String) baseChoice.getSelectedItem();
        if (baseItem == null) return;
        String baseCode = baseItem.substring(0, 3);

        currencyService.getRates(baseCode, new CurrencyService.RateCallback() {
            @Override
            public void onResult(Map<String, Double> rates, LocalDateTime timestamp, boolean isLive) {
                EventQueue.invokeLater(() -> {
                    for (int i = 0; i < 5; i++) {
                        String targetItem = (String) targetChoices[i].getSelectedItem();
                        if (targetItem == null) {
                            clearRow(i);
                            continue;
                        }

                        String targetCode = targetItem.length() >= 3 ? targetItem.substring(0, 3) : targetItem;
                        String targetName = "";
                        for (Currency c : currencyService.getCurrencies()) {
                            if (c.getCode().equals(targetCode)) {
                                targetName = c.getName();
                                break;
                            }
                        }

                        Double rate = rates.get(targetCode);
                        if (rate != null) {
                            tableCodes[i].setText(targetCode);
                            tableNames[i].setText(targetName);
                            tableRates[i].setText(NumberFormatter.formatRate(rate, 4));

                            // Generate 2-day simulation to check change indicator
                            List<RateSnapshot> sim = currencyService.getSimulatedHistory(baseCode, targetCode, rate);
                            double yesterdayRate = sim.get(sim.size() - 2).getRate();
                            double change = rate - yesterdayRate;

                            tableChangesContainer[i].removeAll();
                            if (change > 0.0001) {
                                String text = String.format("▲ Up (+%.2f%%)", (change / yesterdayRate) * 100.0);
                                tableChangesContainer[i].add(ModernUIFactory.createTrendBadge(text, change));
                            } else if (change < -0.0001) {
                                String text = String.format("▼ Down (%.2f%%)", (change / yesterdayRate) * 100.0);
                                tableChangesContainer[i].add(ModernUIFactory.createTrendBadge(text, change));
                            } else {
                                tableChangesContainer[i].add(ModernUIFactory.createTrendBadge("— No Change", 0));
                            }
                            tableChangesContainer[i].revalidate();
                            tableChangesContainer[i].repaint();
                        } else {
                            clearRow(i);
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                EventQueue.invokeLater(() -> {
                    for (int i = 0; i < 5; i++) {
                        tableRates[i].setText("Error fetching");
                        tableChangesContainer[i].removeAll();
                        tableChangesContainer[i].add(ModernUIFactory.createTrendBadge("N/A", 0));
                        tableChangesContainer[i].revalidate();
                        tableChangesContainer[i].repaint();
                    }
                });
            }
        });
    }

    private void clearRow(int index) {
        tableCodes[index].setText("N/A");
        tableNames[index].setText("N/A");
        tableRates[index].setText("N/A");
        tableChangesContainer[index].removeAll();
        tableChangesContainer[index].add(ModernUIFactory.createTrendBadge("N/A", 0));
        tableChangesContainer[index].revalidate();
        tableChangesContainer[index].repaint();
    }
}
