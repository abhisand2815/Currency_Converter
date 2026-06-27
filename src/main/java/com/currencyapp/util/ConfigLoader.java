package com.currencyapp.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getResourceAsStream("/config.properties")) {
            if (input == null) {
                System.err.println("Warning: config.properties not found in resources. Using fallback defaults.");
            } else {
                properties.load(input);
            }
        } catch (Exception ex) {
            System.err.println("Error loading config.properties: " + ex.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
