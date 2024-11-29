package com.whats2watch.w2w.config;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Config {

    private Config() {
        throw new UnsupportedOperationException("Config is a utility class and cannot be instantiated.");
    }

    public static String loadTMDBApiKey() {
        Properties properties = new Properties();
        URL url = ClassLoader.getSystemResource("conf.properties");

        try {
            properties.load(url.openStream());
        } catch (IOException e) {
            return "";
        }
        return properties.getProperty("tmdb.api.key");
    }

}
