package edu.usc.imsc.metrans.busdata;

import edu.usc.imsc.metrans.config.Config;
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
    public static final String BUS_DATA_CSV_HEADER = "DATE_AND_TIME,BUS_ID,LINE_ID,RUN_ID,ROUTE_ID,ROUTE_DESCRIPTION,BUS_DIRECTION,LAT,LON,BUS_LOCATION_TIME,SCHEDULE_DEVIATION,ARRIVAL_AT_NEXT_TIME_POINT,NEXT_TIME_POINT_LOCATION,TIME_POINT";
    public static final String BUS_GPS_CSV_SEPARATOR = ",";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static DateTimeFormatter defaultDateTimeParser =
            DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(Config.zoneId);

    private static final Logger logger = LoggerFactory.getLogger(BusDataIO.class);

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
                if (line.equals(BUS_DATA_CSV_HEADER))
                    continue;
                BusGpsRecord record = convertCsvBusLineToRecord(line);

                if (record != null)
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
     * @return a record or {@code null} if error occurred
     */
    public static BusGpsRecord convertCsvBusLineToRecord(String line) {
        try {
            String[] rawRecord = line.split(BUS_GPS_CSV_SEPARATOR);

            long dateAndTime = ZonedDateTime.parse(rawRecord[0], defaultDateTimeParser).toEpochSecond();
            int busId = Integer.valueOf(rawRecord[1]);
            int lineId = Integer.valueOf(rawRecord[2]);
            int runId = Integer.valueOf(rawRecord[3]);
            int routeId = Integer.valueOf(rawRecord[4]);
            int busDirection = Integer.valueOf(rawRecord[6]);
            double lat = Double.valueOf(rawRecord[7]);
            double lon = Double.valueOf(rawRecord[8]);
            long busLocationTime = ZonedDateTime.parse(rawRecord[9], defaultDateTimeParser).toEpochSecond();

            return new BusGpsRecord(dateAndTime, busId, lineId, runId, routeId, busDirection, lat, lon, busLocationTime);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
