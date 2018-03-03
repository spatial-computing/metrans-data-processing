package edu.usc.imsc.metrans.gtfsutil;

import com.vividsolutions.jts.geom.LineString;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Our own GTFS storage which includes GtfsDaoImpl and other utility mappings,
 * e.g. ShapeId ==> LineString, TripId ==> StopTimes, TripId ==> ShapeId, RouteId ==> Trips mapping
 *
 * Usage:
 *  - Initialize with a Gtfs directory or a {@link GtfsDaoImpl}
 *  - Prepare mappings {@link #prepareMappings()}
 */
public class GtfsStore {
    private Map<String, LineString> shapeLineStrings = null;
    private Map<String, ArrayList<StopTime>> tripStopTimes = null;
    private Map<String, Trip> routeMaxLengthTrip = null;
    private Map<String, String> tripShape = null;
    private Map<String, ArrayList<Trip>> routeTrips = null;
    private Map<String, Stop> stopMap = null;
    private Map<String, ArrayList<ShapePoint>> shapeShapePoints = null;
    private GtfsDaoImpl gtfsDao = null;


    public GtfsStore(String gtfsDir) throws IOException {
        System.out.println("Start reading gtfs data from " + gtfsDir);
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
        System.out.println("Preparing ShapeId ==> ShapePoints:...");
        shapeShapePoints = GtfsUtil.getShapeShapePointsMapping(gtfsDao);

        System.out.println("Preparing ShapeId ==> LineString:...");
        shapeLineStrings = GtfsUtil.getShapeLineStringsMapping(gtfsDao);

        System.out.println("Preparing TripId ==> StopTimes:...");
        tripStopTimes = GtfsUtil.getTripStopTimesMapping(gtfsDao);

        System.out.println("Preparing TripId ==> ShapeId:...");
        tripShape = GtfsUtil.getTripShapeMapping(gtfsDao);

        System.out.println("Preparing RouteId ==> Trips:...");
        routeTrips = GtfsUtil.getRouteTripsMapping(gtfsDao);

        System.out.println("Preparing RouteId ==> MaxLengthTrip:...");
        routeMaxLengthTrip = prepareMaxLengthTrip();

        System.out.println("Preparing StopId ==> Stop:...");
        stopMap = GtfsUtil.getStopMap(gtfsDao);
    }


    /**
     * For each route, find the trip that has maximum length
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

    public Map<String, Stop> getStopMap() {
        return stopMap;
    }

    public Map<String, ArrayList<ShapePoint>> getShapeShapePoints() {
        return shapeShapePoints;
    }
}
