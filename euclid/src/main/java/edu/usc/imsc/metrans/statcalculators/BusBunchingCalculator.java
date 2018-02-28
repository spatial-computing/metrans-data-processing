package edu.usc.imsc.metrans.statcalculators;

import edu.usc.imsc.metrans.timedata.ArrivalTimeEstRawRecord;
import edu.usc.imsc.metrans.timedata.BusBunchingRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class BusBunchingCalculator {
    private static final Logger logger = LoggerFactory.getLogger(BusBunchingCalculator.class);

    // Time buffers are based on the indicators of LA Metro:
    // https://www.metro.net/about/metro-service-changes/service-performance-indicators/
    public static final int EARLY_TIME_BUFFER = 60; // seconds
    public static final int LATE_TIME_BUFFER = 5 * 60; // seconds

    /**
     * Calculate number of buses appears in the on-time time buffer
     *
     * In-Service On-Time Performance uses a standard of one minute early and five-minutes late
     * as the range of on-time achievement.
     * {https://www.metro.net/about/metro-service-changes/service-performance-indicators/}
     *
     * @param records estimated arrival time records of the same (route, stop, trip)
     * @return bus bunching record for the given estimated arrival time records
     */
    public static BusBunchingRecord calBusBunching(ArrayList<ArrivalTimeEstRawRecord> records) {
        if (records.isEmpty())
            return null;
        int nBus = 0;

        for (ArrivalTimeEstRawRecord record : records) {
            if (-EARLY_TIME_BUFFER <= record.getDelayTime() || record.getDelayTime() <= LATE_TIME_BUFFER)
                nBus++;
        }

        BusBunchingRecord busBunchingRecord = new BusBunchingRecord();
        busBunchingRecord.setRouteId(records.get(0).getRouteId());
        busBunchingRecord.setStopId(records.get(0).getStopId());
        busBunchingRecord.setTripId(records.get(0).getTripId());
        busBunchingRecord.setEstimatedTime(records.get(0).getEstimatedTime());
        busBunchingRecord.setScheduleTime(records.get(0).getScheduleTime());
        busBunchingRecord.setNumBusBunching(nBus);

        return busBunchingRecord;
    }
}
