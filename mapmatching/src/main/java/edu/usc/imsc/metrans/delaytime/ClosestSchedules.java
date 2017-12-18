package edu.usc.imsc.metrans.delaytime;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.opengis.referencing.operation.TransformException;

import java.util.*;

import static edu.usc.imsc.metrans.delaytime.PolylineUtil.distanceToLine;
import static edu.usc.imsc.metrans.delaytime.PolylineUtil.isDirection;
import static edu.usc.imsc.metrans.delaytime.Util.*;

public class ClosestSchedules {

    private static double DIS_THRESHOLD = 5.0; //filter the closest schedules

    public static Map<String, ArrayList<StopTime>> findClosestSchedules(
            Map<String, ArrayList<StopTime>> candidateSchedules, Map<String, Double> candidateSumDistance)
            throws TransformException {

        List<Map.Entry<String, Double>> sortedSchedules
                = sortSchedules(candidateSumDistance, candidateSchedules);

        Map<String, ArrayList<StopTime>> closestSchedules = new HashMap<>();

        double leastDis = sortedSchedules.get(0).getValue();
        for (int i = 0; i < sortedSchedules.size(); i++) {
            String tripId = sortedSchedules.get(i).getKey();
            if (sortedSchedules.get(i).getValue() <= (leastDis + DIS_THRESHOLD)) {
                closestSchedules.put(tripId, candidateSchedules.get(tripId));
            }
            else break;
        }

        return closestSchedules;
    }

    public static Map<String, Double> getCandidateSumDistance (ArrayList<BusGpsRecord> run, List<Coordinate> runCoord,
            Map<String, ArrayList<StopTime>> candidateSchedules, Map<String, LineString> shapes)
            throws TransformException{

        Map<String, Double> candidateSumDistance = new HashMap<>();

        for (String candidateSchedule : candidateSchedules.keySet()) {

            LineString line = shapes.get(candidateSchedule);
            LocationIndexedLine indexedLine = new LocationIndexedLine(line);

            if (!isDirection(runCoord.get(0), runCoord.get(1), indexedLine))
                continue;

            ArrayList<StopTime> schedule = candidateSchedules.get(candidateSchedule);
//            double sumDistance = sumDistanceToStops(run, schedule, line);
            double sumDistance = sumDistanceToLine(runCoord, indexedLine);
            candidateSumDistance.put(candidateSchedule, sumDistance);
        }
        return candidateSumDistance;
    }

    private static Double sumDistanceToLine(List<Coordinate> runCoord, LocationIndexedLine indexedLine)
            throws TransformException {

        double sumDistance = 0.0;
        for (int i = 0; i < runCoord.size(); i++) {
            sumDistance += distanceToLine(runCoord.get(i), indexedLine);
        }
        return sumDistance;
    }

    private static Double sumDistanceToStops(ArrayList<BusGpsRecord> run, ArrayList<StopTime> schedule,
            LineString line) throws TransformException {

        double sumDistance = 0.0;
        for (int i = 0; i < run.size(); i++) {
            sumDistance += shortestDistanceToStop(schedule, run.get(i), line);
        }
        return sumDistance;
    }

    private static Double shortestDistanceToStop(ArrayList<StopTime> schedule, BusGpsRecord gps,
            LineString line) throws TransformException {

        double gpsLon = gps.getLon(); double gpsLat = gps.getLat();
        double shortestDistance = getDistance(gpsLon, gpsLat, schedule.get(0).getStop().getLon(),
                schedule.get(0).getStop().getLat());

//        double shortestDistance = getDistance(gpsLon, gpsLat, schedule.get(0).getStop().getLon(),
//                schedule.get(0).getStop().getLat(), line);

        for (int i = 1; i < schedule.size(); i++) {
            double distance = getDistance(gpsLon, gpsLat, schedule.get(i).getStop().getLon(),
                    schedule.get(i).getStop().getLat());
            if (distance < shortestDistance)
                shortestDistance = distance;
        }
        return shortestDistance;
    }
}
