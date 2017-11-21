package edu.usc.imsc.metrans.timematching;

import com.sun.tools.corba.se.idl.InterfaceGen;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import edu.usc.imsc.metrans.mapmatching.GpsRunTripMatcher;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static edu.usc.imsc.metrans.timematching.BusDelayComputation.busDelayComputationMain;
import static edu.usc.imsc.metrans.timematching.BusDelayComputation.calBusDelay;
import static edu.usc.imsc.metrans.timematching.BusDelayPreprocess.*;
import static edu.usc.imsc.metrans.timematching.BusDelayUtil.cutOffCandidateStopTimes;
import static edu.usc.imsc.metrans.timematching.BusDelayUtil.writeTxtFile;
import static edu.usc.imsc.metrans.timematching.SchedulesDetection.schedulesDetectionMain;

public class BusDelayMain {
    private static final Logger logger = LoggerFactory.getLogger(BusDelayMain.class);

    public static void busDelayMain(ArrayList<ArrayList<BusGpsRecord>> allRuns, GtfsStore gtfsStore) {

//        Map<StopTime, BusDelay> busDelayResult = new HashMap<>();
        Map<ZonedDateTime, StopTime> estimatedArrivalTimeResult = new HashMap<>();
        // Get routeId

        if (allRuns.size() == 0) return;

        Route route = GtfsUtil.getRouteFromShortId(gtfsStore, String.valueOf(allRuns.get(0).get(0).getRouteId()));

        // Get all trips for that route
        ArrayList<Trip> tripsOfRoute = getTripsOfRoute(route, gtfsStore);

        // Get all schedules (stop times) for that route
        Map<String, ArrayList<StopTime>> schedulesOfRoute = getSchedulesOfRoute(tripsOfRoute, gtfsStore);

        for (ArrayList<BusGpsRecord> eachRun : allRuns) {

            // Filter on time interval
            Map<String, ArrayList<StopTime>> candidateStopTimes = getCandidateSchedules(eachRun, schedulesOfRoute);
            // Cut off the time schedule based on time interval
//            Map<String, ArrayList<StopTime>> partCandidateStopTimes = cutOffCandidateStopTimes(eachRun, candidateStopTimes);

            // Detect top n closest schedule candidates based on distance
            Map<String, ArrayList<StopTime>> closestCandidateSchedules = schedulesDetectionMain(eachRun, candidateStopTimes);

            if (closestCandidateSchedules.size() != 0) {

//                Map<StopTime, BusDelay> busDelayTime = busDelayComputationMain(eachRun, closestCandidateSchedules);
//                for (StopTime eachBusDelay : busDelayTime.keySet()) {
//
//                    if (busDelayResult.containsKey(eachBusDelay)) {
//                        busDelayResult.get(eachBusDelay).resetDelayTime(busDelayTime.get(eachBusDelay).getDelayTime());
//                        busDelayResult.get(eachBusDelay).resetCountDelayTime(busDelayTime.get(eachBusDelay).getCountDelayTime());
//                        busDelayResult.get(eachBusDelay).resetNoSHow(busDelayTime.get(eachBusDelay).getNoShow());
//                    }
//                    else {
//                        busDelayResult.put(eachBusDelay, busDelayTime.get(eachBusDelay));
//                    }
//                }

                Map<ZonedDateTime, StopTime> estimatedArrivalTime = busDelayComputationMain(eachRun, closestCandidateSchedules);
                estimatedArrivalTimeResult.putAll(estimatedArrivalTime);
            }
        }

        try {
            if(writeTxtFile(estimatedArrivalTimeResult, "./data/estimatedArrivalTime.txt"))
                System.out.println("Finished");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
//        Map<StopTime, Double> avgBusDelayResult = avgBusDelayResult(busDelayResult);
//        showAvgBusDelayResult(avgBusDelayResult);
//        Map<StopTime, Double> noShowRateResult = noShowRateResult(busDelayResult);
//        showNoShowRateResult(noShowRateResult);
    }

    public static void showAvgBusDelayResult(Map<StopTime, Double> avgBusDelayResult) {
        for(StopTime eachStopTime : avgBusDelayResult.keySet()) {
            if(eachStopTime.getStop().getId().getId().equals("5279")) {
                System.out.println(eachStopTime.getStop().getId().getId() + " " +
                        BusDelayUtil.integerToTimeStamp(eachStopTime.getArrivalTime()) + " " +
                        avgBusDelayResult.get(eachStopTime));
            }
        }
    }

    public static void showNoShowRateResult(Map<StopTime, Double> noShowRateResult) {
        for(StopTime eachStopTime : noShowRateResult.keySet()) {
            if(eachStopTime.getStop().getId().getId().equals("5279")) {
                System.out.println(eachStopTime.getStop().getId().getId() + " " +
                        BusDelayUtil.integerToTimeStamp(eachStopTime.getArrivalTime()) + " " +
                        noShowRateResult.get(eachStopTime) * 100 + "%");
            }
        }
    }

    public static Map<StopTime, Double> avgBusDelayResult(Map<StopTime, BusDelay> busDelayResult) {
        Map<StopTime, Double> avgBusDelayResult = new HashMap<>();
        for(StopTime stopTime: busDelayResult.keySet()) {
            avgBusDelayResult.put(stopTime, Double.valueOf(busDelayResult.get(stopTime).getDelayTime() / (double)busDelayResult.get(stopTime).getCountDelayTime()));
        }
        return avgBusDelayResult;
    }

    public static Map<StopTime, Double> noShowRateResult(Map<StopTime, BusDelay> busDelayResult) {
        Map<StopTime, Double> noShowRateResult = new HashMap<>();
        for(StopTime stopTime: busDelayResult.keySet()) {
            BusDelay busDelay = busDelayResult.get(stopTime);
            noShowRateResult.put(stopTime, Double.valueOf((double)busDelay.getNoShow() / (double)(busDelay.getCountDelayTime() + busDelay.getNoShow())));
        }
        return noShowRateResult;
    }

}
