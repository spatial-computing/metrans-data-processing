package edu.usc.imsc.metrans.arrivaltimeestimators;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.timedata.ArrivalTimeEstRecord;
import edu.usc.imsc.metrans.timedata.TripStartTimeEndTime;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static edu.usc.imsc.metrans.arrivaltimeestimators.ClosestTripCalculator.findClosestTrips;
import static edu.usc.imsc.metrans.arrivaltimeestimators.SchedulePreprocessing.*;
import static edu.usc.imsc.metrans.arrivaltimeestimators.Util.*;

import java.util.ArrayList;
import java.util.Map;

public class ArrivalTimeEstimator {
    private static final Logger logger = LoggerFactory.getLogger(ArrivalTimeEstimator.class);

    /**
     * Estimate arrival time for all GPS runs of a route
     * @param route a route
     * @param allRuns all of the route
     * @param gtfsStore GTFS store
     * @return the estimated arrival time records
     */
    public static ArrayList<ArrivalTimeEstRecord> estimateArrivalTime(Route route, ArrayList<ArrayList<BusGpsRecord>> allRuns, GtfsStore gtfsStore) {

        logger.info("Arrival Time Estimation starts");
        ArrayList<ArrivalTimeEstRecord> arrivalTimeEstRecords = new ArrayList<>();
        if (allRuns.size() == 0)
            return arrivalTimeEstRecords;

        // Get all trips for that route
        ArrayList<Trip> tripsOfRoute = getTripsOfRoute(route, gtfsStore);
        logger.info("Total " + tripsOfRoute.size() + " trips");

        // Get all stop times for that route
        Map<String, ArrayList<StopTime>> tripToStopTimesOfRoute = getSchedulesOfRoute(tripsOfRoute, gtfsStore);

        Integer numStopTimes = 0;
        for (String trip: tripToStopTimesOfRoute.keySet()) {
            numStopTimes += tripToStopTimesOfRoute.get(trip).size();
        }
        logger.info("Total " + numStopTimes + " StopTimes");

        // Get start time and end time of each trip
        Map<String, TripStartTimeEndTime> tripStartTimeEndTimes = getTripStartTimeEndTimes(tripToStopTimesOfRoute);

        for (ArrayList<BusGpsRecord> run : allRuns) {

            // Filter on time interval
            Map<String, ArrayList<StopTime>> candidateTrips
                    = getCandidateTrips(run, tripToStopTimesOfRoute, tripStartTimeEndTimes);

            // Get the closest trip candidates based on distance
            Map<String, ArrayList<StopTime>> closestCandidateTrips = findClosestTrips(run, candidateTrips);

            if (closestCandidateTrips.size() != 0) {
                arrivalTimeEstRecords.addAll(estimateArrivalTime(run, closestCandidateTrips));
            }
        }

        return arrivalTimeEstRecords;

    }

