package edu.usc.imsc.metrans.timematching;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;

import static edu.usc.imsc.metrans.timematching.BusDelayUtil.*;
import static edu.usc.imsc.metrans.timematching.SchedulesDetectionUtil.getDistance;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusDelayComputation {

    private static double delayTimeThreshold = 15 * 60;

    public static ArrayList<BusDelay> busDelayComputationMain(ArrayList<BusGpsRecord> runs,
                                               Map<String, ArrayList<StopTime>> closestCandidateSchedules) {


        ArrayList<BusDelay> estimatedArrivalTimeResult =  new ArrayList<>();

        for(String schedule : closestCandidateSchedules.keySet()) {

            ArrayList<BusDelay> estimatedArrivalTime = calBusDelay(runs, closestCandidateSchedules.get(schedule));
            estimatedArrivalTimeResult.addAll(estimatedArrivalTime);
        }
        return estimatedArrivalTimeResult;
    }

    public static ArrayList<BusDelay> calBusDelay(ArrayList<BusGpsRecord> run, ArrayList<StopTime> stopTimes) {

        ArrayList<BusDelay> estimatedArrivalTimeResult = new ArrayList<>();

        for(int i = 0; i < run.size() - 1; i++) {
            BusGpsRecord gps1 = run.get(i);
            BusGpsRecord gps2 = run.get(i + 1);

            Integer gps1Time = timeStampToInteger(gps1.getBusLocationTime());
            Integer gps2Time = timeStampToInteger(gps2.getBusLocationTime());

            ArrayList<StopTime> inBetweenStops = findInBetweenStops(gps1, gps2, stopTimes);

            if (inBetweenStops != null) {
                for (int j = 0; j < inBetweenStops.size(); j++) {

                    StopTime stop = inBetweenStops.get(j);
                    Double estimatedArrivalTime = calEstimatedArrivalTime(gps1, gps2, gps1Time, gps2Time, stop);
                    Double delay = estimatedArrivalTime - stop.getArrivalTime();

                    // Filter the one that delay too much or arrival too early
                    if (delay >= -delayTimeThreshold && delay <= delayTimeThreshold) {
                        ZonedDateTime estimatedArrivalZDT = doubleToZonedDateTime(estimatedArrivalTime, gps1.getBusLocationTime());
                        BusDelay tmp = new BusDelay(stop, estimatedArrivalZDT, run.get(i).getBusId(), delay);
                        estimatedArrivalTimeResult.add(tmp);
                    }
                }
            }
        }
        return estimatedArrivalTimeResult;
    }

    public static Double calEstimatedArrivalTime(BusGpsRecord gps1, BusGpsRecord gps2,
                                        Integer gps1Time, Integer gps2Time, StopTime inBetweenStop) {

        Double d0 = getDistance(gps1.getLon(), gps1.getLat(), gps2.getLon(), gps2.getLat());
        Double d1 = getDistance(gps1.getLon(), gps1.getLat(), inBetweenStop.getStop().getLon(), inBetweenStop.getStop().getLat());

        Double estimatedArrivalTime = d1 / (d0 / (gps2Time - gps1Time)) + gps1Time;
        return estimatedArrivalTime;
    }


    public static ArrayList<StopTime> findInBetweenStops(BusGpsRecord gps1, BusGpsRecord gps2, ArrayList<StopTime> stopTimes) {

        ArrayList<StopTime> inBetweenStops = new ArrayList<>();

        Double gps1Lon = gps1.getLon();
        Double gps1Lat = gps1.getLat();
        Double gps2Lon = gps2.getLon();
        Double gps2Lat = gps2.getLat();

        for (int i = 0; i < stopTimes.size(); i++) {
            Double stopLon = stopTimes.get(i).getStop().getLon();
            Double stopLat = stopTimes.get(i).getStop().getLat();
            if (getAngle(stopLon, stopLat, gps1Lon, gps1Lat, gps2Lon, gps2Lat) >= 150.0) {
                inBetweenStops.add(stopTimes.get(i));
            }
        }
        return inBetweenStops;
    }
}
