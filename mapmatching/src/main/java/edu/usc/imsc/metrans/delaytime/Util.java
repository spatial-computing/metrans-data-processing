package edu.usc.imsc.metrans.delaytime;


import java.time.ZonedDateTime;
import java.util.*;


public class Util {

    private static final double EARTH_RADIUS = 6378137;

    /**
     * Calculate radian value of a double value
     *
     * @param d double value
     * @return radian value of a double value
     */
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


    /**
     * Calculate the angle between edge (stop, gps1) and (stop, gps2)
     *
     * @return the angle between edge (stop, gps1) and (stop, gps2)
     */
    public static double getAngle(double stopLon, double stopLat,
                                  double gps1Lon, double gps1Lat,
                                  double gps2Lon, double gps2Lat) {

        double angle = 0.0;
        double d12 = calDistance(gps1Lon, gps1Lat, gps2Lon, gps2Lat);
        double d1s = calDistance(gps1Lon, gps1Lat, stopLon, stopLat);
        double d2s = calDistance(gps2Lon, gps2Lat, stopLon, stopLat);

        if (d12 < (d1s + d2s)) {
            double tmp = (d1s * d1s + d2s * d2s - d12 * d12) / (2 * d1s * d2s);
            angle = Math.acos(tmp);
        }

        return angle * 180 / Math.PI;
    }

    /**
     * Calculate distance between 2 (lat, lon) points on Earth surface
     *
     * @param lon1 point 1's longitude
     * @param lat1 point 1's latitude
     * @param lon2 point 2's latitude
     * @param lat2 point 2's latitude
     * @return distance between 2 (lat, lon) points on Earth surface
     */
    public static double calDistance(double lon1, double lat1, double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000.0) / 10000.0;
        return s;
    }


    /**
     * Sort scheduled trips by average distance from a GPS record to a stop of the scheduled trip
     *
     * @param candidateSumDistance map from TripId => avg distance
     * @return list of sorted scheduled trips along with avg distance
     */
    public static List<Map.Entry<String, Double>> sortScheduledTripsByAvgDist(Map<String, Double> candidateSumDistance) {

        List<Map.Entry<String, Double>> candidateSumDistanceList = new ArrayList<>(candidateSumDistance.entrySet());
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


    /**
     * Get the number of seconds from the time object
     *
     * @param time a time object
     * @return the number of seconds from the time object
     */
    public static int getNumSecondsFromMidnight(ZonedDateTime time) {
        return time.getHour() * 3600 + time.getMinute() * 60 + time.getSecond();
    }


    /**
     * Convert a number of seconds from midnight to a time object
     * @param numSecondsFromMidnight a number of seconds from midnight to a time object
     * @param gpsTime a time object to get other values from
     * @return a time object for the given number of seconds from midnight
     */
    public static ZonedDateTime convertDoubleToZonedDateTime(double numSecondsFromMidnight, ZonedDateTime gpsTime) {

        int hour = (int) numSecondsFromMidnight / 3600;
        int minute = (int) (numSecondsFromMidnight - hour * 3600) / 60;
        int second = (int) (numSecondsFromMidnight - hour * 3600 - minute * 60);
        int nanoSecond = (int) ((numSecondsFromMidnight - hour * 3600 - minute * 60 - second) * 1000);

        ZonedDateTime estimatedArrivalZDT = ZonedDateTime.of(gpsTime.getYear(), gpsTime.getMonthValue(), gpsTime.getDayOfMonth(),
                hour, minute, second, nanoSecond, gpsTime.getZone());

        return estimatedArrivalZDT;
    }


}
