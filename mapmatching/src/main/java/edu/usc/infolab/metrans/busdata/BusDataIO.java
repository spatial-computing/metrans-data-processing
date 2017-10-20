package edu.usc.infolab.metrans.busdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * IO class for bus data
 */
public class BusDataIO {
    public static final String BUS_GPS_CSV_SEPARATOR = ",";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final ZoneId LOS_ANGELES_ZONE_ID = ZoneId.of("America/Los_Angeles");

    private static final Logger logger = LoggerFactory.getLogger(BusDataIO.class);
    private static DateTimeFormatter defaultDateTimeParser =
            DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(LOS_ANGELES_ZONE_ID);

    /**
     * Read bus GPS records from a CSV file
     * @param inputCsvFile file path
     * @return list of records in the order in the input file
     */
    public static ArrayList<BusGpsRecord> readBusGpsRecordsFromFile(String inputCsvFile) {
        ArrayList<BusGpsRecord> records = new ArrayList<>();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(inputCsvFile));
            reader.readLine(); // skip the header

            String line;

            while ((line = reader.readLine()) != null) {
                BusGpsRecord record = convertCsvBusLineToRecord(line);

                records.add(record);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        return records;
    }


    /**
     * Convert a raw line CSV bus data into a record
     * @param line raw line CSV
     * @return a record
     */
    public static BusGpsRecord convertCsvBusLineToRecord(String line) {
        String[] rawRecord = line.split(BUS_GPS_CSV_SEPARATOR);

        ZonedDateTime dateAndTime = ZonedDateTime.parse(rawRecord[0], defaultDateTimeParser);
        int busId = Integer.valueOf(rawRecord[1]);
        int lineId = Integer.valueOf(rawRecord[2]);
        int runId = Integer.valueOf(rawRecord[3]);
        int routeId = Integer.valueOf(rawRecord[4]);
        int busDirection = Integer.valueOf(rawRecord[6]);
        double lat = Double.valueOf(rawRecord[7]);
        double lon = Double.valueOf(rawRecord[8]);
        ZonedDateTime busLocationTime = ZonedDateTime.parse(rawRecord[9], defaultDateTimeParser);

        return new BusGpsRecord(dateAndTime, busId, lineId, runId, routeId, busDirection, lat, lon, busLocationTime);
    }
}
