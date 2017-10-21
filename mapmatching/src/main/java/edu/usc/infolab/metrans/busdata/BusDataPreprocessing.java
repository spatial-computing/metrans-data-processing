package edu.usc.infolab.metrans.busdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

public class BusDataPreprocessing {
    private static final Logger logger = LoggerFactory.getLogger(BusDataPreprocessing.class);


    /**
     * Preprocess records of bus GPS into RUNs:
     * - Getting: BUS_ID => records
     * - Getting records of each bus PER DAY
     * - Trimming invalid directions in the beginning or end of a day
     * - Separating the whole day records of a bus into RUNs
     * - Cleaning runs
     * @param records records of bus GPS
     * @return cleaned runs
     */
    public static ArrayList<ArrayList<BusGpsRecord>> preprocessBusGpsDataIntoRuns(ArrayList<BusGpsRecord> records) {
        logger.info("Getting: BUS_ID => records");
        Map<Integer, ArrayList<BusGpsRecord>> busIdRecordsMaps = BusDataUtil.getBusIdRecordsMap(records);
        logger.info(busIdRecordsMaps.size() + " bus ids");


        logger.info("Getting records of each bus PER DAY");
        ArrayList<ArrayList<BusGpsRecord>> busGpsDayTrips = new ArrayList<>();
        for (ArrayList<BusGpsRecord> busIdsRecords : busIdRecordsMaps.values()) {
            Map<Integer, ArrayList<BusGpsRecord>> dayRecordsMaps = BusDataUtil.getDayRecordsMap(busIdsRecords);
            busGpsDayTrips.addAll(dayRecordsMaps.values());
        }

        int recordCount = 0;
        for (ArrayList<BusGpsRecord> busIdsRecords : busGpsDayTrips)
            recordCount += busIdsRecords.size();
        logger.info(busGpsDayTrips.size() + " bus GPS day trips" + " with " + recordCount + " records");


        logger.info("Trimming invalid directions in the beginning or end of a day");
        int trimmedCount = 0;
        for (ArrayList<BusGpsRecord> busIdsRecords : busGpsDayTrips) {
            int startSize = busIdsRecords.size();
            BusDataUtil.trimInvalidDirection(busIdsRecords);
            trimmedCount += startSize - busIdsRecords.size();
        }

        recordCount = 0;
        for (ArrayList<BusGpsRecord> busIdsRecords : busGpsDayTrips)
            recordCount += busIdsRecords.size();
        logger.info(busGpsDayTrips.size() + " bus GPS day trips" + " with " + recordCount + " records, " + trimmedCount + " trimmed");

        int invalidDirectionTripsCount = 0;
        for (ArrayList<BusGpsRecord> busIdsRecords : busGpsDayTrips) {
            if (!BusDataUtil.hasValidDirection(busIdsRecords)) {
                invalidDirectionTripsCount += 1;
            }
        }
        logger.info(invalidDirectionTripsCount + " invalid direction day trips");


        logger.info("Separating the whole day records of a bus into RUNs");
        ArrayList<ArrayList<BusGpsRecord>> allRuns = new ArrayList<>();
        for (ArrayList<BusGpsRecord> busIdsRecords : busGpsDayTrips) {
            ArrayList<ArrayList<BusGpsRecord>> dayRuns = BusDataUtil.separateToRuns(busIdsRecords);
            allRuns.addAll(dayRuns);
        }
        logger.info("Total " + allRuns.size() + " runs");

        logger.info("Cleaning runs");
        BusDataUtil.cleanRuns(allRuns);
        logger.info("Total " + allRuns.size() + " runs after cleaning");

        return allRuns;
    }
}
