package edu.usc.imsc.metrans.connection;

import edu.usc.imsc.metrans.timedata.DelayTimeRecord;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;

import java.io.File;
import java.io.FileOutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class FileIO {

    public static Boolean writeFile(Route route, ArrayList<DelayTimeRecord> estimatedArrivalTimeResult) {

        try {
            if(writeHelper(route, estimatedArrivalTimeResult, "./data/estimatedArrivalTime.txt"))
                return true;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }


    public static boolean writeHelper(Route route, ArrayList<DelayTimeRecord> estimatedArrivalTimeResult, String fileName) throws Exception{
        boolean flag = false;
        File file = new File(fileName);
        FileOutputStream o = null;
        try {
            if(!file.exists()) {
                file.createNewFile();
                o = new FileOutputStream(file);

                o.write(("routeId, runId, stopId, tripId, busId, scheduleTime, estimatedTime, delay\n").getBytes("GBK"));

                for (DelayTimeRecord busDelay : estimatedArrivalTimeResult) {
                    String stop = busDelay.getStopTime().getStop().getId().getId();
                    String trip = busDelay.getStopTime().getTrip().getId().getId();
                    Integer runId = busDelay.getRunId();
                    Integer busId = busDelay.getBusId();
                    Integer arrivalTime = busDelay.getStopTime().getArrivalTime();
                    ZonedDateTime estimatedTime = busDelay.getEstimatedTime();

                    Double delay = busDelay.getDelayTime();
                    o.write((route.getId() + "," + stop + "," + trip + "," + busId + "," +
                            arrivalTime + "," + estimatedTime + "," + delay + "\n").getBytes("GBK"));
                }
                o.close();
                flag = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return flag;
    }
}
