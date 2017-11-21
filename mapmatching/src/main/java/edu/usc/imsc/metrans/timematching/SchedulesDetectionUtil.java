package edu.usc.imsc.metrans.timematching;

import org.onebusaway.gtfs.model.StopTime;

import java.util.*;

public class SchedulesDetectionUtil {

    private static final double EARTH_RADIUS = 6378137;
    private static double rad(double d){
        return d * Math.PI / 180.0;
    }


    /**
     * @param lon1
     * @param lat1
     * @param lon2
     * @param lat2
     * @return
     */
    public static Double getDistance(Double lon1, Double lat1, Double lon2, Double lat2){
        Double radLat1 = rad(lat1);
        Double radLat2 = rad(lat2);
        Double a = radLat1 - radLat2;
        Double b = rad(lon1) - rad(lon2);
        Double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000.0) / 10000.0;
        return s;
    }

    public static List<Map.Entry<String, Double>> sortSchedules (Map<String, Double> candidateSumDistance,
                                                         Map<String, ArrayList<StopTime>> candidateSchedules) {

        List<Map.Entry<String, Double>> candidateSumDistanceList = new ArrayList<Map.Entry<String, Double>>(candidateSumDistance.entrySet());
        Collections.sort(candidateSumDistanceList, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
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
