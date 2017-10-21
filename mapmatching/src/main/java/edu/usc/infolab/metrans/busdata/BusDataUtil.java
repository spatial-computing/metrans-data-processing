package edu.usc.infolab.metrans.busdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * Bus data processing utilities
 */
public class BusDataUtil {
    public static final Set<Integer> VALID_BUS_DIRECTIONS = Collections.unmodifiableSet(
            new HashSet<Integer>(Arrays.asList(0, 1, 2, 3)));

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
     * Check if a record has valid direction
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
}