    /**
     * Estimate arrival time of a GPS run
     * @param run a GPS run
     * @param candidateTrips candidate trips
     * @return the estimated arrival times
     */
    public static ArrayList<ArrivalTimeEstRecord> estimateArrivalTime(ArrayList<BusGpsRecord> run,
                                                                      Map<String, ArrayList<StopTime>> candidateTrips) {
        ArrayList<ArrivalTimeEstRecord> arrivalTimeEstRecords =  new ArrayList<>();
        if (run.isEmpty())
            return arrivalTimeEstRecords;

        int busId = run.get(0).getBusId();

        // Compute for all stop times
        for(String trip : candidateTrips.keySet()) {
            ArrayList<StopTime> stopTimes = candidateTrips.get(trip);
            for(int i = 0; i < stopTimes.size(); i++) {
                StopTime stopTime = stopTimes.get(i);

                // Find ids of GPS records so that the bus runs from gpsId1 to gpsId2
                int gpsId1 = findClosestGPSToStop(run, stopTime.getStop());
                int gpsId2 = gpsId1 + 1;

                if (gpsId1 == 0)
                    gpsId2 = gpsId1 + 1;
                else if (gpsId1 == run.size() - 1)
                    gpsId2 = gpsId1 - 1;
                else {
                    double dis1 = getDistance(run.get(gpsId1 - 1).getLon(), run.get(gpsId1 - 1).getLat(),
                            stopTime.getStop().getLon(), stopTime.getStop().getLat());

                    double dis2 = getDistance(run.get(gpsId1 + 1).getLon(), run.get(gpsId1 + 1).getLat(),
                            stopTime.getStop().getLon(), stopTime.getStop().getLat());

                    if (dis1 <= dis2) gpsId2 = gpsId1 - 1;
                    if (dis1 > dis2) gpsId2 = gpsId1 + 1;
                }

                // estimate time and delay time
                long gps1Time = Util.getSecondsFromNoonMinus12Hours(run.get(gpsId1).getBusLocationTime());
                long gps2Time = Util.getSecondsFromNoonMinus12Hours(run.get(gpsId2).getBusLocationTime());
                long estimatedTime = estimateArrivalTime(
                        run.get(gpsId1), run.get(gpsId2), gps1Time, gps2Time, stopTime.getStop());
                long delay = estimatedTime - stopTime.getArrivalTime();

                // Filter the one that delay too much or arrival too early
                if (delay >= -DELAY_TIME_THRESHOLD && delay <= DELAY_TIME_THRESHOLD) {
//                    ZonedDateTime estimatedArrivalZDT
//                            = doubleToZonedDateTime(estimatedTime, run.get(gpsId).getBusLocationTime());

                    long epochEstimatedTime = Util.getEpochTimestampFromSecondsFromNoonMinus12Hours(
                            estimatedTime,
                            Util.convertEpochSecondsToZonedDateTime(run.get(gpsId1).getBusLocationTime()));
                    ArrivalTimeEstRecord tmp = new ArrivalTimeEstRecord(stopTime, epochEstimatedTime, busId, delay);
                    arrivalTimeEstRecords.add(tmp);
                }
            }
        }


//        for(String schedule : closestCandidateSchedules.keySet()) {
//            for(int i = 0; i < run.size() - 1; i++) {
//                BusGpsRecord gps1 = run.get(i);
//                BusGpsRecord gps2 = run.get(i + 1);
//
//                int gps1Time = zonedDateTimeToInteger(gps1.getBusLocationTime());
//                int gps2Time = zonedDateTimeToInteger(gps2.getBusLocationTime());
//
//
//                ArrayList<StopTime> inBetweenStops = findInBetweenStops(gps1, gps2, closestCandidateSchedules.get(schedule));
//
//                if (inBetweenStops != null) {
//                    for (int j = 0; j < inBetweenStops.size(); j++) {
//
//                        StopTime stop = inBetweenStops.get(j);
//                        double estimatedTime = estimateArrivalTime(gps1, gps2, gps1Time, gps2Time, stop);
//                        double delay = estimatedTime - stop.getArrivalTime();
//
//                        // Filter the one that delay too much or arrival too early
//                        if (delay >= -DELAY_TIME_THRESHOLD && delay <= DELAY_TIME_THRESHOLD) {
//                            ZonedDateTime estimatedArrivalZDT
//                                    = doubleToZonedDateTime(estimatedTime, gps1.getBusLocationTime());
//                            ArrivalTimeEstRecord tmp = new ArrivalTimeEstRecord(stop, estimatedArrivalZDT, busId, delay);
//                            estimatedArrivalTimeResult.add(tmp);
//                        }
//                    }
//                }
//            }
//        }
        return arrivalTimeEstRecords;
    }

    /**
     * Estimate arrival time to a stop that is in-between 2 GPS records
     * @param gps1 GPS record 1
     * @param gps2 GPS record 2
     * @param gps1Time the time of GPS record 1
     * @param gps2Time the time of GPS record 2
     * @param inBetweenStop the stop in-between 2 GPS records
     * @return the estimated timestamp
     */
    public static long estimateArrivalTime(BusGpsRecord gps1, BusGpsRecord gps2,
                                           long gps1Time, long gps2Time, Stop inBetweenStop) {

        double d0 = getDistance(gps1.getLon(), gps1.getLat(), gps2.getLon(), gps2.getLat());
        double d1 = getDistance(gps1.getLon(), gps1.getLat(), inBetweenStop.getLon(), inBetweenStop.getLat());

        double estimatedArrivalTime = d1 / (d0 / (gps2Time - gps1Time)) + gps1Time;
        return (long)estimatedArrivalTime;
    }

    /**
     * Find the id of the GPS record of a run that is closest to a stop
     * @param run a GPS run
     * @param stop a stop
     * @return the id of the GPS record of a run that is closest to a stop
     */
    public static int findClosestGPSToStop(ArrayList<BusGpsRecord> run, Stop stop) {
        int gpsId = 0;
        double dis = getDistance(run.get(0).getLon(), run.get(0).getLat(), stop.getLon(), stop.getLat());

        for(int i = 0; i < run.size(); i++) {
            double tmp = getDistance(run.get(i).getLon(), run.get(i).getLat(), stop.getLon(), stop.getLat());

            if (tmp < dis) {
                gpsId = i;
                dis = tmp;
            }
        }
        return gpsId;
    }

    public static ArrayList<StopTime> findInBetweenStops(BusGpsRecord gps1, BusGpsRecord gps2, ArrayList<StopTime> stopTimes) {

        ArrayList<StopTime> inBetweenStops = new ArrayList<>();

        double gps1Lon = gps1.getLon();
        double gps1Lat = gps1.getLat();
        double gps2Lon = gps2.getLon();
        double gps2Lat = gps2.getLat();

        for (int i = 0; i < stopTimes.size(); i++) {
            double stopLon = stopTimes.get(i).getStop().getLon();
            double stopLat = stopTimes.get(i).getStop().getLat();
            if (getAngle(stopLon, stopLat, gps1Lon, gps1Lat, gps2Lon, gps2Lat) >= 150.0) {
                inBetweenStops.add(stopTimes.get(i));
            }
        }
        return inBetweenStops;
    }


}
