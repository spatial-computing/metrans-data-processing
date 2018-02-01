package edu.usc.imsc.metrans.mapmatching;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MapMatchingUtil {
    private static final Logger logger = LoggerFactory.getLogger(MapMatchingUtil.class);

    /**
     * Remove runs having too few number of records
     * @param runs list of runs
     * @param countThreshold the minimum number of records a run should have
     * @return {@code true} if this list changed as a result of the call
     */
    public static boolean removeTooFewRecordsRun(ArrayList<ArrayList<BusGpsRecord>> runs, int countThreshold) {
        ArrayList<ArrayList<BusGpsRecord>> toRemoveRuns = new ArrayList<>();
        for (ArrayList<BusGpsRecord> aRun : runs) {
            if (aRun.size() < countThreshold)
                toRemoveRuns.add(aRun);
        }

        return runs.removeAll(toRemoveRuns);
    }
}
