package edu.usc.imsc.metrans.delaytime;

import com.vividsolutions.jts.geom.LineString;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.opengis.referencing.operation.TransformException;

import java.util.*;

import static edu.usc.imsc.metrans.delaytime.DelayTimeMain.line;
import static edu.usc.imsc.metrans.delaytime.Util.*;

public class ClosestSchedules {

    private static double DIS_THRESHOLD = 5.0; //filter the closest schedules

    public static Map<String, ArrayList<StopTime>> findClosestSchedules(ArrayList<BusGpsRecord> run,
            Map<String, ArrayList<StopTime>> candidateSchedules) throws TransformException {

        Map<String, Double> candidateSumDistance = new HashMap<>();
        for (String candidateSchedule : candidateSchedules.keySet()) {
            ArrayList<StopTime> schedule = candidateSchedules.get(candidateSchedule);
            double sumDistance = sumDistanceFromStops(run, schedule);
            candidateSumDistance.put(candidateSchedule, sumDistance);
        }

        List<Map.Entry<String, Double>> sortedSchedules = sortSchedules(candidateSumDistance, candidateSchedules);

        Map<String, ArrayList<StopTime>> closestSchedules = new HashMap<>();
        if (sortedSchedules.size() == 0)
            return  closestSchedules;

        double leastDis = sortedSchedules.get(0).getValue();
        for (int i = 0; i < sortedSchedules.size(); i++) {
            String key = sortedSchedules.get(i).getKey();
            if (sortedSchedules.get(i).getValue() <= (leastDis + DIS_THRESHOLD)) {
                closestSchedules.put(key, candidateSchedules.get(key));
            }
            else
                break;
        }

        return closestSchedules;
    }


    public static Double sumDistanceFromStops(ArrayList<BusGpsRecord> run, ArrayList<StopTime> schedule)
            throws TransformException {

        double sumDistance = 0.0;
        for (int i = 0; i < run.size(); i++) {
            sumDistance += shortestDistanceFromStops(schedule, run.get(i));
        }
        return sumDistance / run.size();
    }

    public static Double shortestDistanceFromStops(ArrayList<StopTime> schedule, BusGpsRecord gps)
            throws TransformException {

        double gpsLon = gps.getLon();
        double gpsLat = gps.getLat();
        double shortestDistance = DistanceOnPolyline.getDistance(gpsLon, gpsLat, schedule.get(0).getStop().getLon(),
                schedule.get(0).getStop().getLat(), line);

        for (int i = 1; i < schedule.size(); i++) {
            double distance = DistanceOnPolyline.getDistance(gpsLon, gpsLat, schedule.get(i).getStop().getLon(),
                    schedule.get(i).getStop().getLat(), line);
            if (distance < shortestDistance)
                shortestDistance = distance;
        }
        return shortestDistance;
    }
}
