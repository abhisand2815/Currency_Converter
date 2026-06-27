package com.currencyapp.ui;

import com.currencyapp.model.Currency;
import com.currencyapp.model.RateSnapshot;
import com.currencyapp.service.CurrencyService;
import com.currencyapp.util.NumberFormatter;
import com.currencyapp.ui.components.ModernUIFactory;
import com.currencyapp.ui.components.ModernUIFactory.RoundPanel;
import com.currencyapp.ui.components.ModernUIFactory.ModernButton;
import com.currencyapp.ui.components.ModernUIFactory.ModernTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ChartPanel extends JPanel {
    private final JComboBox<String> fromChoice;
    private final JComboBox<String> toChoice;
    private final ModernTextField fromFilter;
    private final ModernTextField toFilter;
    private final org.jfree.chart.ChartPanel chartPanelComponent;
    
    private final JLabel currentRateVal;
    private final JLabel highVal;
    private final JLabel lowVal;
    private final JLabel changeVal;

    private final CurrencyService currencyService;

    public ChartPanel() {
        this.currencyService = CurrencyService.getInstance();
        setLayout(new BorderLayout(15, 15));
        setBackground(ModernUIFactory.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Top Control Panel (Padded Card)
        RoundPanel controls = new RoundPanel(12);
        controls.setLayout(new GridBagLayout());
        controls.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel fromLabel = new JLabel("Base:");
        fromLabel.setFont(ModernUIFactory.FONT_BODY_BOLD);
        fromLabel.setForeground(ModernUIFactory.COLOR_TEXT_MAIN);
        
        fromFilter = new ModernTextField("", 5);
        fromChoice = ModernUIFactory.createComboBox();
        
        JLabel toLabel = new JLabel("Target:");
        toLabel.setFont(ModernUIFactory.FONT_BODY_BOLD);
        toLabel.setForeground(ModernUIFactory.COLOR_TEXT_MAIN);
        
        toFilter = new ModernTextField("", 5);
        toChoice = ModernUIFactory.createComboBox();

        ModernButton generateBtn = new ModernButton("Show Trend", true);
        generateBtn.setFont(ModernUIFactory.FONT_BODY_BOLD);

        // Row 0: Base
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.1;
        controls.add(fromLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.2;
        controls.add(fromFilter, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.4;
        controls.add(fromChoice, gbc);

        // Row 1: Target
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.1;
        controls.add(toLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.2;
        controls.add(toFilter, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.4;
        controls.add(toChoice, gbc);

        // Span generate button across both rows
        gbc.gridx = 3; gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(6, 15, 6, 5);
        controls.add(generateBtn, gbc);
        add(controls, BorderLayout.NORTH);

        // Center Chart Display Card
        RoundPanel chartCard = new RoundPanel(12);
        chartCard.setLayout(new BorderLayout());
        chartCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Initialize JFreeChart Panel as a placeholder or clean layout
        chartPanelComponent = new org.jfree.chart.ChartPanel(null);
        chartPanelComponent.setBackground(Color.WHITE);
        chartPanelComponent.setOpaque(true);
        chartPanelComponent.setDisplayToolTips(true);
        
        // We render a custom placeholder inside a component that overrides paint
        JPanel placeholderPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(ModernUIFactory.COLOR_TEXT_MUTED);
                g.setFont(ModernUIFactory.FONT_BODY);
                String txt = "Select currencies and click 'Show Trend' to generate chart.";
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(txt)) / 2;
                int y = getHeight() / 2;
                g.drawString(txt, x, y);
            }
        };
        placeholderPanel.setBackground(Color.WHITE);
        chartCard.add(placeholderPanel, BorderLayout.CENTER);
        add(chartCard, BorderLayout.CENTER);

        // Bottom Stats Panel (Horizontal Row of cards)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 75));

        RoundPanel p1 = createStatSubCard("Current Rate", currentRateVal = new JLabel("N/A", JLabel.CENTER));
        RoundPanel p2 = createStatSubCard("7-Day High", highVal = new JLabel("N/A", JLabel.CENTER));
        RoundPanel p3 = createStatSubCard("7-Day Low", lowVal = new JLabel("N/A", JLabel.CENTER));
        RoundPanel p4 = createStatSubCard("% Change (7D)", changeVal = new JLabel("N/A", JLabel.CENTER));

        statsPanel.add(p1);
        statsPanel.add(p2);
        statsPanel.add(p3);
        statsPanel.add(p4);
        add(statsPanel, BorderLayout.SOUTH);

        // Populate Choice Boxes
        populateChoices();

        // Listeners for filters
        DocumentListener fromFilterListener = new DocumentListener() {
            private void update() {
                filterChoice(fromChoice, fromFilter.getText(), "USD");
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        };
        fromFilter.getDocument().addDocumentListener(fromFilterListener);

        DocumentListener toFilterListener = new DocumentListener() {
            private void update() {
                filterChoice(toChoice, toFilter.getText(), "EUR");
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        };
        toFilter.getDocument().addDocumentListener(toFilterListener);

        // Default selections
        setChoiceSelection(fromChoice, "USD");
        setChoiceSelection(toChoice, "EUR");

        generateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Swap placeholder with the chart display if not already done
                if (chartCard.getComponent(0) != chartPanelComponent) {
                    chartCard.removeAll();
                    chartCard.add(chartPanelComponent, BorderLayout.CENTER);
                    chartCard.revalidate();
                }
                generateTrendChart();
            }
        });
    }

    private RoundPanel createStatSubCard(String title, JLabel valLabel) {
        RoundPanel card = new RoundPanel(10);
        card.setLayout(new GridLayout(2, 1, 0, 4));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLbl = new JLabel(title, JLabel.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        titleLbl.setForeground(ModernUIFactory.COLOR_TEXT_MUTED);
        
        valLabel.setFont(ModernUIFactory.FONT_VALUE_SMALL);
        valLabel.setForeground(ModernUIFactory.COLOR_TEXT_MAIN);
        
        card.add(titleLbl);
        card.add(valLabel);
        return card;
    }

    private void populateChoices() {
        List<Currency> list = currencyService.getCurrencies();
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

    private void generateTrendChart() {
        String fromItem = (String) fromChoice.getSelectedItem();
        String toItem = (String) toChoice.getSelectedItem();

        if (fromItem == null || toItem == null) {
            return;
        }

        String fromCode = fromItem.substring(0, 3);
        String toCode = toItem.substring(0, 3);

        // Fetch current rate to base simulation on
        currencyService.getRates(fromCode, new CurrencyService.RateCallback() {
            @Override
            public void onResult(Map<String, Double> rates, LocalDateTime timestamp, boolean isLive) {
                Double currentRate = rates.get(toCode);
                if (currentRate != null) {
                    List<RateSnapshot> history = currencyService.getSimulatedHistory(fromCode, toCode, currentRate);
                    
                    // Calculations
                    double high = Double.MIN_VALUE;
                    double low = Double.MAX_VALUE;
                    for (RateSnapshot s : history) {
                        if (s.getRate() > high) high = s.getRate();
                        if (s.getRate() < low) low = s.getRate();
                    }

                    double oldestRate = history.get(0).getRate();
                    double newestRate = history.get(history.size() - 1).getRate();
                    double percentChange = ((newestRate - oldestRate) / oldestRate) * 100.0;

                    // Update Labels
                    final double fHigh = high;
                    final double fLow = low;
                    final double fChange = percentChange;
                    
                    EventQueue.invokeLater(() -> {
                        currentRateVal.setText(NumberFormatter.formatRate(newestRate, 4));
                        highVal.setText(NumberFormatter.formatRate(fHigh, 4));
                        lowVal.setText(NumberFormatter.formatRate(fLow, 4));
                        
                        String sign = fChange >= 0 ? "+" : "";
                        changeVal.setText(sign + String.format("%.2f%%", fChange));
                        if (fChange >= 0) {
                            changeVal.setForeground(ModernUIFactory.COLOR_SUCCESS_TEXT);
                        } else {
                            changeVal.setForeground(ModernUIFactory.COLOR_ERROR_TEXT);
                        }

                        // Generate chart
                        JFreeChart chart = createChartObject(fromCode, toCode, history);
                        chartPanelComponent.setChart(chart);
                    });
                }
            }

            @Override
            public void onError(String message) {
                // Ignore or log error
            }
        });
    }

    private JFreeChart createChartObject(String from, String to, List<RateSnapshot> history) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (RateSnapshot s : history) {
            dataset.addValue(s.getRate(), "Exchange Rate", s.getDate().format(formatter));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                from + " to " + to + " 7-Day Trend", // Title
                null,                                // X Axis Label (cleaner without "Date")
                null,                                // Y Axis Label (cleaner without "Rate")
                dataset,
                PlotOrientation.VERTICAL,
                false,                               // Legend
                true,                                // Tooltips
                false                                // URLs
        );

        // Customize General Chart Appearance
        chart.setBackgroundPaint(Color.WHITE);
        if (chart.getTitle() != null) {
            chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 15));
            chart.getTitle().setPaint(ModernUIFactory.COLOR_TEXT_MAIN);
            chart.getTitle().setMargin(0, 0, 10, 0);
        }

        // Customize the line and grid layout appearance
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null); // Remove chart outline border
        plot.setRangeGridlinePaint(ModernUIFactory.COLOR_BORDER);
        plot.setRangeGridlineStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{3.0f}, 0.0f)); // Dashed gridlines
        
        // Hide domain gridlines for cleaner modern looks
        plot.setDomainGridlinesVisible(false);

        // Set Axis styles
        plot.getDomainAxis().setAxisLinePaint(ModernUIFactory.COLOR_BORDER);
        plot.getDomainAxis().setTickLabelPaint(ModernUIFactory.COLOR_TEXT_MUTED);
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        
        org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(false); // Do not force zero, show rate fluctuations clearly
        rangeAxis.setAxisLinePaint(ModernUIFactory.COLOR_BORDER);
        rangeAxis.setTickLabelPaint(ModernUIFactory.COLOR_TEXT_MUTED);
        rangeAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, ModernUIFactory.COLOR_PRIMARY); // Modern Blue line
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));       // Sleek thick line
        renderer.setSeriesShapesVisible(0, true);                  // Dots on points
        renderer.setSeriesShapesFilled(0, true);
        
        // Make dots look clean (draw circular shapes)
        renderer.setUseOutlinePaint(true);
        renderer.setSeriesOutlinePaint(0, Color.WHITE);
        renderer.setSeriesOutlineStroke(0, new BasicStroke(1.5f));

        return chart;
    }
}
