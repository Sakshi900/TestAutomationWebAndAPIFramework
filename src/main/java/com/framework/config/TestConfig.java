package com.framework.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestConfig {

        private static final Properties props = new Properties();

        static {
            try {
                props.load(new FileInputStream("src/test/resources/config.properties"));
            } catch (IOException e) {
                throw new RuntimeException("Unable to load config.properties", e);
            }
            // system properties override file values
            for (String key : System.getProperties().stringPropertyNames()) {
                if (props.containsKey(key)) {
                    props.setProperty(key, System.getProperty(key));
                }
            }
        }

        public static String baseUrl() { return props.getProperty("baseUrl"); }
        public static String browser() { return props.getProperty("browser", "chrome"); }
        public static boolean headless() { return Boolean.parseBoolean(props.getProperty("headless", "true")); }
        public static int implicitWaitSec() { return Integer.parseInt(props.getProperty("implicitWaitSec", "0")); }
        public static int explicitWaitSec() { return Integer.parseInt(props.getProperty("explicitWaitSec", "20")); }
    }

