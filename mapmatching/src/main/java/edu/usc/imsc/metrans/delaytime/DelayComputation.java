package edu.usc.imsc.metrans.delaytime;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.timedata.DelayTimeRecord;
import org.onebusaway.gtfs.model.StopTime;
import org.opengis.referencing.operation.TransformException;

import static edu.usc.imsc.metrans.delaytime.DelayTimeMain.line;
import static edu.usc.imsc.metrans.delaytime.Util.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelayComputation {

    private static double delayTimeThreshold = 15 * 60;

    public static ArrayList<DelayTimeRecord> delayComputation(ArrayList<BusGpsRecord> run,
                                                              Map<String, ArrayList<StopTime>> closestCandidateSchedules)
            throws TransformException {

        int busId = run.get(0).getBusId();
        ArrayList<DelayTimeRecord> estimatedArrivalTimeResult =  new ArrayList<>();
        for(String schedule : closestCandidateSchedules.keySet()) {
            for(int i = 0; i < run.size() - 1; i++) {
                BusGpsRecord gps1 = run.get(i);
                BusGpsRecord gps2 = run.get(i + 1);

                int gps1Time = zonedDateTimeToInteger(gps1.getBusLocationTime());
                int gps2Time = zonedDateTimeToInteger(gps2.getBusLocationTime());

                ArrayList<StopTime> inBetweenStops = findInBetweenStops(gps1, gps2, closestCandidateSchedules.get(schedule));

                if (inBetweenStops != null) {
                    for (int j = 0; j < inBetweenStops.size(); j++) {

                        StopTime stop = inBetweenStops.get(j);
                        double estimatedTime = calEstimatedArrivalTime(gps1, gps2, gps1Time, gps2Time, stop);
                        double delay = estimatedTime - stop.getArrivalTime();

                        // Filter the one that delay too much or arrival too early
                        if (delay >= -delayTimeThreshold && delay <= delayTimeThreshold) {
                            ZonedDateTime estimatedArrivalZDT
                                    = doubleToZonedDateTime(estimatedTime, gps1.getBusLocationTime());
                            DelayTimeRecord tmp = new DelayTimeRecord(stop, estimatedArrivalZDT, busId, delay);
                            estimatedArrivalTimeResult.add(tmp);
                        }
                    }
                }
            }
        }
        return estimatedArrivalTimeResult;
    }

    public static Double calEstimatedArrivalTime(BusGpsRecord gps1, BusGpsRecord gps2,
                                        int gps1Time, int gps2Time, StopTime inBetweenStop) throws TransformException {


        double d0 = DistanceOnPolyline.getDistance(gps1.getLon(), gps1.getLat(), gps2.getLon(), gps2.getLat(), line);
        double d1 = DistanceOnPolyline.getDistance(gps1.getLon(), gps1.getLat(), inBetweenStop.getStop().getLon(),
                inBetweenStop.getStop().getLat(), line);

        double estimatedArrivalTime = d1 / (d0 / (gps2Time - gps1Time)) + gps1Time;
        return estimatedArrivalTime;
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
