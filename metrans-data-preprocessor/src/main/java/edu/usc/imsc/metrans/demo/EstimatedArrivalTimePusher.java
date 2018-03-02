package edu.usc.imsc.metrans.demo;


import edu.usc.imsc.metrans.busdata.BusDataIO;
import edu.usc.imsc.metrans.connection.DatabaseIO;
import edu.usc.imsc.metrans.timedata.ArrivalTimeEstRawRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class EstimatedArrivalTimePusher {

    private static final Logger logger = LoggerFactory.getLogger(EstimatedArrivalTimePusher.class);

    public static void main(String[] args) {
        String dataDir = "../../";
        String estimatedDir = dataDir + "estimated_data_160101_171010/";

        ArrayList<String> files = Utils.getFilesWithExtInFolder(estimatedDir, ".csv");
        Collections.sort(files);

        int readingBatchSize = 1000000;

        for (String busDataFile: files) {
            logger.info("STARTED: " + busDataFile);
            ArrayList<ArrivalTimeEstRawRecord> records = new ArrayList<>();

            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new FileReader(busDataFile));

                String line;

                while ((line = reader.readLine()) != null) {
                    ArrivalTimeEstRawRecord record = BusDataIO.convertCsvEstimatedTimeToRecord(line);

                    if (record != null)
                        records.add(record);

                    if (records.size() >= readingBatchSize) {
                        boolean isInserted = DatabaseIO.insertBatchEstimatedArrivalTime(records);
                        if (isInserted) {
                            records.clear();
                        }
                    }
                }

                while (!records.isEmpty()) {
                    boolean isInserted = DatabaseIO.insertBatchEstimatedArrivalTime(records);
                    if (isInserted) {
                        records.clear();
                    }
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

            logger.info("FINISHED: " + busDataFile);
        }
    }
}
