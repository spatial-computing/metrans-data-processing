package edu.usc.imsc.metrans.demo;

import edu.usc.imsc.metrans.busdata.BusDataIO;
import edu.usc.imsc.metrans.busdata.BusDataPreprocessing;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.mapmatching.GpsRunTripMatcher;
import org.onebusaway.gtfs.model.Trip;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class MapMatchingDemo {
    private static final Logger logger = LoggerFactory.getLogger(MapMatchingDemo.class);

    public static void main(String[] args) throws IOException, TransformException {
        logger.info("Getting data from CSV file");
        String busDataFile = "data/busdata/Bus10.csv";
        ArrayList<BusGpsRecord> records = BusDataIO.readBusGpsRecordsFromFile(busDataFile);
        logger.info(records.size() + " records");

        ArrayList<ArrayList<BusGpsRecord>> allRuns = BusDataPreprocessing.preprocessBusGpsDataIntoRuns(records);

        String gtfsDir = "data/gtfs_bus_160617";
        GtfsStore gtfsStore = new GtfsStore(gtfsDir);

        for (ArrayList<BusGpsRecord> gpsRun : allRuns) {
            ArrayList<ArrayList<BusGpsRecord>> splitRuns = GpsRunTripMatcher.splitRunAndRecoverDirection(gpsRun, gtfsStore);
            //logger.info("Split runs size = " + splitRuns.size());

            if (1 < splitRuns.size()) {
                logger.info("split runs ***************************************************");
                for (ArrayList<BusGpsRecord> aRun: splitRuns) {
                    logger.info("A new run: ---------------------------");
                    for (BusGpsRecord record : aRun)
                        logger.info(record.toString());
                }
            }
        }

        logger.info("DONE");
    }
}
