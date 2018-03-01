package edu.usc.imsc.metrans.gtfsutil;

import edu.usc.imsc.metrans.Config;

import java.io.IOException;

public class GtfsStoreProvider {
    private static GtfsStore gtfsStore = null;

    /**
     * Get GTFS store from a directory in configuration
     * @return GTFS store or {@code null} if error occurred
     */
    public static GtfsStore getGtfsStore() {
        if (gtfsStore == null) {
            synchronized (GtfsStoreProvider.class) {
                if (gtfsStore == null) {
                    try {
                        gtfsStore = new GtfsStore(Config.gtfsDir);
                    } catch (IOException e) {
                        System.err.println("Unable to load GTFS data from " + Config.gtfsDir + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }

            }
        }

        return gtfsStore;
    }
}
