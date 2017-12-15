package edu.usc.imsc.metrans.delaytime;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.timedata.DelayTimeRecord;
import org.onebusaway.gtfs.model.StopTime;

import static edu.usc.imsc.metrans.delaytime.Util.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;

public class DelayComputation {

    private static double delayTimeThreshold = 15 * 60;

    /**
     * Calculate estimated arrival time and delay for stop-times of scheduled trips given a GPS run.
     * We only keep the records with delay within {@link #delayTimeThreshold}
     * @param run the GPS run
     * @param closestCandidateSchedules candidate scheduled trips
     * @return estimated arrival time and delay
     */
    public static ArrayList<DelayTimeRecord> delayComputation(ArrayList<BusGpsRecord> run,
                                                              Map<String, ArrayList<StopTime>> closestCandidateSchedules) {

        int busId = run.get(0).getBusId();
        ArrayList<DelayTimeRecord> estimatedArrivalTimeResult = new ArrayList<>();
        for (String tripId : closestCandidateSchedules.keySet()) {
            for (int i = 0; i < run.size() - 1; i++) {
                BusGpsRecord gps1 = run.get(i);
                BusGpsRecord gps2 = run.get(i + 1);

                int gps1Time = getNumSecondsFromMidnight(gps1.getBusLocationTime());
                int gps2Time = getNumSecondsFromMidnight(gps2.getBusLocationTime());

                ArrayList<StopTime> inBetweenStops = findInBetweenStops(gps1, gps2, closestCandidateSchedules.get(tripId));

                for (int j = 0; j < inBetweenStops.size(); j++) {

                    StopTime stop = inBetweenStops.get(j);
                    double estimatedTime = calEstimatedArrivalTime(gps1, gps2, gps1Time, gps2Time, stop);
                    double delay = estimatedTime - stop.getArrivalTime();

                    // Filter the one that delay too much or arrival too early
                    if (delay >= -delayTimeThreshold && delay <= delayTimeThreshold) {
                        ZonedDateTime estimatedArrivalZDT
                                = convertDoubleToZonedDateTime(estimatedTime, gps1.getBusLocationTime());
                        DelayTimeRecord tmp = new DelayTimeRecord(stop, estimatedArrivalZDT, busId, delay);
                        estimatedArrivalTimeResult.add(tmp);
                    }
                }
            }
        }
        return estimatedArrivalTimeResult;
    }

    /**
     * Calculate estimated arrival time at a stop that lies in between 2 GPS records
     *
     * @param gps1          GPS record 1
     * @param gps2          GPS record 2
     * @param gps1Time      the time in seconds from midnight of GPS record 1
     * @param gps2Time      the time in seconds from midnight of GPS record 2
     * @param inBetweenStop a stop that lies in between 2 GPS records
     * @return estimated arrival time at the stop
     */
    public static Double calEstimatedArrivalTime(BusGpsRecord gps1, BusGpsRecord gps2,
                                                 int gps1Time, int gps2Time, StopTime inBetweenStop) {


        double d0 = calDistance(gps1.getLon(), gps1.getLat(), gps2.getLon(), gps2.getLat());
        double d1 = calDistance(gps1.getLon(), gps1.getLat(), inBetweenStop.getStop().getLon(),
                inBetweenStop.getStop().getLat());

        double estimatedArrivalTime = d1 / (d0 / (gps2Time - gps1Time)) + gps1Time;
        return estimatedArrivalTime;
    }


    /**
     * Get stop-time of stops that are in between 2 GPS records
     *
     * @param gps1      GPS record 1
     * @param gps2      GPS record 2
     * @param stopTimes list of stop times
     * @return stop-times of stops that are in between 2 GPS records
     */
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
