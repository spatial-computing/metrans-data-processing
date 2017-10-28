package edu.usc.imsc.metrans.mapmatching;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import infolab.usc.geo.util.WGS2MetricTransformer;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Map GPS run(s) to a scheduled trip
 */
public class GpsRunTripMatcher {
    private static final Logger logger = LoggerFactory.getLogger(GpsRunTripMatcher.class);
    public static final double BUS_GPS_OUTLIER_DISTANCE = 400; // distance from Bus GPS point to a project point to be considered as an outlier


    /**
     * Split GPS points into different runs and make sure that each run is a single run along a trip.
     * Steps:
     * - Find the max length trip of a round (called Parent trip)
     * - For each GPS point:
     *   + project to the shape
     *   + find the closest bus stop of the trip to the projected point
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
            ArrayList<BusGpsRecord> records, GtfsStore gtfsStore) throws TransformException {
        ArrayList<ArrayList<BusGpsRecord>> runs = new ArrayList<>();

        if (records.isEmpty()) return runs;

        // records is not empty, find route
        Route route = GtfsUtil.getRouteFromShortId(gtfsStore, String.valueOf(records.get(0).getRouteId()));
        if (route == null) return runs;

        // route is found, find parent trip
        Trip trip = gtfsStore.getRouteMaxLengthTrip().get(route.getId().getId());
        if (trip == null) return runs;
        String tripId = trip.getId().getId();

        //parent trip is found, find stop-times of that trip
        ArrayList<StopTime> stopTimes = gtfsStore.getTripStopTimes().get(tripId);
        ArrayList<Coordinate> stopCoords = new ArrayList<>();
        for (StopTime stopTime : stopTimes) {
            Coordinate coordinate = new Coordinate(stopTime.getStop().getLon(), stopTime.getStop().getLat());
            coordinate = WGS2MetricTransformer.LATransformer.fromWGS84(coordinate);
            stopCoords.add(coordinate);
        }

        // get the shape of the trip
        String shapeId = gtfsStore.getTripShape().get(tripId);
        LineString tripShape = gtfsStore.getShapeLineStrings().get(shapeId);
        // the tripShape should already be transformed before
        LocationIndexedLine indexedLine = new LocationIndexedLine(tripShape);

        // get closed stop to the projected point
        ArrayList<StopTime> closestStops = new ArrayList<>();
        ArrayList<BusGpsRecord> outlierRemovedRecords = new ArrayList<>();

        for (BusGpsRecord record : records) {
            Coordinate busCoordinate = new Coordinate(record.getLon(), record.getLat());
            busCoordinate = WGS2MetricTransformer.LATransformer.fromWGS84(busCoordinate);
            Coordinate projectPoint = MapProjector.project(busCoordinate, indexedLine);

            // find closest stop : NOTE: nearest neighbor implementation instead of linear search
            // if the distance to the closes stop is larger than a threshold, it is not counted
            int closestStopIndex = findClosestPointIndex(stopCoords, projectPoint);
            if (0 <= closestStopIndex) {
                closestStops.add(stopTimes.get(closestStopIndex));
                outlierRemovedRecords.add(record);
            }
        }

        if (outlierRemovedRecords.isEmpty()) return runs;

        // find trending
        int[] trend = new int[outlierRemovedRecords.size()];
        trend[0] = 0;
        for (int i = 1; i < outlierRemovedRecords.size(); i++) {
            int stopSequenceDiff = closestStops.get(i).getStopSequence() - closestStops.get(i - 1).getStopSequence();
            trend[i] = Integer.signum(stopSequenceDiff);
        }

        /*
         * group records to runs based on the trend:
         * + temporarily ignore trend 0
         * + startRunIndex = the first non-zero trend
         * + nextRunIndex = the smallest index i has a different trend value from startRunIndex
         * + [startRunIndex, nextRunIndex - 1] are 1 run
         * + remove trailing 0-trend records
         * + if trend[startRunIndex - 1] == 0, keep it,
         *      - because that means the first record in this run already pass 1 stop
         * + assign startRunIndex = nextRunIndex and repeat
         */
        // remove heading 0s
        int startRunIndex = 0;
        while (startRunIndex < trend.length && trend[startRunIndex] == 0) {
            startRunIndex += 1;
        }
        //now we have startRunIndex as the first non-zero trend

        while (startRunIndex < trend.length) {
            // find a run starting at startRunIndex
            int nextRunIndex = startRunIndex + 1;
            while (nextRunIndex < trend.length
                    && (trend[startRunIndex] == trend[nextRunIndex] || trend[nextRunIndex] == 0))
                nextRunIndex += 1;
            //[startRunIndex, nextRunIndex - 1] is the longest sequence of records starting at startRunIndex
            // that is either non-decreasing or non-increasing
            // which is a run
            ArrayList<BusGpsRecord> aRun = new ArrayList<>(outlierRemovedRecords.subList(startRunIndex, nextRunIndex));

            //refine the run by
            //remove trailing 0-trend records in each run (since a run is always start with non-zero trend
            for (int i = nextRunIndex - 1; startRunIndex <= i; i--)
                if (trend[i] == 0)
                    aRun.remove(aRun.size() - 1);
                else
                    break;

            if (!aRun.isEmpty()) {
                // keep the previous startRunIndex - 1 if its trend == 0
                if (0 < startRunIndex && trend[startRunIndex - 1] == 0)
                    aRun.add(0, outlierRemovedRecords.get(startRunIndex - 1));
                // assign new direction of a record as the trend value but use BUS_DIRECTION = 0 when trend = -1
                int newDirection = trend[startRunIndex];
                if (newDirection == -1)
                    newDirection = 0;
                for (BusGpsRecord record : aRun)
                    record.setBusDirection(newDirection);

                runs.add(aRun);
            }
            startRunIndex = nextRunIndex;
        }


        return runs;
    }


    /**
     * Find index of the closest point in {@code geom1} to the {@code point}
     *
     * Return -1 if the distance from Bus GPS point to a project point is larger than {@link #BUS_GPS_OUTLIER_DISTANCE}
     * @param geom1 list of points
     * @param point the point to calculate closest point
     * @return the index of the closest point in {@code geom1} to the {@code point} or -1 if error occurs
     */
    public static int findClosestPointIndex(ArrayList<Coordinate> geom1, Coordinate point) {
        double minDist = Double.POSITIVE_INFINITY;
        int minIndex = -1;
        for (int i = 0; i < geom1.size(); i++) {
            Coordinate coordinate = geom1.get(i);
            double dist = point.distance(coordinate);
            if (dist < minDist) {
                minDist = dist;
                minIndex = i;
            }
        }

        if (BUS_GPS_OUTLIER_DISTANCE < minDist)
            minIndex = -1;

        return minIndex;
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
