package edu.usc.imsc.metrans.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
    private static final int RUN_MIN_NUM_RECORDS_DEFAULT = 4;

    private static final String RUN_MIN_NUM_RECORDS_KEY = "preprocessing.runMinNumRecords";

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public static int RUN_MIN_NUM_RECORDS = RUN_MIN_NUM_RECORDS_DEFAULT;


    /**
     * Prepare configuration.
     *
     * Should be run before any configuration is used
     */
    public static void prepare() {

    }
}
