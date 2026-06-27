package com.currencyapp.ui;

import com.currencyapp.service.CurrencyService;
import com.currencyapp.ui.components.ModernUIFactory;
import com.currencyapp.ui.components.ModernUIFactory.ModernButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel containerPanel;
    private final StatusBar statusBar;

    private final ConverterPanel converterPanel;
    private final ChartPanel chartPanel;
    private final ComparePanel comparePanel;

    private final ModernButton btnConverter;
    private final ModernButton btnChart;
    private final ModernButton btnCompare;

    public MainWindow() {
        super("Currency Converter & Trends");
        
        // Window parameters
        setSize(850, 720); // slightly wider for modern look
        setMinimumSize(new Dimension(800, 680));
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(ModernUIFactory.COLOR_BG);

        // Header Panel (Logo + Tabs)
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ModernUIFactory.COLOR_BORDER),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        // Logo/Title on the Left
        JLabel titleLabel = new JLabel("Currency Exchange");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(ModernUIFactory.COLOR_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Navigation Tabs on the Right
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navPanel.setOpaque(false);
        
        btnConverter = ModernButton.createTabButton("Converter");
        btnChart = ModernButton.createTabButton("Trend Charts");
        btnCompare = ModernButton.createTabButton("Compare Rates");

        navPanel.add(btnConverter);
        navPanel.add(btnChart);
        navPanel.add(btnCompare);
        headerPanel.add(navPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Center Multi-Panel Shell with some padding around it
        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);
        containerPanel.setOpaque(false);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        converterPanel = new ConverterPanel();
        chartPanel = new ChartPanel();
        comparePanel = new ComparePanel();

        containerPanel.add(converterPanel, "CONVERTER");
        containerPanel.add(chartPanel, "CHART");
        containerPanel.add(comparePanel, "COMPARE");
        add(containerPanel, BorderLayout.CENTER);

        // Bottom Status Bar
        statusBar = new StatusBar();
        add(statusBar, BorderLayout.SOUTH);

        // Setup Tab Actions
        ActionListener navListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if (source == btnConverter) {
                    showTab("CONVERTER");
                } else if (source == btnChart) {
                    showTab("CHART");
                } else if (source == btnCompare) {
                    showTab("COMPARE");
                }
            }
        };

        btnConverter.addActionListener(navListener);
        btnChart.addActionListener(navListener);
        btnCompare.addActionListener(navListener);

        // Set Default Active Tab
        showTab("CONVERTER");

        // Window closing events for clean shutdown
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });

        // Trigger background connectivity check
        CurrencyService.getInstance().checkConnectivity();
    }

    private void showTab(String tabName) {
        cardLayout.show(containerPanel, tabName);

        // Reset all navigation states
        btnConverter.setTabActive(false);
        btnChart.setTabActive(false);
        btnCompare.setTabActive(false);

        // Set active states
        switch (tabName) {
            case "CONVERTER":
                btnConverter.setTabActive(true);
                break;
            case "CHART":
                btnChart.setTabActive(true);
                break;
            case "COMPARE":
                btnCompare.setTabActive(true);
                break;
        }
        
        // Force rendering updates
        revalidate();
        repaint();
    }

    private void cleanup() {
        if (converterPanel != null) {
            converterPanel.cleanup();
        }
    }

    public static void main(String[] args) {
        // Set system properties for clean anti-aliasing in Swing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Set system look and feel or keep it flat
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Run application on the Event Dispatch Thread
        EventQueue.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
