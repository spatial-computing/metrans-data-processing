package edu.usc.imsc.metrans.demo;

import edu.usc.imsc.metrans.busdata.BusDataIO;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.busdata.BusDataPreprocessing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BusDataIOMain {
    private static final Logger logger = LoggerFactory.getLogger(BusDataIOMain.class);

    public static void main(String[] args) {
        logger.info("Getting data from CSV file");
        ArrayList<BusGpsRecord> records = BusDataIO.readBusGpsRecordsFromFile("data/busdata/all.csv");
        logger.info(records.size() + " records");

        BusDataPreprocessing.preprocessBusGpsDataIntoRuns(records);
    }
}
