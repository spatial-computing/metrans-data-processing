package edu.usc.imsc.metrans.gtfsutil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.onebusaway.csv_entities.EntityHandler;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.serialization.GtfsReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Utilities for GTFS data
 */
public class GtfsUtil {

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
     * Get a map from ShapeId to {@link LineString} representation of the shape
     * @param store GTFS store
     * @return a map from ShapeId to LineString representation of the shape from {@code store}
     */
    public static Map<String, LineString> getShapeLineStringsMapping(GtfsDaoImpl store) {
        Map<String, LineString> shapeLineStrings = new HashMap<>();

        Map<String, ArrayList<ShapePoint>> shapeShapePoints = getShapeShapePointsMapping(store);

        GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
        for (Map.Entry<String, ArrayList<ShapePoint>> entry : shapeShapePoints.entrySet()) {
            List<Coordinate> points = new ArrayList<>();
            for (ShapePoint sp : entry.getValue()) {
                points.add(new Coordinate(sp.getLon(), sp.getLat()));
            }
            Coordinate[] coordinates = new Coordinate[points.size()];
            LineString lineString = factory.createLineString(points.toArray(coordinates));
            shapeLineStrings.put(entry.getKey(), lineString);
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
}
