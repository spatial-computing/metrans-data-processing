package edu.usc.imsc.metrans.delaytime;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.connection.DatabaseIO;
import edu.usc.imsc.metrans.connection.FileIO;
import edu.usc.imsc.metrans.connection.OracleIO;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import edu.usc.imsc.metrans.timedata.*;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static edu.usc.imsc.metrans.delaytime.DelayComputation.*;
import static edu.usc.imsc.metrans.delaytime.PolylineUtil.getRunCoord;
import static edu.usc.imsc.metrans.delaytime.SchedulePreprocessing.*;
import static edu.usc.imsc.metrans.delaytime.ClosestSchedules.*;

public class DelayTimeMain {
    private static final Logger logger = LoggerFactory.getLogger(DelayTimeMain.class);

    public static void delayTimeMain(ArrayList<ArrayList<BusGpsRecord>> allRuns, GtfsStore gtfsStore)
            throws TransformException, IOException {

        logger.info("BUS DELAY ESTIMATION START");
        ArrayList<DelayTimeRecord> estimatedArrivalTimeResult = new ArrayList<>();
        if (allRuns.size() == 0) return;

        // Get routeId
        Route route = GtfsUtil.getRouteFromShortId(gtfsStore, String.valueOf(allRuns.get(0).get(0).getRouteId()));

        // Get all trips for that route
        ArrayList<Trip> trips = getTrips(route, gtfsStore);
        logger.info("Total " + trips.size() + " trips");

        // Get all shapes for trips
        Map<String, LineString> shapes = getShapes(trips, gtfsStore);

        // Get all schedules (stop times) for that route
        Map<String, ArrayList<StopTime>> schedules = getSchedules(trips, gtfsStore);

        Integer scheduleTimes = 0;
        for (String schedule: schedules.keySet()) {
            scheduleTimes += schedules.get(schedule).size();
        }
        logger.info("Total " + scheduleTimes + " scheduleTimes");

        // Get start time and end time of all schedules
        Map<String, ScheduleStartTimeEndTime> scheduleStartTimeEndTime = getScheduleStartTimeEndTime(schedules);

        for (int i = 0; i < allRuns.size(); i++) {

            ArrayList<BusGpsRecord> run = allRuns.get(i);

            // Filter on time interval
            Map<String, ArrayList<StopTime>> candidateSchedules
                    = getCandidateSchedulesByTime(run, schedules, scheduleStartTimeEndTime);

            if (candidateSchedules.size() == 0)
                continue;

            // Transform Gps into Coordinate
            List<Coordinate> runCoord = getRunCoord(run);

            // Compute sum distance for candidates
            Map<String, Double> candidateSumDistance
                    = getCandidateSumDistance(run, runCoord, candidateSchedules, shapes);

            if (candidateSumDistance.size() == 0)
                continue;

            // Detect top n closest schedule candidates based on distance
            Map<String, ArrayList<StopTime>> closestSchedules
                    = findClosestSchedules(candidateSchedules, candidateSumDistance);

            if (closestSchedules.size() == 0)
                continue;

            ArrayList<DelayTimeRecord> estimatedArrivalTime
                    = delayComputation(i, run, runCoord, closestSchedules, shapes);

            estimatedArrivalTimeResult.addAll(estimatedArrivalTime);
        }

        logger.info("WRITE BEGIN");
        FileIO.writeFile(route, estimatedArrivalTimeResult);
//        DatabaseIO.writeDatabase(route, estimatedArrivalTimeResult);
//        OracleIO.writeDatabase(route, estimatedArrivalTimeResult);
        logger.info("FINISHED");

    }
}
