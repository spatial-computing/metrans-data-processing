package edu.usc.imsc.metrans.timematching;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;

import java.util.*;

import static edu.usc.imsc.metrans.timematching.SchedulesDetectionUtil.*;

public class SchedulesDetection {

    private static double DIS_THRESHOLD = 5.0; //filter the closest schedules

    public static Map<String, ArrayList<StopTime>> schedulesDetectionMain(ArrayList<BusGpsRecord> run,
                                           Map<String, ArrayList<StopTime>> candidateSchedules) {

        Map<String, Double> candidateSumDistance = new HashMap<>();
        for (String candidateSchedule : candidateSchedules.keySet()) {
            ArrayList<StopTime> schedule = candidateSchedules.get(candidateSchedule);
            Double sumDistance = sumDistanceFromStops(run, schedule);
            candidateSumDistance.put(candidateSchedule, sumDistance);
        }

        List<Map.Entry<String, Double>> sortedSchedules = sortSchedules(candidateSumDistance, candidateSchedules);

        Map<String, ArrayList<StopTime>> closestSchedules = new HashMap<>();
        if (sortedSchedules.size() == 0)
            return  closestSchedules;


//        Integer I;
//        if (sortedSchedules.size() < CLOSEST_NUM) I = sortedSchedules.size();
//        else I = CLOSEST_NUM;
//
//        for (int i = 0; i < I; i++) {
//            String key = sortedSchedules.get(i).getKey();
//            closestSchedules.put(key, candidateSchedules.get(key));
//        }

        Double previousDis = sortedSchedules.get(0).getValue();
        for (int i = 0; i < sortedSchedules.size(); i++) {
            String key = sortedSchedules.get(i).getKey();
            if (sortedSchedules.get(i).getValue() <= (previousDis + DIS_THRESHOLD)) {
                closestSchedules.put(key, candidateSchedules.get(key));
                previousDis = sortedSchedules.get(i).getValue();
            }
            else
                break;
        }

        return closestSchedules;
    }


    public static Double sumDistanceFromStops(ArrayList<BusGpsRecord> run, ArrayList<StopTime> schedule) {
        Double sumDistance = 0.0;
        for (int i = 0; i < run.size(); i++) {
            sumDistance += shortestDistanceFromStops(schedule, run.get(i));
        }
        return sumDistance / run.size();
    }

    public static Double shortestDistanceFromStops(ArrayList<StopTime> schedule, BusGpsRecord gps){

        Double gpsLon = gps.getLon();
        Double gpsLat = gps.getLat();
        Double shortestDistance = getDistance(gpsLon, gpsLat, schedule.get(0).getStop().getLon(),
                schedule.get(0).getStop().getLat());

        for (int i = 1; i < schedule.size(); i++) {
            Double distance = getDistance(gpsLon, gpsLat, schedule.get(i).getStop().getLon(),
                    schedule.get(i).getStop().getLat());
            if (distance < shortestDistance)
                shortestDistance = distance;
        }
        return shortestDistance;
    }
}
