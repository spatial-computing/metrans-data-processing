package edu.usc.imsc.metrans.arrivaltimeestimators;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import org.onebusaway.gtfs.model.StopTime;

import java.util.*;
import static edu.usc.imsc.metrans.arrivaltimeestimators.Util.*;

public class ClosestTripCalculator {

    private static final double DISTANCE_THRESHOLD = 5.0; //filter the closest schedules

    /**
     * Find the candidate trips that are within the {@code minDist} + {@link #DISTANCE_THRESHOLD} to a GPS run,
     * where {@code minDist} is the minimum distance from the a trip to the GPS run
     * @param run a GPS run
     * @param candidateTrips candidate trips as Trip => StopTimes mapping
     * @return the candidate trips
     */
    public static Map<String, ArrayList<StopTime>> findClosestTrips(ArrayList<BusGpsRecord> run,
                                                                    Map<String, ArrayList<StopTime>> candidateTrips) {
        Map<String, Double> candidateSumDistance = new HashMap<>();
        for (String trip : candidateTrips.keySet()) {
            ArrayList<StopTime> stopTimes = candidateTrips.get(trip);
            double sumDistance = calSumShortestDistances(run, stopTimes);
            candidateSumDistance.put(trip, sumDistance);
        }

        List<Map.Entry<String, Double>> sortedTrips = getSortedTripDistances(candidateSumDistance);

        Map<String, ArrayList<StopTime>> closestTrips = new HashMap<>();
        if (sortedTrips.isEmpty())
            return closestTrips;

        double minDist = sortedTrips.get(0).getValue();
        for (int i = 0; i < sortedTrips.size(); i++) {
            String key = sortedTrips.get(i).getKey();
            if (sortedTrips.get(i).getValue() <= (minDist + DISTANCE_THRESHOLD)) {
                closestTrips.put(key, candidateTrips.get(key));
            }
            else
                break;
        }

        return closestTrips;
    }

    /**
     * Calculate the sum of the shortest distances each a GPS record of a run to a trip
     * @param run the GPS run
     * @param stopTimes stop times of a trip
     * @return  the sum of the shortest distances
     */
    public static Double calSumShortestDistances(ArrayList<BusGpsRecord> run, ArrayList<StopTime> stopTimes) {
        double sumDistance = 0.0;
        for (int i = 0; i < run.size(); i++) {
            sumDistance += calShortestDistance(stopTimes, run.get(i));
        }
        return sumDistance / run.size();
    }

    /**
     * Get the shortest distance between a GPS record and a stop
     * @param stopTimes stop times of a trip
     * @param gps the GPS record
     * @return the shortest distance between a GPS record and a stop or {@code Double.MAX_VALUE} if unable to calculate
     */
    public static Double calShortestDistance(ArrayList<StopTime> stopTimes, BusGpsRecord gps){
        double shortestDistance = Double.MAX_VALUE;

        double gpsLon = gps.getLon();
        double gpsLat = gps.getLat();

        for (int i = 0; i < stopTimes.size(); i++) {
            double distance = getDistance(gpsLon, gpsLat, stopTimes.get(i).getStop().getLon(),
                    stopTimes.get(i).getStop().getLat());
            if (distance < shortestDistance)
                shortestDistance = distance;
        }
        return shortestDistance;
    }

    /**
     * Get list of trip-distance pair, sorted by distance then trip
     * @param tripDistanceMap trip -> distance map
     * @return list of trip-distance pair, sorted by distance then trip
     */
    public static List<Map.Entry<String, Double>> getSortedTripDistances(Map<String, Double> tripDistanceMap) {

        List<Map.Entry<String, Double>> candidateSumDistanceList = new ArrayList<Map.Entry<String, Double>>(tripDistanceMap.entrySet());
        candidateSumDistanceList.sort(new Comparator<Map.Entry<String, Double>>() {

            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                int flag = o1.getValue().compareTo(o2.getValue());
                if (flag == 0) {
                    return o1.getKey().compareTo(o2.getKey());
                }
                return flag;
            }
        });

        return candidateSumDistanceList;
    }
}
