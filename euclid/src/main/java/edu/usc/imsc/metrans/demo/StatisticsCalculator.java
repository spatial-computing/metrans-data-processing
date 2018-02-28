package edu.usc.imsc.metrans.demo;

import edu.usc.imsc.metrans.arrivaltimeestimators.Util;
import edu.usc.imsc.metrans.connection.DatabaseIO;
import edu.usc.imsc.metrans.statcalculators.DelayCalculator;
import edu.usc.imsc.metrans.timedata.ArrivalTimeEstRawRecord;
import edu.usc.imsc.metrans.timedata.ArrivalTimeEstRawRecordComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class StatisticsCalculator {
    public static final int SECONDS_IN_DAY = 24 * 60* 60;

    private static final Logger logger = LoggerFactory.getLogger(StatisticsCalculator.class);
    private static ArrayList<ArrivalTimeEstRawRecord> delayRecords;

    public static void main(String[] args) {
        long startTimestamp = Long.valueOf(args[0]);
        long endTimestamp = Long.valueOf(args[1]);

        logger.info("Start calculating statistics from " + startTimestamp + " to " + (endTimestamp - 1));

        long time = startTimestamp;
        while (time < endTimestamp) {
            ZonedDateTime currTime = Util.convertSecondsToZonedDateTime(time);
            logger.info("Date: " + currTime.getYear() + "-" + currTime.getMonth() + "-" + currTime.getDayOfMonth());

            delayRecords = new ArrayList<>();

            ArrayList<ArrivalTimeEstRawRecord> records = DatabaseIO.getEstimatedDelayByDay(time);

            if (!records.isEmpty()) {
                calStats(records);

                logger.info("Inserting " + delayRecords.size() + " delay records");
                DatabaseIO.insertBatchEstimatedDelayTime(delayRecords);
            }

            time += SECONDS_IN_DAY;
        }

    }

    public static void calStats(ArrayList<ArrivalTimeEstRawRecord> records) {
        records.sort(new ArrivalTimeEstRawRecordComparator());

        int prevIdx = 0;
        int currIdx = prevIdx + 1;
        while (currIdx < records.size()) {
            ArrivalTimeEstRawRecord prevRecord = records.get(prevIdx);
            ArrivalTimeEstRawRecord currRecord = records.get(currIdx);

            if (!prevRecord.getRouteId().equals(currRecord.getRouteId())
                    || !prevRecord.getStopId().equals(currRecord.getStopId())
                    || !prevRecord.getTripId().equals(currRecord.getTripId())) {
                // a different combo
                // So calStats all the records of the current combo
                ArrayList<ArrivalTimeEstRawRecord> aCombo = new ArrayList<>();
                for (int i = prevIdx; i < currIdx; i++)
                    aCombo.add(records.get(i));

                // calculate statistics for this combo
                calStatsCombo(aCombo);

                // and prepare for the next combo
                prevIdx = currIdx;
            }

            currIdx++;
        }
    }

    public static void calStatsCombo(ArrayList<ArrivalTimeEstRawRecord> aCombo) {
        delayRecords.add(DelayCalculator.findDelayTimeRecord(aCombo));
    }
}
