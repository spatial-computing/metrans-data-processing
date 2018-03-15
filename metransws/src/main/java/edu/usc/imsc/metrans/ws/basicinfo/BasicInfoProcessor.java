package edu.usc.imsc.metrans.ws.basicinfo;

import edu.usc.imsc.metrans.database.DatabaseIO;
import edu.usc.imsc.metrans.gtfsutil.GtfsStoreProvider;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import edu.usc.imsc.metrans.utils.Utils;
import edu.usc.imsc.metrans.ws.storage.DataCache;
import edu.usc.imsc.metrans.ws.storage.DbItemInfo;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;

import java.util.ArrayList;

public class BasicInfoProcessor {
    public static OverallBasicInfo getOverallBasicInfo() {
        OverallBasicInfo info = new OverallBasicInfo();

        Double avgDeviation = DataCache.getValue(DataCache.AVG_DEVIATION_OVERALL);
        if (avgDeviation == null)
            avgDeviation = (double) Utils.ERROR_VALUE;

        info.setAvgDeviation(avgDeviation);
        Double reliability = DataCache.getValue(DataCache.RELIABILITY_OVERALL);
        if (reliability == null)
            reliability = (double)Utils.ERROR_VALUE;
        info.setReliability(reliability);

        info.setNumBusRoutes(GtfsStoreProvider.getGtfsStore().getGtfsDao().getAllRoutes().size());
        info.setNumBusStops(GtfsStoreProvider.getGtfsStore().getGtfsDao().getAllStops().size());
        info.setNumDataPoints(DatabaseIO.getEstimatedDataPoints());
        info.setStartTime(DatabaseIO.getMinMaxTime(true));
        info.setEndTime(DatabaseIO.getMinMaxTime(false));

        return  info;
    }

    public static RouteBasicInfo getRouteBasicInfo(int routeId) {
        RouteBasicInfo info = new RouteBasicInfo();

        // avg deviation
        ArrayList<DbItemInfo> avgDeviations = DataCache.getAvgDeviationsOfAllRoutes();
        DbItemInfo avgDeviation = null;
        for (DbItemInfo obj: avgDeviations) {
            if (obj.getRouteId() == routeId) {
                avgDeviation = obj;
                break;
            }
        }

        if (avgDeviation != null) {
            info.setAvgDeviation(avgDeviation.getTimeDiff());
            info.setReliability(DatabaseIO.getReliability(routeId));

            info.setAvgDeviationRank(avgDeviation.getRank());

            //get num trips of this route
            try {
                Route route = GtfsUtil.getRouteFromShortId(GtfsStoreProvider.getGtfsStore(), String.valueOf(routeId));
                if (route != null) {
                    ArrayList<Trip> tripsOfRoute = GtfsStoreProvider.getGtfsStore().getRouteTrips().get(route.getId().getId());
                    info.setNumTripsPerDay(tripsOfRoute.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            info.setNumDataPointsPerDay(-1);
        } else {
            System.err.println("Unable to find route average deviation for route " + routeId);
        }

        return info;
    }

    public static RouteStopBasicInfo getRouteStopBasicInfo(int routeId, int stopId) {
        RouteStopBasicInfo info = new RouteStopBasicInfo();

        // avg deviation
        ArrayList<DbItemInfo> avgDeviations = DataCache.getAvgDeviationsOfStopsOfRoute(Long.valueOf(routeId));
        DbItemInfo avgDeviation = null;
        for (DbItemInfo obj: avgDeviations) {
            if (obj.getStopId() == stopId) {
                avgDeviation = obj;
                break;
            }
        }
        if (avgDeviation != null) {
            info.setAvgDeviation(avgDeviation.getTimeDiff());
            info.setReliability(DatabaseIO.getReliability(routeId, stopId));

            info.setAvgDeviationRank(avgDeviation.getRank());
            Stop stop = GtfsStoreProvider.getGtfsStore().getStopMap().get(String.valueOf(stopId));
            if (stop != null) {
                info.setStopName(stop.getName());
            }

            //waiting time
            info.setWaitingTimeEstimation(Utils.ERROR_VALUE);
            ArrayList<DbItemInfo> avgMinPosDelays = DataCache.getAvgMinPosDelaysOfStopsOfRoute(Long.valueOf(routeId));
            for (DbItemInfo avgMinPosDelay : avgMinPosDelays) {
                if (avgMinPosDelay.getStopId() == stopId) {
                    info.setWaitingTimeEstimation(avgMinPosDelay.getTimeDiff());
                    break;
                }
            }

        } else {
            System.err.println("Unable to find route average deviation for route " + routeId + ", stop " + stopId);
        }

        return info;
    }

    public static RouteStopTripBasicInfo getRouteStopTripBasicInfo(int routeId, int stopId, int tripId) {
        RouteStopTripBasicInfo info = new RouteStopTripBasicInfo();

        DbItemInfo avgDeviation = DatabaseIO.getAvgDeviationOfTripOfStopOfRoute(routeId, stopId, tripId);
        if (avgDeviation != null) {
            info.setAvgDeviation(avgDeviation.getTimeDiff());
            info.setReliability(DatabaseIO.getReliability(routeId, stopId, tripId));
        } else {
            info.setAvgDeviation(Utils.ERROR_VALUE);
            System.err.println("Unable to find average deviation for route " + routeId
                    + ", stop " + stopId + ", trip " + tripId);
        }

        return info;
    }
}
