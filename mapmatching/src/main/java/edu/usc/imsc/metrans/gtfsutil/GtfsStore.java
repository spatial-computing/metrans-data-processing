package edu.usc.imsc.metrans.gtfsutil;

import com.vividsolutions.jts.geom.LineString;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
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
    private static final Logger logger = LoggerFactory.getLogger(GtfsStore.class);
    private Map<String, LineString> shapeLineStrings = null;
    private Map<String, ArrayList<StopTime>> tripStopTimes = null;
    private Map<String, String> tripShape = null;
    private Map<String, ArrayList<Trip>> routeTrips = null;
    private GtfsDaoImpl gtfsDao = null;


    public GtfsStore(String gtfsDir) throws IOException {
        logger.info("Start reading gtfs data from " + gtfsDir);
        gtfsDao = GtfsUtil.readGtfsFromDir(gtfsDir);
    }


    public GtfsStore(GtfsDaoImpl gtfsDaoImpl) throws IOException {
       this.gtfsDao = gtfsDaoImpl;
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
}
