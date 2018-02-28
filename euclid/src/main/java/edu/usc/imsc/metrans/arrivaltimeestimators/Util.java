package edu.usc.imsc.metrans.arrivaltimeestimators;


import edu.usc.imsc.metrans.config.Config;
import org.onebusaway.gtfs.model.StopTime;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

public class Util {

    private static final double EARTH_RADIUS = 6378137;
    public static long DELAY_TIME_THRESHOLD = 15 * 60; // time buffer for delay estimation

    private static double rad(double d){
        return d * Math.PI / 180.0;
    }
    
    public static double getAngle(double stopLon, double stopLat,
                                  double gps1Lon, double gps1Lat,
                                  double gps2Lon, double gps2Lat) {

        double angle = 0.0;
        double d0 = getDistance(gps1Lon, gps1Lat, gps2Lon, gps2Lat);
        double d1 = getDistance(gps1Lon, gps1Lat, stopLon, stopLat);
        double d2 = getDistance(gps2Lon, gps2Lat, stopLon, stopLat);

        if (d0 < (d1 + d2)) {
            double tmp = (d1 * d1 + d2 * d2 - d0 * d0) / (2 * d1 * d2);
            angle = Math.acos(tmp);
        }

        return angle * 180 / Math.PI;
    }


    public static double getDistance(double lon1, double lat1, double lon2, double lat2){
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
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

    public static int zonedDateTimeToInteger(ZonedDateTime time) {
        return time.getHour() * 3600 + time.getMinute() * 60 + time.getSecond();
    }

    public static ZonedDateTime convertSecondsToZonedDateTime(long nSeconds) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(nSeconds), Config.zoneId);
    }


    /**
     * Get number of seconds from "noon minus 12 hours" to a specific time
     * @param time the specific time
     * @return number of seconds from "noon minus 12 hours" to a specific time
     */
    public static long getSecondsFromNoonMinus12Hours(ZonedDateTime time) {
        ZonedDateTime noonTime = time.withHour(12).withMinute(0).withSecond(0);
        return time.toEpochSecond() - noonTime.minusHours(12).toEpochSecond();
    }

    /**
     * Get number of seconds from "noon minus 12 hours" to a specific time
     * @param timestamp the specific time
     * @return number of seconds from "noon minus 12 hours" to a specific time
     */
    public static long getSecondsFromNoonMinus12Hours(long timestamp) {
        ZonedDateTime time = convertSecondsToZonedDateTime(timestamp);
        return getSecondsFromNoonMinus12Hours(time);
    }


    /**
     * Get the epoch timestamp for {@code nSeconds} from "noon minus 12 hours" of same {@code theDay}
     * @param nSeconds number of seconds from "noon minus 12 hours"
     * @param theDay the day
     * @return the epoch timestamp
     */
    public static long getEpochTimestampFromSecondsFromNoonMinus12Hours(long nSeconds, ZonedDateTime theDay) {
        ZonedDateTime noonTime = theDay.withHour(12).withMinute(0).withSecond(0);
        return noonTime.minusHours(12).toEpochSecond() + nSeconds;
    }

    public static ZonedDateTime doubleToZonedDateTime(double estimatedArrivalTime, ZonedDateTime gpsTime) {

        int hour = (int)estimatedArrivalTime / 3600;
        int minute = (int)(estimatedArrivalTime - hour * 3600) / 60;
        int second = (int)(estimatedArrivalTime - hour * 3600 - minute * 60);
        int nanoSecond = (int)((estimatedArrivalTime - hour * 3600 - minute * 60 - second) * 1000);

        ZonedDateTime estimatedArrivalZDT = ZonedDateTime.of(gpsTime.getYear(), gpsTime.getMonthValue(), gpsTime.getDayOfMonth(),
                hour, minute, second, nanoSecond, gpsTime.getZone());

        return estimatedArrivalZDT;
    }

}
