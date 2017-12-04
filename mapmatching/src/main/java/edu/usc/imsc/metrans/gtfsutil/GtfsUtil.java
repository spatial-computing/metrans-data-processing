package edu.usc.imsc.metrans.gtfsutil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import edu.usc.imsc.metrans.mapmatching.MapMatchingUtil;
import infolab.usc.geo.util.WGS2MetricTransformer;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.onebusaway.csv_entities.EntityHandler;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Utilities for GTFS data
 */
public class GtfsUtil {
    private static final Logger logger = LoggerFactory.getLogger(GtfsUtil.class);

    /**
     * Read GTFS data from {@code inputDir}
     * @param inputDir gtfs input directory with extracted files
     * @return a store for GTFS data
     * @throws IOException
     */
    public static GtfsDaoImpl readGtfsFromDir(String inputDir) throws IOException {
        GtfsReader reader = new GtfsReader();
        reader.setInputLocation(new File(inputDir));

        /**
         * You can register an entity handler that listens for new objects as they
         * are read
         */
//        reader.addEntityHandler(new GtfsEntityHandler());

        /**
         * Or you can use the internal entity store, which has references to all the
         * loaded entities
         */
        GtfsDaoImpl store = new GtfsDaoImpl();
        reader.setEntityStore(store);

        reader.run();

        return store;
    }

    private static class GtfsEntityHandler implements EntityHandler {

        public void handleEntity(Object bean) {
            if (bean instanceof Stop) {
                Stop stop = (Stop) bean;
                System.out.println("stop: " + stop.getName());
            }
        }
    }

    /**
     * Get a map from ShapeId to {@link LineString} representation of the shape.
     *
     * The line string is already transformed using fromWGS84
     * @param store GTFS store
     * @return a map from ShapeId to LineString representation of the shape from {@code store}
     */
    public static Map<String, LineString> getShapeLineStringsMapping(GtfsDaoImpl store) {
        Map<String, LineString> shapeLineStrings = new HashMap<>();

        // Transform GPS to metric space.

        Map<String, ArrayList<ShapePoint>> shapeShapePoints = getShapeShapePointsMapping(store);

        GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
        try {
            for (Map.Entry<String, ArrayList<ShapePoint>> entry : shapeShapePoints.entrySet()) {
                List<Coordinate> points = new ArrayList<>();
                for (ShapePoint sp : entry.getValue()) {
                    points.add(new Coordinate(sp.getLon(), sp.getLat()));
                }
                Coordinate[] coordinates = new Coordinate[points.size()];
                LineString lineString = factory.createLineString(points.toArray(coordinates));
                lineString = (LineString) WGS2MetricTransformer.LATransformer.fromWGS84(lineString);
                shapeLineStrings.put(entry.getKey(), lineString);
            }
        } catch (Exception ex) {
            logger.error("Unable to get shape to LineString mapping: ", ex);
        }

        return shapeLineStrings;
    }

    /**
     * From a GTFS store, get a map from ShapeId to points of that shape.
     * Points in each shape is sorted by theirs sequences
     * @param store GTFS store
     * @return get a map from ShapeId to points of that shape
     */
    public static Map<String, ArrayList<ShapePoint>> getShapeShapePointsMapping(GtfsDaoImpl store) {
        Map<String, ArrayList<ShapePoint> > shapeShapePoints = new HashMap<>();
        Collection<ShapePoint> shapePoints = store.getAllShapePoints();
        for (ShapePoint sp : shapePoints) {
            String shapeId = sp.getShapeId().getId();

            if (shapeShapePoints.containsKey(shapeId)) {
                shapeShapePoints.get(shapeId).add(sp);
            } else {
                ArrayList<ShapePoint> points = new ArrayList<>();
                points.add(sp);
                shapeShapePoints.put(shapeId, points);
            }
        }

        Comparator<ShapePoint> shapePointSequenceComparator = new Comparator<ShapePoint>() {
            @Override
            public int compare(ShapePoint o1, ShapePoint o2) {
                return o1.getSequence() - o2.getSequence();
            }
        };

        for (Map.Entry<String, ArrayList<ShapePoint>> entry : shapeShapePoints.entrySet()) {
            entry.getValue().sort(shapePointSequenceComparator);
        }

        return shapeShapePoints;
    }


