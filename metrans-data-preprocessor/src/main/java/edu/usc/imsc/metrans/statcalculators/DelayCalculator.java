package edu.usc.imsc.metrans.statcalculators;

import edu.usc.imsc.metrans.timedata.ArrivalTimeEstRawRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Calculate average delay time
 */
public class DelayCalculator {
    private static final Logger logger = LoggerFactory.getLogger(DelayCalculator.class);

    /**
     * Given records of the same (route, stop, trip):
     *   - get the max negative delay time and min positive delay time
     *   - use the one with smaller absolute value as the result
     * @param records records of the same (route, stop, trip)
     * @return the record of the delay time
     */
    public static ArrivalTimeEstRawRecord findDelayTimeRecord(ArrayList<ArrivalTimeEstRawRecord> records) {
        ArrivalTimeEstRawRecord maxNegRecord = null;
        ArrivalTimeEstRawRecord minPosRecord = null;

        for (ArrivalTimeEstRawRecord record : records) {
            if (record.getDelayTime() < 0) {
                // if this is a negative delay time record
                // keep it if we do not have a previous negative delay time record
                if (maxNegRecord == null)
                    maxNegRecord = record;
                // or if this record has a bigger value
                else if (maxNegRecord.getDelayTime() < record.getDelayTime())
                        maxNegRecord = record;

            } else {
                // if this is a positive delay time record
                // keep it if we do not have a previous positive delay time record
                if (minPosRecord == null)
                    minPosRecord = record;
                // or if this record has a smaller value
                else if (record.getDelayTime() < minPosRecord.getDelayTime())
                    minPosRecord = record;
            }
        }

        ArrivalTimeEstRawRecord delayRecord;

        if (maxNegRecord != null && minPosRecord != null) {
            //have both -> get the smaller one
            if (Math.abs(maxNegRecord.getDelayTime()) < minPosRecord.getDelayTime())
                delayRecord = maxNegRecord;
            else
                delayRecord = minPosRecord;
        } else {
            if (maxNegRecord != null)
                delayRecord = maxNegRecord;
            else
                delayRecord = minPosRecord;
        }

        return delayRecord;
    }
}
