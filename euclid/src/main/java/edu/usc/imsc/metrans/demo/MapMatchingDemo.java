package edu.usc.imsc.metrans.demo;

import edu.usc.imsc.metrans.busdata.BusDataIO;
import edu.usc.imsc.metrans.busdata.BusDataPreprocessing;
import edu.usc.imsc.metrans.busdata.BusDataUtil;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.connection.FileIO;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import edu.usc.imsc.metrans.mapmatching.GpsRunTripMatcher;
import edu.usc.imsc.metrans.mapmatching.MapMatchingUtil;
import edu.usc.imsc.metrans.timedata.DelayTimeRecord;
import org.onebusaway.gtfs.model.Route;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static edu.usc.imsc.metrans.delaytime.DelayTimeMain.delayTimeMain;

public class MapMatchingDemo {
    private static final Logger logger = LoggerFactory.getLogger(MapMatchingDemo.class);

    public static ArrayList<String> getFilesWithExtInFolder(String folderPath, String extention) {
        File f = new File(folderPath);

        FilenameFilter extFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(extention);
            }
        };

        File[] files = f.listFiles(extFilter);

        ArrayList<String> res = new ArrayList<>();

        for (File file : files) {
            if (!file.isDirectory()) {
                res.add(file.getPath());
            }
        }


        return res;
    }

    public static void main(String[] args) throws IOException, TransformException {

        String dataDir = "../../";

        String gtfsDir = dataDir + "gtfs_bus_171212";
        GtfsStore gtfsStore = new GtfsStore(gtfsDir);

        String gpsDir = dataDir + "data_160101_171010/";
        ArrayList<String> files = getFilesWithExtInFolder(gpsDir, ".csv");
        Collections.sort(files);

        String startFileName = gpsDir + "Bus51.csv";

        ArrayList<BusGpsRecord> records = new ArrayList<>();
        ArrayList<ArrayList<BusGpsRecord>> allRuns =  new ArrayList<>();
        ArrayList<ArrayList<BusGpsRecord>> allSplitRuns;
        ArrayList<ArrayList<BusGpsRecord>> nonSplitRuns;
        ArrayList<ArrayList<BusGpsRecord>> beSplitRuns;
        ArrayList<ArrayList<BusGpsRecord>> orgNonSplitRuns;
        ArrayList<ArrayList<BusGpsRecord>> orgBeSplitRuns;
        ArrayList<DelayTimeRecord> estimatedArrivalTimeResult = new ArrayList<>();

        for (String busDataFile: files) {
            if (busDataFile.compareTo(startFileName) < 0)
                continue;

            logger.info("Cleaning...");
            records.clear();
            allRuns.clear();
            allSplitRuns = new ArrayList<>();
            nonSplitRuns = new ArrayList<>();
            beSplitRuns = new ArrayList<>();
            orgNonSplitRuns = new ArrayList<>();
            orgBeSplitRuns = new ArrayList<>();
            estimatedArrivalTimeResult.clear();

            logger.info("Getting data from CSV file: " + busDataFile);


            records = BusDataIO.readBusGpsRecordsFromFile(busDataFile);
            logger.info(records.size() + " records");

            allRuns = BusDataPreprocessing.preprocessBusGpsDataIntoRuns(records);

            logger.info("Removing two few records runs");
            MapMatchingUtil.removeTooFewRecordsRun(allRuns, 5);
            BusDataUtil.printRunsStatistics(allRuns);

            int splitCount = 0;

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

            if (allRuns.isEmpty())
                continue;

//          BusDelayMain.busDelayMain(allRuns, gtfsStore);
            // Get routeId
            Route route = GtfsUtil.getRouteFromShortId(gtfsStore, String.valueOf(allRuns.get(0).get(0).getRouteId()));
            estimatedArrivalTimeResult = delayTimeMain(route, allRuns, gtfsStore);

            logger.info("WRITE BEGIN");
            String outDir = dataDir + "estimated_data_160101_171010/";
            String fileName = outDir + "estimatedArrivalTime." + route.getId().getId() + ".csv";
            FileIO.writeFile(route, estimatedArrivalTimeResult, fileName);
//          DatabaseIO.writeDatabase(route, estimatedArrivalTimeResult);
            logger.info("FINISHED: " + busDataFile);
        }
    }
}