    /**
     * Get a map of TripId to StopTimes of that trip, sorted by stop sequence
     * @param store GTFS store
     * @return a map of TripId to StopTimes of that trip
     */
    public static Map<String, ArrayList<StopTime>> getTripStopTimesMapping(GtfsDaoImpl store) {
        Map<String, ArrayList<StopTime>> tripStopTimes = new HashMap<>();

        for (StopTime stopTime: store.getAllStopTimes()) {
            String tripId = stopTime.getTrip().getId().getId();
            String key = tripId;

            if (tripStopTimes.containsKey(key)) {
                tripStopTimes.get(key).add(stopTime);
            } else {
                ArrayList<StopTime> stopTimes = new ArrayList<>();
                stopTimes.add(stopTime);

                tripStopTimes.put(key, stopTimes);
            }
        }

        //sort by stop sequence
        Comparator<StopTime> stopTimeSequenceComparator = new Comparator<StopTime>() {
            @Override
            public int compare(StopTime o1, StopTime o2) {
                return o1.getStopSequence() - o2.getStopSequence();
            }
        };

        for (Map.Entry<String, ArrayList<StopTime>> entry : tripStopTimes.entrySet()) {
            entry.getValue().sort(stopTimeSequenceComparator);
        }

        return tripStopTimes;
    }


    /**
     * Get TripId ==> ShapeId mapping
     * @param store GTFS store
     * @return TripId ==> ShapeId mapping
     */
    public static Map<String, String> getTripShapeMapping(GtfsDaoImpl store) {
        Map<String, String> tripShapeMap = new HashMap<>();

        for (Trip trip : store.getAllTrips()) {
            tripShapeMap.put(trip.getId().getId(), trip.getShapeId().getId());
        }

        return tripShapeMap;
    }


    /**
     * Get RouteId ==> Trips mapping
     * @param store GTFS store
     * @return RouteId ==> Trips mapping
     */
    public static Map<String, ArrayList<Trip>> getRouteTripsMapping(GtfsDaoImpl store) {
        Map<String, ArrayList<Trip>> routeTripsMap = new HashMap<>();

        for (Trip trip : store.getAllTrips()) {
            String routeId = trip.getRoute().getId().getId();
            String key = routeId;

            if (routeTripsMap.containsKey(key)) {
                routeTripsMap.get(key).add(trip);
            } else {
                ArrayList<Trip> trips = new ArrayList<>();
                trips.add(trip);

                routeTripsMap.put(key, trips);
            }
        }

        return routeTripsMap;
    }


    /**
     * Calculate length of a trip based on its shape
     * @param trip the trip
     * @return length of a trip based on its shape
     */
    public static double calTripLength(GtfsStore store, Trip trip) {
        double len = -1;

        try {
            String shapeId = trip.getShapeId().getId();
            LineString lineString = store.getShapeLineStrings().get(shapeId);
            len = lineString.getLength();
        } catch (Exception ex) {
            logger.error("Error calculating length of trip " + trip.toString(), ex);
        }

        return len;
    }


    /**
     * Get the short version of a route id, e.g. 10-13097 becomes 10, DSE-HG remains DSE-HG.
     *
     * Algo: If the route id starts with a number, return the number before "-", else return itself.
     * @param routeId a full route id
     * @return the short version of a route id
     */
    public static String toShortRouteId(String routeId) {
        String shortName = "";
        if (Character.isDigit(routeId.charAt(0))) {
            // start with a number
            for (int i = 0 ; i < routeId.length(); i++) {
                if (routeId.charAt(i) == '-') {
                    shortName = routeId.substring(0, i);
                }
            }
        } else
            shortName = routeId;

        return shortName;
    }


    /**
     * Get route from a short id (such as 10)
     * @param gtfsStore GTFS store
     * @param shortId route short id
     * @return return the route from short id or {@code null} if no route found
     */
    public static Route getRouteFromShortId(GtfsStore gtfsStore, String shortId) {
        for (Route route : gtfsStore.getGtfsDao().getAllRoutes()) {
            if (toShortRouteId(route.getId().getId()).equals(shortId))
                return route;
        }
        return null;
    }
}
