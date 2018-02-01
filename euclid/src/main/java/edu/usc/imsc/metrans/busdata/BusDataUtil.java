package edu.usc.imsc.metrans.busdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * Bus data processing utilities
 */
public class BusDataUtil {
    public static final double EPSILON = 1e-12;
    public static final Set<Integer> VALID_BUS_DIRECTIONS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(0, 1, 2, 3)));

    private static final Logger logger = LoggerFactory.getLogger(BusDataUtil.class);

    private static Comparator<BusGpsRecord> busGpsRecordLocationTimeComparator = new Comparator<BusGpsRecord>() {
        @Override
        public int compare(BusGpsRecord o1, BusGpsRecord o2) {
            return o1.getBusLocationTime().compareTo(o2.getBusLocationTime());
        }
    };


    /**
     * Convert a time to a single Integer formatted as YYYYMMDD
     * @param aTime
     * @return the integer value
     */
    public static Integer convertTimeToIntegerFormat(ZonedDateTime aTime) {
        return aTime.getYear() * 10000 + aTime.getMonthValue() * 100 + aTime.getDayOfMonth();
    }

    /**
     * Get a map from a bus id to records of that bus id, sorted by BUS_LOCATION_TIME
     * @param records list of records
     * @return a map from a bus id to records of that bus id
     */
    public static Map<Integer, ArrayList<BusGpsRecord>> getBusIdRecordsMap(ArrayList<BusGpsRecord> records) {
        Map<Integer, ArrayList<BusGpsRecord>> busIdRecordsMaps = new HashMap<>();

        for (BusGpsRecord record : records) {
            Integer key = record.getBusId();
            if (busIdRecordsMaps.containsKey(key)) {
                busIdRecordsMaps.get(key).add(record);
            } else {
                ArrayList<BusGpsRecord> busIdRecords = new ArrayList<>();
                busIdRecords.add(record);
                busIdRecordsMaps.put(key, busIdRecords);
            }
        }

        for (Map.Entry<Integer, ArrayList<BusGpsRecord>> entry : busIdRecordsMaps.entrySet()) {
            entry.getValue().sort(busGpsRecordLocationTimeComparator);
        }

        return busIdRecordsMaps;
    }

    /**
     * Get a map from a day to records of that day, sorted by BUS_LOCATION_TIME.
     *
     * The key has format: YYYYMMDD as a single Integer
     *
     * @param records list of records
     * @return a map from a day to records of that day
     */
    public static Map<Integer, ArrayList<BusGpsRecord>> getDayRecordsMap(ArrayList<BusGpsRecord> records) {
        Map<Integer, ArrayList<BusGpsRecord>> dayRecordsMaps = new HashMap<>();

        for (BusGpsRecord record : records) {
            Integer key = convertTimeToIntegerFormat(record.getBusLocationTime());
            if (dayRecordsMaps.containsKey(key)) {
                dayRecordsMaps.get(key).add(record);
            } else {
                ArrayList<BusGpsRecord> busIdRecords = new ArrayList<>();
                busIdRecords.add(record);
                dayRecordsMaps.put(key, busIdRecords);
            }
        }

        for (Map.Entry<Integer, ArrayList<BusGpsRecord>> entry : dayRecordsMaps.entrySet()) {
            entry.getValue().sort(busGpsRecordLocationTimeComparator);
        }

        return dayRecordsMaps;
    }


    /**
     * Check if current record is in the same run with previous record.
     *
     * They are in the same run if they have same BUS_ID, LINE_ID, RUN_ID, ROUTE_ID, BUS_DIRECTION
     * @param prevRecord previous record
     * @param currRecord current record
     * @return whether or not current record is in the same run with previous record
     */
    public static boolean isInSameRun(BusGpsRecord prevRecord, BusGpsRecord currRecord) {
        return prevRecord.getBusId() == currRecord.getBusId() &&
                prevRecord.getLineId() == currRecord.getLineId() &&
                prevRecord.getRunId() == currRecord.getRunId() &&
                prevRecord.getRouteId() == currRecord.getRouteId() &&
                prevRecord.getBusDirection() == currRecord.getBusDirection();
    }


    /**
     * Separate records into different runs:
     * - ALL consecutive records with the same BUS_ID, LINE_ID, RUN_ID, ROUTE_ID, BUS_DIRECTION are in the same run
     * @param records a list of records
     * @return list of runs
     */
    public static ArrayList<ArrayList<BusGpsRecord>> separateToRuns(ArrayList<BusGpsRecord> records) {
        ArrayList<ArrayList<BusGpsRecord>> allRuns = new ArrayList<>();
        if (records.isEmpty())
            return allRuns;

        ArrayList<BusGpsRecord> aRun = new ArrayList<>();
        aRun.add(records.get(0));

        for (int i = 1; i < records.size(); i++) {
            BusGpsRecord prevRecord = aRun.get(aRun.size() - 1);
            BusGpsRecord currRecord = records.get(i);

            if (isInSameRun(prevRecord, currRecord)) {
                aRun.add(currRecord);
            } else {
                // a NEW run
                allRuns.add(aRun); // save the run

                aRun = new ArrayList<>(); // start a new run
                aRun.add(currRecord);
            }
        }
        allRuns.add(aRun);


        return allRuns;
    }


    /**
     * Check if a run has valid direction (using {@link #hasValidDirection(BusGpsRecord)},
     * assuming all records have same direction
     * @param aRun
     * @return whether or not a run has valid direction
     */
    public static boolean isValidDirectionRun(ArrayList<BusGpsRecord> aRun) {
        if (!hasValidDirection(aRun.get(0)))
            return false;
        return true;
    }


    /**
     * Remove "not good" runs, using {@link #isValidDirectionRun(ArrayList)}
     * @param runs
     */
    public static void cleanRuns(ArrayList<ArrayList<BusGpsRecord>> runs) {
        ArrayList<ArrayList<BusGpsRecord>> toRemove = new ArrayList<>();
        for (ArrayList<BusGpsRecord> aRun : runs) {
            if (!isValidDirectionRun(aRun))
                toRemove.add(aRun);
        }

        runs.removeAll(toRemove);
    }


    /**
     * Get set of all directions of bus records
     * @param records bus records
     * @return set of all directions of bus records
     */
    public static Set<Integer> getDirections(ArrayList<BusGpsRecord> records) {
        Set<Integer> directions = new HashSet<>();
        for (BusGpsRecord record : records) {
            directions.add(record.getBusDirection());
        }

        return directions;
    }

    /**
     * Check if a record has valid direction.
     * A direction is valid if it is in {@link #VALID_BUS_DIRECTIONS}
     * @param record a bus GPS record
     * @return whether or not a record has valid direction
     */
    public static boolean hasValidDirection(BusGpsRecord record) {
        return VALID_BUS_DIRECTIONS.contains(record.getBusDirection());
    }


    /**
     * Check if all records have valid direction
     * @param records a list of bus GPS records
     * @return whether or not all records have valid direction
     */
    public static boolean hasValidDirection(ArrayList<BusGpsRecord> records) {
        for (BusGpsRecord record: records) {
            if (!BusDataUtil.hasValidDirection(record)) {
                return false;
            }
        }

        return true;
    }


    /**
     * Remove starting and ending records with incorrect BUS_DIRECTION
     * @param records list of records to trim
     */
    public static void trimInvalidDirection(ArrayList<BusGpsRecord> records) {
        while (!records.isEmpty() && !hasValidDirection(records.get(0))) {
            records.remove(0);
        }

        while (!records.isEmpty() && !hasValidDirection(records.get(records.size() - 1))) {
            records.remove(records.size() - 1);
        }
    }


    /**
     * Print some statistics for multiple runs
     * @param allRuns
     */
    public static void printRunsStatistics(ArrayList<ArrayList<BusGpsRecord>> allRuns) {
        logger.info("Total " + allRuns.size() + " runs");

        int recordCount = 0;
        for (ArrayList<BusGpsRecord> aRun : allRuns)
            recordCount += aRun.size();
        logger.info("Total " + recordCount + " records");
    }

    /**
     * Remove duplicates records in a run
     * @param gpsRun a GPS run
     */
    public static void removeDuplicateRecordsInRun(ArrayList<BusGpsRecord> gpsRun) {
        int size = gpsRun.size();
        boolean[] dups = new boolean[size];
        Arrays.fill(dups, false);
        for (int i = 0; i < size; i++) {
            if (!dups[i]) {
                for (int j = i + 1; j < size; j++) {
                    if (isDuplicate(gpsRun.get(i), gpsRun.get(j))) {
                        dups[j] = true;
                    }
                }
            }
        }

        for (int i = size - 1; 0 <= i; i--) {
            if (dups[i]) {
                gpsRun.remove(i);
            }
        }
    }

    /**
     * Whether or not two 2 records are duplicate
     * @param a record 1
     * @param b record 2
     * @return Whether or not two 2 records are duplicate
     */
    public static boolean isDuplicate(BusGpsRecord a, BusGpsRecord b) {
        return a.getBusId() == b.getBusId()
                && a.getLineId() == b.getLineId()
                && a.getRunId() == b.getRunId()
                && a.getRouteId() == b.getRouteId()
                && a.getBusDirection() == b.getBusDirection()
                && (Math.abs(a.getLat() - b.getLat()) <= EPSILON)
                && (Math.abs(a.getLon() - b.getLon()) <= EPSILON)
                && a.getBusLocationTime().isEqual(b.getBusLocationTime());
    }
}
