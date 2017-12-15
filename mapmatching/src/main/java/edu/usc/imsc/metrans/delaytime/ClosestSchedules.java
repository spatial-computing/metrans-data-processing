package edu.usc.imsc.metrans.delaytime;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import org.onebusaway.gtfs.model.StopTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.usc.imsc.metrans.delaytime.Util.calDistance;
import static edu.usc.imsc.metrans.delaytime.Util.sortScheduledTripsByAvgDist;

public class ClosestSchedules {

    private static double DIST_THRESHOLD = 5.0; //filter the closest schedules


    /**
     * Find closest scheduled trips to a run.
     * Those trips should have the average of the shortest distance from a GPS record of the run to a stop
     * not too much bigger compared to the closest one.
     * @param run a GPS run
     * @param candidateSchedules candidate scheduled trips
     * @return the closest scheduled trips to a run
     */
    public static Map<String, ArrayList<StopTime>> findClosestSchedules(ArrayList<BusGpsRecord> run,
            Map<String, ArrayList<StopTime>> candidateSchedules) {

        Map<String, Double> candidateAvgDistance = new HashMap<>();
        for (String tripId : candidateSchedules.keySet()) {
            ArrayList<StopTime> schedule = candidateSchedules.get(tripId);
            double avgDistance = calAverageDistanceFromStops(run, schedule);
            candidateAvgDistance.put(tripId, avgDistance);
        }

        List<Map.Entry<String, Double>> sortedSchedules = sortScheduledTripsByAvgDist(candidateAvgDistance);

        Map<String, ArrayList<StopTime>> closestSchedules = new HashMap<>();
        if (sortedSchedules.size() == 0)
            return  closestSchedules;

        /*
         * Get trips having avg distance <= avg_dist_of_the_closest_trip + thres_hold
         */
        double leastDist = sortedSchedules.get(0).getValue();
        for (int i = 0; i < sortedSchedules.size(); i++) {
            String key = sortedSchedules.get(i).getKey();
            if (sortedSchedules.get(i).getValue() <= (leastDist + DIST_THRESHOLD)) {
                closestSchedules.put(key, candidateSchedules.get(key));
            }
            else
                break;
        }

        return closestSchedules;
    }


    /**
     * Calculate average of the shortest distance from a GPS record to a stop of the scheduled trip
     * @param run GPS records
     * @param schedule stops of the scheduled trip
     * @return average of the shortest distance from a GPS record to a stop of the scheduled trip
     */
    public static Double calAverageDistanceFromStops(ArrayList<BusGpsRecord> run, ArrayList<StopTime> schedule) {

        double sumDistance = 0.0;
        for (int i = 0; i < run.size(); i++) {
            sumDistance += shortestDistanceFromStops(schedule, run.get(i));
        }
        return sumDistance / run.size();
    }

    /**
     * Calculate the shortest distance from a GPS record to a stop of the scheduled trip
     * @param schedule stops of the scheduled trip
     * @param gps a GPS record
     * @return the shortest distance from a GPS record to a stop of the scheduled trip
     */
    public static Double shortestDistanceFromStops(ArrayList<StopTime> schedule, BusGpsRecord gps){
        double gpsLon = gps.getLon();
        double gpsLat = gps.getLat();
        double shortestDistance = calDistance(gpsLon, gpsLat, schedule.get(0).getStop().getLon(),
                schedule.get(0).getStop().getLat());

        for (int i = 1; i < schedule.size(); i++) {
            double distance = calDistance(gpsLon, gpsLat, schedule.get(i).getStop().getLon(),
                    schedule.get(i).getStop().getLat());
            if (distance < shortestDistance)
                shortestDistance = distance;
        }
        return shortestDistance;
    }
}
