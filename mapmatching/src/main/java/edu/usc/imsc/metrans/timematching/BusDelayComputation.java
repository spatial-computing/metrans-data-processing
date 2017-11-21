package edu.usc.imsc.metrans.timematching;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import org.onebusaway.gtfs.model.StopTime;

import static edu.usc.imsc.metrans.timematching.BusDelayUtil.calEstimatedArrivalZonedDateTime;
import static edu.usc.imsc.metrans.timematching.BusDelayUtil.getAngle;
import static edu.usc.imsc.metrans.timematching.BusDelayUtil.timeStampToInteger;
import static edu.usc.imsc.metrans.timematching.SchedulesDetectionUtil.getDistance;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusDelayComputation {

    public static Map<ZonedDateTime, StopTime> busDelayComputationMain(ArrayList<BusGpsRecord> runs,
                                               Map<String, ArrayList<StopTime>> closestCandidateSchedules) {


//        Map<StopTime, BusDelay> busDelayTime = new HashMap<>();
        Map<ZonedDateTime, StopTime> estimatedArrivalTimeResult =  new HashMap<>();

        for(String schedule : closestCandidateSchedules.keySet()) {
//            Map<StopTime, BusDelay> busDelays = calBusDelay(runs, closestCandidateSchedules.get(schedule));
//            for(StopTime stopTime : busDelays.keySet()) {
//                if (busDelayTime.keySet().contains(stopTime)) {
//                    busDelayTime.get(stopTime).resetDelayTime(busDelays.get(stopTime).getDelayTime());
//                    busDelayTime.get(stopTime).resetCountDelayTime(busDelays.get(stopTime).getCountDelayTime());
//                    busDelayTime.get(stopTime).resetNoSHow(busDelays.get(stopTime).getNoShow());
//                } else {
//                    busDelayTime.put(stopTime, busDelays.get(stopTime));
//                }
//            }
            Map<ZonedDateTime, StopTime> estimatedArrivalTime = calBusDelay(runs, closestCandidateSchedules.get(schedule));
            estimatedArrivalTimeResult.putAll(estimatedArrivalTime);

        }
        return estimatedArrivalTimeResult;
    }

    public static Map<ZonedDateTime, StopTime> calBusDelay(ArrayList<BusGpsRecord> run, ArrayList<StopTime> stopTimes) {

//        Map<StopTime, BusDelay> busDelays = new HashMap<>();

        Map<ZonedDateTime, StopTime> estimatedArrivalTimeResult = new HashMap<>();

        for(int i = 0; i < run.size() - 1; i++) {
            BusGpsRecord gps1 = run.get(i);
            BusGpsRecord gps2 = run.get(i + 1);

            Integer gps1Time = timeStampToInteger(gps1.getBusLocationTime());
            Integer gps2Time = timeStampToInteger(gps2.getBusLocationTime());

            ArrayList<StopTime> inBetweenStops = findInBetweenStops(gps1, gps2, stopTimes);

            if (inBetweenStops != null) {
                for (int j = 0; j < inBetweenStops.size(); j++) {
//                    BusDelay delayTime = calDelayTime(gps1, gps2, gps1Time, gps2Time, inBetweenStops.get(j));
//                    if (delayTime != null) {
//                        if (busDelays.keySet().contains(inBetweenStops.get(j))) {
//                            busDelays.get(inBetweenStops.get(j)).resetDelayTime(delayTime.getDelayTime());
//                            busDelays.get(inBetweenStops.get(j)).resetCountDelayTime(delayTime.getCountDelayTime());
//                            busDelays.get(inBetweenStops.get(j)).resetNoSHow(delayTime.getNoShow());
//                        } else {
//                            busDelays.put(inBetweenStops.get(j), delayTime);
//                        }
//                    }

                    Double estimatedArrivalTime = calEstimatedArrivalTime(gps1, gps2, gps1Time, gps2Time, inBetweenStops.get(j));
                    ZonedDateTime estimatedArrivalZonedDateTime = calEstimatedArrivalZonedDateTime(estimatedArrivalTime, gps1.getBusLocationTime());
                    estimatedArrivalTimeResult.put(estimatedArrivalZonedDateTime, inBetweenStops.get(j));
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

    public static BusDelay calDelayTime(BusGpsRecord gps1, BusGpsRecord gps2,
                                        Integer gps1Time, Integer gps2Time, StopTime inBetweenStop) {

        Double d0 = getDistance(gps1.getLon(), gps1.getLat(), gps2.getLon(), gps2.getLat());
        Double d1 = getDistance(gps1.getLon(), gps1.getLat(), inBetweenStop.getStop().getLon(), inBetweenStop.getStop().getLat());

        Double estimatedArrivalTime = d1 / (d0 / (gps2Time - gps1Time)) + gps1Time;
        Double estimatedDelayTime = estimatedArrivalTime - inBetweenStop.getArrivalTime();

        if (estimatedDelayTime >= -240.0 && estimatedDelayTime <= 60.0) {
            BusDelay delayTime = new BusDelay(inBetweenStop.getStop(), inBetweenStop.getArrivalTime(), estimatedDelayTime, 1, 0);
            return delayTime;
        }
        else {
            BusDelay delayTime = new BusDelay(inBetweenStop.getStop(), inBetweenStop.getArrivalTime(), 0.0, 0, 1);
            return delayTime;
        }
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
            Double tmp = getAngle(stopLon, stopLat, gps1Lon, gps1Lat, gps2Lon, gps2Lat);
            if (getAngle(stopLon, stopLat, gps1Lon, gps1Lat, gps2Lon, gps2Lat) >= 150.0) {
                inBetweenStops.add(stopTimes.get(i));
            }
        }
        return inBetweenStops;
    }
}
