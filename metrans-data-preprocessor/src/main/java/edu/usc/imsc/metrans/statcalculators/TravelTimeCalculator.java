package edu.usc.imsc.metrans.statcalculators;

import edu.usc.imsc.metrans.timedata.ArrivalTimeEstRawRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Calculate average delay time
 */
public class TravelTimeCalculator {
    private static final Logger logger = LoggerFactory.getLogger(TravelTimeCalculator.class);

    /**
     * Given records of the same (route, stop, trip):
     *   - find travel-time record, which is the one with min positive delay time record
     * @param records records of the same (route, stop, trip)
     * @return the record of the travel-time or {@code null} if not exist
     */
    public static ArrivalTimeEstRawRecord findTravelTimeRecord(ArrayList<ArrivalTimeEstRawRecord> records) {
        ArrivalTimeEstRawRecord minPosRecord = null;

        for (ArrivalTimeEstRawRecord record : records) {
            if (0 <= record.getDelayTime()) {
                // if this is a positive delay time record
                // keep it if we do not have a previous positive delay time record
                if (minPosRecord == null)
                    minPosRecord = record;
                    // or if this record has a smaller value
                else if (record.getDelayTime() < minPosRecord.getDelayTime())
                    minPosRecord = record;
            }
        }

        return minPosRecord;
    }
}
