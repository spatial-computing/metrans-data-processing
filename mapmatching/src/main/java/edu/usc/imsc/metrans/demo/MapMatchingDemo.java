package edu.usc.imsc.metrans.demo;

import edu.usc.imsc.metrans.busdata.BusDataIO;
import edu.usc.imsc.metrans.busdata.BusDataPreprocessing;
import edu.usc.imsc.metrans.busdata.BusDataUtil;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.mapmatching.GpsRunTripMatcher;
import edu.usc.imsc.metrans.mapmatching.MapMatchingUtil;
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

        logger.info("Removing two few records runs");
        MapMatchingUtil.removeTooFewRecordsRun(allRuns, 5);
        BusDataUtil.printRunsStatistics(allRuns);

        String gtfsDir = "data/gtfs_bus_160617";
        GtfsStore gtfsStore = new GtfsStore(gtfsDir);

        int splitCount = 0;
        ArrayList<ArrayList<BusGpsRecord>> allSplitRuns = new ArrayList<>();
        ArrayList<ArrayList<BusGpsRecord>> nonSplitRuns = new ArrayList<>();
        ArrayList<ArrayList<BusGpsRecord>> beSplitRuns = new ArrayList<>();
        ArrayList<ArrayList<BusGpsRecord>> orgNonSplitRuns = new ArrayList<>();
        ArrayList<ArrayList<BusGpsRecord>> orgBeSplitRuns = new ArrayList<>();

        GpsRunTripMatcher.setCounts(0, 0);

        for (ArrayList<BusGpsRecord> gpsRun : allRuns) {
            ArrayList<ArrayList<BusGpsRecord>> splitRuns = GpsRunTripMatcher.splitRunAndRecoverDirection(gpsRun, gtfsStore);
            //logger.info("Split runs size = " + splitRuns.size());

            if (1 < splitRuns.size()) {
//                logger.info("split runs ***************************************************");
//                for (ArrayList<BusGpsRecord> aRun: splitRuns) {
//                    logger.info("A new run: ---------------------------");
//                    for (BusGpsRecord record : aRun)
//                        logger.info(record.toString());
//                }

                splitCount += 1;
                beSplitRuns.addAll(splitRuns);
                orgBeSplitRuns.add(gpsRun);
            } else {
                nonSplitRuns.addAll(splitRuns);
                orgNonSplitRuns.add(gpsRun);
            }

            allSplitRuns.addAll(splitRuns);
        }

        logger.info("outlierCount = " + GpsRunTripMatcher.getOutlierCount());
        logger.info("zeroTrendCount = " + GpsRunTripMatcher.getZeroTrendCount());

        logger.info("orgNonSplitRuns");
        BusDataUtil.printRunsStatistics(orgNonSplitRuns);
        logger.info("nonSplitRuns");
        BusDataUtil.printRunsStatistics(nonSplitRuns);
        logger.info("orgBeSplitRuns");
        BusDataUtil.printRunsStatistics(orgBeSplitRuns);
        logger.info("beSplitRuns");
        BusDataUtil.printRunsStatistics(beSplitRuns);

        logger.info(splitCount + " runs split");

        allRuns.clear();
        allRuns = allSplitRuns;
        logger.info("allRuns after splitting");
        BusDataUtil.printRunsStatistics(allRuns);

        logger.info("allRuns after runs with too few records");
        MapMatchingUtil.removeTooFewRecordsRun(allRuns, 5);
        BusDataUtil.printRunsStatistics(allRuns);


        logger.info("DONE");
    }
}
