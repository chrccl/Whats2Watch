package com.whats2watch.w2w.config;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Config {

    public static String loadTMDBApiKey() {
        Properties properties = new Properties();
        URL url = ClassLoader.getSystemResource("conf.properties");

        try {
            properties.load(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty("tmdb.api.key");
    }

}
