package edu.usc.imsc.metrans.delaytime;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.connection.DatabaseIO;
import edu.usc.imsc.metrans.connection.FileIO;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import edu.usc.imsc.metrans.timedata.*;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

import static edu.usc.imsc.metrans.delaytime.DelayComputation.*;
import static edu.usc.imsc.metrans.delaytime.SchedulePreprocessing.*;
import static edu.usc.imsc.metrans.delaytime.ClosestSchedules.*;

public class DelayTimeMain {
    private static final Logger logger = LoggerFactory.getLogger(DelayTimeMain.class);

    public static void delayTimeMain(ArrayList<ArrayList<BusGpsRecord>> allRuns, GtfsStore gtfsStore) {

        logger.info("BUS DELAY ESTIMATION START");
        ArrayList<DelayTimeRecord> estimatedArrivalTimeResult = new ArrayList<>();
        if (allRuns.size() == 0) return;

        // Get routeId
        Route route = GtfsUtil.getRouteFromShortId(gtfsStore, String.valueOf(allRuns.get(0).get(0).getRouteId()));

        // Get all trips for that route
        ArrayList<Trip> tripsOfRoute = getTripsOfRoute(route, gtfsStore);
        logger.info("Total " + tripsOfRoute.size() + " trips");

        // Get all schedules (stop times) for that route
        Map<String, ArrayList<StopTime>> schedulesOfRoute = getSchedulesOfRoute(tripsOfRoute, gtfsStore);

        Integer scheduleTimes = 0;
        for (String schedule: schedulesOfRoute.keySet()) {
            scheduleTimes += schedulesOfRoute.get(schedule).size();
        }
        logger.info("Total " + scheduleTimes + " scheduleTimes");

        // Get start time and end time of all schedules
        Map<String, ScheduleStartTimeEndTime> scheduleStartTimeEndTime = getScheduleStartTimeEndTime(schedulesOfRoute);

        for (ArrayList<BusGpsRecord> run : allRuns) {

            // Filter on time interval
            Map<String, ArrayList<StopTime>> candidateSchedules
                    = getCandidateSchedules(run, schedulesOfRoute, scheduleStartTimeEndTime);

            // Detect top n closest schedule candidates based on distance
            Map<String, ArrayList<StopTime>> closestCandidateSchedules
                    = findClosestSchedules(run, candidateSchedules);

            if (closestCandidateSchedules.size() != 0) {

                ArrayList<DelayTimeRecord> estimatedArrivalTime
                        = delayComputation(run, closestCandidateSchedules);
                estimatedArrivalTimeResult.addAll(estimatedArrivalTime);

            }
        }

        logger.info("WRITE BEGIN");
        FileIO.writeFile(route, estimatedArrivalTimeResult);
//        DatabaseIO.writeDatabase(route, estimatedArrivalTimeResult);
        logger.info("FINISHED");

    }
}