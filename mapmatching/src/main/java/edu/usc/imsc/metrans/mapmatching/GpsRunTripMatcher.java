package edu.usc.imsc.metrans.mapmatching;


import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Map GPS run(s) to a scheduled trip
 */
public class GpsRunTripMatcher {
    private static final Logger logger = LoggerFactory.getLogger(GpsRunTripMatcher.class);


    /**
     * Split GPS points into different runs and make sure that each run is a single run along a trip.
     * Steps:
     * - Find the max length trip of a round (called Parent trip)
     * - For each GPS point, find the closest bus stop of the trip to the point
     * - Find stop sequence trend (increasing = 1, idle = 0, decreasing = 1) for each GPS point
     *   + trend(p_i) = sign(stop_sequence(p_i) − stop_sequence(p_{i−1}))
     * - Temporarily ignore trend = 0 for a moment, we group all consecutive points that have the same trend value as
     * the same run
     * - Then remove all trend = 0 points at the beginning or end of a run
     * - The trend value is assigned back to the GPS point, but using BUS_DIRECTION = 0 when trend = -1.
     *
     * @param records records of a GPS points, which may consist of many runs
     * @param gtfsStore a GTFS storage
     * @return a schedule {@link Trip} matched to the run, or {@code null} if unable to match or error occurred
     */
    public static ArrayList<ArrayList<BusGpsRecord>> splitRunAndRecoverDirection(
            ArrayList<BusGpsRecord> records, GtfsStore gtfsStore) {
        ArrayList<ArrayList<BusGpsRecord>> runs = new ArrayList<>();

        if (records.isEmpty()) return runs;

        // records is not empty, find route
        Route route = GtfsUtil.getRouteFromShortId(gtfsStore, String.valueOf(records.get(0).getRouteId()));
        if (route == null) return runs;

        // route is found, find parent trip
        Trip trip = gtfsStore.getRouteMaxLengthTrip().get(route.getId().getId());
        if (trip == null) return runs;

        //parent trip is found, find stop-times of that trip
        ArrayList<StopTime> stopTimes = gtfsStore.getTripStopTimes().get(trip.getId().getId());

        //TODO:

        return runs;
    }


    /**
     * Map a GPS run to a scheduled trip:
     * - For all shape of a
     * @param gpsRun records of a GPS run
     * @param gtfsStore a GTFS storage
     * @return a schedule {@link Trip} matched to the run, or {@code null} if unable to match or error occurred
     */
    public static Trip matchGpsRunToScheduledTrip(ArrayList<BusGpsRecord> gpsRun, GtfsStore gtfsStore) {
        Trip matchedTrip = null;

        //TODO:

        return matchedTrip;
    }
}
