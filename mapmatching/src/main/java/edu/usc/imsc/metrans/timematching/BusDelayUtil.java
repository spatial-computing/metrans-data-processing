package edu.usc.imsc.metrans.timematching;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.onebusaway.gtfs.model.StopTime;

import java.io.File;
import java.io.FileOutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static edu.usc.imsc.metrans.timematching.BusDelayPreprocess.getCandidateSchedules;
import static edu.usc.imsc.metrans.timematching.BusDelayPreprocess.getScheduleStartTimeEndTime;
import static edu.usc.imsc.metrans.timematching.SchedulesDetectionUtil.getDistance;

public class BusDelayUtil {

    private static final Integer BUS_GPS_DELAY_TIME = 1800; // threshold 1800s

    public static Map<String, ArrayList<StopTime>> cutOffCandidateStopTimes (
            ArrayList<BusGpsRecord> run, Map<String, ArrayList<StopTime>> candidateStopTimes) {

        RunStartTimeEndTime runStartTimeEndTime = new RunStartTimeEndTime(run);
        Map<String, ArrayList<StopTime>> partCandidateStopTimes = new HashMap<>();

        for (String scheduleId : candidateStopTimes.keySet()) {
            ArrayList<StopTime> partCandidateStopTime = new ArrayList<>();
            ArrayList<StopTime> candidateStopTime = candidateStopTimes.get(scheduleId);
            for (int i = 0; i < candidateStopTime.size(); i++){
                Integer tmpStart = Math.abs(runStartTimeEndTime.getRunStartTime() - candidateStopTime.get(i).getArrivalTime());
                Integer tmpEnd = Math.abs(candidateStopTime.get(i).getArrivalTime() - runStartTimeEndTime.getRunEndTime());
                if ((tmpStart <= BUS_GPS_DELAY_TIME && tmpStart >= 0)
                        || (tmpEnd <= BUS_GPS_DELAY_TIME && tmpEnd >= 0))
                    partCandidateStopTime.add(candidateStopTime.get(i));
            }
            partCandidateStopTimes.put(scheduleId, partCandidateStopTime);
        }
        return partCandidateStopTimes;
    }

    public static Double getAngle(Double stopLon, Double stopLat,
                                  Double gps1Lon, Double gps1Lat,
                                  Double gps2Lon, Double gps2Lat) {

        Double angle = 0.0;

        Double d0 = getDistance(gps1Lon, gps1Lat, gps2Lon, gps2Lat);
        Double d1 = getDistance(gps1Lon, gps1Lat, stopLon, stopLat);
        Double d2 = getDistance(gps2Lon, gps2Lat, stopLon, stopLat);

        if (d0 < (d1 + d2)) {
            Double tmp = (d1 * d1 + d2 * d2 - d0 * d0) / (2 * d1 * d2);
            angle = Math.acos(tmp);
        }

        return angle * 180 / Math.PI;
    }

    public static Integer timeStampToInteger(ZonedDateTime time) {
        return time.getHour() * 3600 + time.getMinute() * 60 + time.getSecond();
    }

    public static ZonedDateTime doubleToZonedDateTime(Double estimatedArrivalTime, ZonedDateTime gpsTime) {

        Double hour = estimatedArrivalTime / 3600;
        Double minute = (estimatedArrivalTime - hour.intValue() * 3600) / 60;
        Double second = (estimatedArrivalTime - hour.intValue() * 3600 - minute.intValue() * 60);
        Double nanoSecond = (estimatedArrivalTime - hour.intValue() * 3600 - minute.intValue() * 60 - second.intValue()) * 1000;


        ZonedDateTime estimatedArrivalZDT = ZonedDateTime.of(gpsTime.getYear(), gpsTime.getMonthValue(), gpsTime.getDayOfMonth(),
                hour.intValue(), minute.intValue(), second.intValue(), nanoSecond.intValue(), gpsTime.getZone());

        return estimatedArrivalZDT;
    }

}
