package edu.usc.imsc.metrans.timematching;

import com.sun.tools.corba.se.idl.InterfaceGen;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.connection.DatabaseIO;
import edu.usc.imsc.metrans.connection.FileIO;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Map;

import static edu.usc.imsc.metrans.timematching.BusDelayComputation.busDelayComputationMain;
import static edu.usc.imsc.metrans.timematching.BusDelayPreprocess.*;
import static edu.usc.imsc.metrans.timematching.SchedulesDetection.schedulesDetectionMain;

public class BusDelayMain {
    private static final Logger logger = LoggerFactory.getLogger(BusDelayMain.class);

    public static void busDelayMain(ArrayList<ArrayList<BusGpsRecord>> allRuns, GtfsStore gtfsStore) {

        ArrayList<BusDelay> estimatedArrivalTimeResult = new ArrayList<>();
        if (allRuns.size() == 0) return;

        // Get routeId
        Route route = GtfsUtil.getRouteFromShortId(gtfsStore, String.valueOf(allRuns.get(0).get(0).getRouteId()));

        // Get all trips for that route
        ArrayList<Trip> tripsOfRoute = getTripsOfRoute(route, gtfsStore);

        // Get all schedules (stop times) for that route
        Map<String, ArrayList<StopTime>> schedulesOfRoute = getSchedulesOfRoute(tripsOfRoute, gtfsStore);

        // Get start time and end time of all schedules
        Map<String, ScheduleStartTimeEndTime> scheduleStartTimeEndTime = getScheduleStartTimeEndTime(schedulesOfRoute);

        for (ArrayList<BusGpsRecord> eachRun : allRuns) {

            // Filter on time interval
            Map<String, ArrayList<StopTime>> candidateSchedules
                    = getCandidateSchedules(eachRun, schedulesOfRoute, scheduleStartTimeEndTime);

            // Detect top n closest schedule candidates based on distance
            Map<String, ArrayList<StopTime>> closestCandidateSchedules
                    = schedulesDetectionMain(eachRun, candidateSchedules);

            if (closestCandidateSchedules.size() != 0) {

                ArrayList<BusDelay> estimatedArrivalTime
                        = busDelayComputationMain(eachRun, closestCandidateSchedules);
                estimatedArrivalTimeResult.addAll(estimatedArrivalTime);
            }
        }

        FileIO.writeFile(route, estimatedArrivalTimeResult);
//        DatabaseIO.writeDatabase(route, estimatedArrivalTimeResult);
    }
}
