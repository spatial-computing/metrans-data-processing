package edu.usc.imsc.metrans.connection;

import edu.usc.imsc.metrans.timedata.DelayTimeRecord;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileIO {
    private static final Logger logger = LoggerFactory.getLogger(FileIO.class);

    public static boolean writeFile(Route route, ArrayList<DelayTimeRecord> estimatedArrivalTimeResult, String fileName){
        boolean flag = false;
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName, true), StandardCharsets.UTF_8));

            for (DelayTimeRecord busDelay : estimatedArrivalTimeResult) {
                AgencyAndId stop = busDelay.getStopTime().getStop().getId();
                AgencyAndId trip = busDelay.getStopTime().getTrip().getId();
                Integer busId = busDelay.getBusId();
                Integer arrivalTime = busDelay.getStopTime().getArrivalTime();
                long estimatedTime = busDelay.getEstimatedTime();
                Double delay = busDelay.getDelayTime();

                writer.write(route.getId().getId() + ","
                        + stop.getId() + ","
                        + trip.getId() + ","
                        + busId + ","
                        + arrivalTime + ","
                        + estimatedTime + ","
                        + delay);

                writer.newLine();
            }
            flag = true;
            writer.flush();

            logger.info("Writing to " + fileName + ": DONE");
        } catch (IOException e) {
            logger.error("Error writing route " + route.getId().getId() + " to file " + fileName, e);
            flag = false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return flag;
    }
}
