package edu.usc.imsc.metrans.connection;

import edu.usc.imsc.metrans.timematching.BusDelay;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;

import java.io.File;
import java.io.FileOutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class FileIO {

    public static void writeFile(Route route, ArrayList<BusDelay> estimatedArrivalTimeResult) {

        try {
            if(writeHelper(route, estimatedArrivalTimeResult, "./data/estimatedArrivalTime.txt"))
                System.out.println("Finished");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    public static boolean writeHelper(Route route, ArrayList<BusDelay> estimatedArrivalTimeResult, String fileName) throws Exception{
        boolean flag = false;
        File file = new File(fileName);
        FileOutputStream o = null;
        try {
            if(!file.exists()) {
                file.createNewFile();
                o = new FileOutputStream(file);

                for (BusDelay busDelay : estimatedArrivalTimeResult) {
                    AgencyAndId stop = busDelay.getStopTime().getStop().getId();
                    AgencyAndId trip = busDelay.getStopTime().getTrip().getId();
                    Integer busId = busDelay.getBusId();
                    Integer arrivalTime = busDelay.getStopTime().getArrivalTime();
                    ZonedDateTime estimatedTime = busDelay.getEstimatedTime();

                    Double delay = busDelay.getDelayTime();
                    o.write((route.getId() + "," + stop + "," + trip + "," + busId + "," + arrivalTime + "," + estimatedTime + "," + delay + "\n").getBytes("GBK"));
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
