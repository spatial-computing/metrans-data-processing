package edu.usc.infolab.metrans.busdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Bus data processing utilities
 */
public class BusDataUtil {
    private static final Logger logger = LoggerFactory.getLogger(BusDataUtil.class);

    private static Comparator<BusGpsRecord> busGpsRecordLocationTimeComparator = new Comparator<BusGpsRecord>() {
        @Override
        public int compare(BusGpsRecord o1, BusGpsRecord o2) {
            return o1.getBusLocationTime().compareTo(o2.getBusLocationTime());
        }
    };

    /**
     * Get a map from a bus id to records of that bus id, sorted by BUS_LOCATION_TIME
     * @param records list of records
     * @return a map from a bus id to records of that bus id
     */
    public static Map<Integer, ArrayList<BusGpsRecord>> getBusIdRecordsMap(ArrayList<BusGpsRecord> records) {
        Map<Integer, ArrayList<BusGpsRecord>> busIdRecordsMaps = new HashMap<>();

        for (BusGpsRecord record : records) {
            if (busIdRecordsMaps.containsKey(record.getBusId())) {
                busIdRecordsMaps.get(record.getBusId()).add(record);
            } else {
                ArrayList<BusGpsRecord> busIdRecords = new ArrayList<>();
                busIdRecords.add(record);
                busIdRecordsMaps.put(record.getBusId(), busIdRecords);
            }
        }

        for ( Map.Entry<Integer, ArrayList<BusGpsRecord>> entry : busIdRecordsMaps.entrySet()) {
            entry.getValue().sort(busGpsRecordLocationTimeComparator);
        }

        return busIdRecordsMaps;
    }
}
