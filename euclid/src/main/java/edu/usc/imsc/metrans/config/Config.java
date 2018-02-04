package edu.usc.imsc.metrans.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Properties;
import java.util.TimeZone;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public static final String CONFIG_FILE = "config.properties";

    public static final String DEFAULT_TIME_ZONE = "America/Los_Angeles";
    public static final int DEFAULT_RUN_MIN_NUM_RECORDS = 4;

    public static final String KEY_TIME_ZONE = "timezone";
    public static final String KEY_RUN_MIN_NUM_RECORDS_KEY = "preprocessing.runMinNumRecords";

    public static int runMinNumRecords = DEFAULT_RUN_MIN_NUM_RECORDS;
    public static TimeZone timeZone = TimeZone.getTimeZone(Config.DEFAULT_TIME_ZONE);
    public static ZoneId zoneId = timeZone.toZoneId();


    /**
     * Load configuration from files
     */
    public static void load() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(CONFIG_FILE));

            if (properties.containsKey(KEY_RUN_MIN_NUM_RECORDS_KEY)) {
                runMinNumRecords = Integer.parseInt(properties.getProperty(KEY_RUN_MIN_NUM_RECORDS_KEY));
            }

            if (properties.containsKey(KEY_TIME_ZONE)) {
                timeZone = TimeZone.getTimeZone(properties.getProperty(KEY_TIME_ZONE));
                zoneId = timeZone.toZoneId();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
