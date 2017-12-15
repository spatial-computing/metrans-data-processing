package edu.usc.imsc.metrans.gtfsutil;

import com.vividsolutions.jts.geom.LineString;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Our own GTFS storage which includes GtfsDaoImpl and other utility mappings,
 * e.g. ShapeId ==> LineString, TripId ==> StopTimes, TripId ==> ShapeId, RouteId ==> Trips mapping
 * <p>
 * Usage:
 * - Initialize with a Gtfs directory or a {@link GtfsDaoImpl}
 * - Prepare mappings {@link #prepareMappings()}
 */
public class GtfsStore {
    private static final Logger logger = LoggerFactory.getLogger(GtfsStore.class);
    private Map<String, LineString> shapeLineStrings = null; // ShapeId ==> LineString
    private Map<String, ArrayList<StopTime>> tripStopTimes = null; // TripId ==> StopTimes
    private Map<String, Trip> routeMaxLengthTrip = null; // RouteId ==> MaxLengthTrip
    private Map<String, String> tripShape = null; //TripId ==> ShapeId
    private Map<String, ArrayList<Trip>> routeTrips = null; // RouteId ==> Trips
    private GtfsDaoImpl gtfsDao = null;


    public GtfsStore(String gtfsDir) throws IOException {
        logger.info("Start reading gtfs data from " + gtfsDir);
        gtfsDao = GtfsUtil.readGtfsFromDir(gtfsDir);

        prepareMappings();
    }


    public GtfsStore(GtfsDaoImpl gtfsDaoImpl) throws IOException {
        this.gtfsDao = gtfsDaoImpl;

        prepareMappings();
    }


    /**
     * Prepare data mappings:
     * - ShapeId ==> LineString
     * - TripId ==> StopTimes
     * - TripId ==> ShapeId
     * - RouteId ==> Trips
     */
    public void prepareMappings() {
        logger.info("Preparing ShapeId ==> LineString:...");
        shapeLineStrings = GtfsUtil.getShapeLineStringsMapping(gtfsDao);

        logger.info("Preparing TripId ==> StopTimes:...");
        tripStopTimes = GtfsUtil.getTripStopTimesMapping(gtfsDao);

        logger.info("Preparing TripId ==> ShapeId:...");
        tripShape = GtfsUtil.getTripShapeMapping(gtfsDao);

        logger.info("Preparing RouteId ==> Trips:...");
        routeTrips = GtfsUtil.getRouteTripsMapping(gtfsDao);

        logger.info("Preparing RouteId ==> MaxLengthTrip:...");
        routeMaxLengthTrip = prepareMaxLengthTrip();
    }


    /**
     * For each route, find the trip that has maximum length
     *
     * @return mapping from route id to the trip that has maximum length
     */
    private Map<String, Trip> prepareMaxLengthTrip() {
        Map<String, Trip> tmp = new HashMap<>();
        for (Route route : getGtfsDao().getAllRoutes()) {
            Trip trip = findMaxLengthTripForRoute(route);

            tmp.put(route.getId().getId(), trip);
        }

        return tmp;
    }

    /**
     * For a {@code route}, find the trip that has maximum length
     *
     * @param route a route
     * @return the trip that has maximum length
     */
    public Trip findMaxLengthTripForRoute(Route route) {
        double maxLength = -1;
        Trip maxLengthTrip = null;

        for (Trip trip : getGtfsDao().getAllTrips()) {
            if (trip.getRoute().equals(route)) {
                double len = GtfsUtil.calTripLength(this, trip);
                if (maxLength < len) {
                    maxLength = len;
                    maxLengthTrip = trip;
                }
            }
        }

        return maxLengthTrip;
    }


    public Map<String, LineString> getShapeLineStrings() {
        return shapeLineStrings;
    }

    public Map<String, ArrayList<StopTime>> getTripStopTimes() {
        return tripStopTimes;
    }

    public GtfsDaoImpl getGtfsDao() {
        return gtfsDao;
    }

    public Map<String, String> getTripShape() {
        return tripShape;
    }

    public Map<String, ArrayList<Trip>> getRouteTrips() {
        return routeTrips;
    }

    public Map<String, Trip> getRouteMaxLengthTrip() {
        return routeMaxLengthTrip;
    }
}
