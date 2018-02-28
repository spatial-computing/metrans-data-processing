package edu.usc.imsc.metrans.arrivaltimeestimators;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.timedata.ArrivalTimeEstRecord;
import edu.usc.imsc.metrans.timedata.ScheduleStartTimeEndTime;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static edu.usc.imsc.metrans.arrivaltimeestimators.ClosestSchedules.findClosestSchedules;
import static edu.usc.imsc.metrans.arrivaltimeestimators.SchedulePreprocessing.*;
import static edu.usc.imsc.metrans.arrivaltimeestimators.Util.*;

import java.util.ArrayList;
import java.util.Map;

public class ArrivalTimeEstimator {
    private static final Logger logger = LoggerFactory.getLogger(ArrivalTimeEstimator.class);

    public static ArrayList<ArrivalTimeEstRecord> estimateArrivalTimeForARun(ArrayList<BusGpsRecord> run,
                                                                             Map<String, ArrayList<StopTime>> closestCandidateSchedules) {

        int busId = run.get(0).getBusId();
        ArrayList<ArrivalTimeEstRecord> estimatedArrivalTimeResult =  new ArrayList<>();
        // Compute for all stop time
        for(String schedule : closestCandidateSchedules.keySet()) {
            ArrayList<StopTime> stopTimes = closestCandidateSchedules.get(schedule);
            for(int i = 0; i < stopTimes.size(); i++) {
                StopTime stopTime = stopTimes.get(i);
                int gpsId = findClosestGPS(run, stopTime);
                int gpsId2 = gpsId + 1;
                if (gpsId == 0) gpsId2 = gpsId + 1;
                else if (gpsId == run.size() - 1) gpsId2 = gpsId - 1;
                else {
                    double dis1 = getDistance(run.get(gpsId - 1).getLon(), run.get(gpsId - 1).getLat(),
                            stopTime.getStop().getLon(), stopTime.getStop().getLat());

                    double dis2 = getDistance(run.get(gpsId + 1).getLon(), run.get(gpsId + 1).getLat(),
                            stopTime.getStop().getLon(), stopTime.getStop().getLat());

                    if (dis1 <= dis2) gpsId2 = gpsId - 1;
                    if (dis1 > dis2) gpsId2 = gpsId + 1;
                }

                long gps1Time = Util.getSecondsFromNoonMinus12Hours(run.get(gpsId).getBusLocationTime());
                long gps2Time = Util.getSecondsFromNoonMinus12Hours(run.get(gpsId2).getBusLocationTime());
                long estimatedTime = calEstimatedArrivalTime(
                        run.get(gpsId), run.get(gpsId2), gps1Time, gps2Time, stopTime.getStop());
                long delay = estimatedTime - stopTime.getArrivalTime();

                // Filter the one that delay too much or arrival too early
                if (delay >= -DELAY_TIME_THRESHOLD && delay <= DELAY_TIME_THRESHOLD) {
//                    ZonedDateTime estimatedArrivalZDT
//                            = doubleToZonedDateTime(estimatedTime, run.get(gpsId).getBusLocationTime());

                    long epochEstimatedTime = Util.getEpochTimestampFromSecondsFromNoonMinus12Hours(
                            estimatedTime,
                            Util.convertSecondsToZonedDateTime(run.get(gpsId).getBusLocationTime()));
                    ArrivalTimeEstRecord tmp = new ArrivalTimeEstRecord(stopTime, epochEstimatedTime, busId, delay);
                    estimatedArrivalTimeResult.add(tmp);
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
//                        double estimatedTime = calEstimatedArrivalTime(gps1, gps2, gps1Time, gps2Time, stop);
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
        return estimatedArrivalTimeResult;
    }

    public static long calEstimatedArrivalTime(BusGpsRecord gps1, BusGpsRecord gps2,
                                        long gps1Time, long gps2Time, Stop inBetweenStop) {

        double d0 = getDistance(gps1.getLon(), gps1.getLat(), gps2.getLon(), gps2.getLat());
        double d1 = getDistance(gps1.getLon(), gps1.getLat(), inBetweenStop.getLon(), inBetweenStop.getLat());

        double estimatedArrivalTime = d1 / (d0 / (gps2Time - gps1Time)) + gps1Time;
        return (long)estimatedArrivalTime;
    }

    public static int findClosestGPS(ArrayList<BusGpsRecord> run, StopTime stopTime) {
        int gpsId = 0;
        double dis = getDistance(run.get(0).getLon(), run.get(0).getLat(),
                stopTime.getStop().getLon(), stopTime.getStop().getLat());

        for(int i = 0; i < run.size(); i++) {
            double tmp = getDistance(run.get(i).getLon(), run.get(i).getLat(),
                    stopTime.getStop().getLon(), stopTime.getStop().getLat());

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

    public static ArrayList<ArrivalTimeEstRecord> estimateArrivalTime(Route route, ArrayList<ArrayList<BusGpsRecord>> allRuns, GtfsStore gtfsStore) {

        logger.info("Arrival Time Estimation starts");
        ArrayList<ArrivalTimeEstRecord> estimatedArrivalTimeResult = new ArrayList<>();
        if (allRuns.size() == 0)
            return estimatedArrivalTimeResult;

        // Get all trips for that route
        ArrayList<Trip> tripsOfRoute = getTripsOfRoute(route, gtfsStore);
        logger.info("Total " + tripsOfRoute.size() + " trips");

        // Get all schedules (stop times) for that route
        Map<String, ArrayList<StopTime>> schedulesOfRoute = getSchedulesOfRoute(tripsOfRoute, gtfsStore);

        Integer scheduleTimes = 0;
        for (String schedule: schedulesOfRoute.keySet()) {
            scheduleTimes += schedulesOfRoute.get(schedule).size();
        }
        logger.info("Total " + scheduleTimes + " scheduleTimes");

        // Get start time and end time of all schedules
        Map<String, ScheduleStartTimeEndTime> scheduleStartTimeEndTime = getScheduleStartTimeEndTime(schedulesOfRoute);

        for (ArrayList<BusGpsRecord> run : allRuns) {

            // Filter on time interval
            Map<String, ArrayList<StopTime>> candidateSchedules
                    = getCandidateSchedules(run, schedulesOfRoute, scheduleStartTimeEndTime);

            // Detect top n closest schedule candidates based on distance
            Map<String, ArrayList<StopTime>> closestCandidateSchedules
                    = findClosestSchedules(run, candidateSchedules);

            if (closestCandidateSchedules.size() != 0) {

                ArrayList<ArrivalTimeEstRecord> estimatedArrivalTime
                        = estimateArrivalTimeForARun(run, closestCandidateSchedules);
                estimatedArrivalTimeResult.addAll(estimatedArrivalTime);

            }
        }

        return estimatedArrivalTimeResult;

    }
}