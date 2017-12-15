package edu.usc.imsc.metrans.delaytime;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import edu.usc.imsc.metrans.timedata.RunStartTimeEndTime;
import edu.usc.imsc.metrans.timedata.ScheduleStartTimeEndTime;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SchedulePreprocessing {

    private static int errorTime = 10 * 60; // in seconds

    /**
     * Get scheduled trips with [start, end] time covers [start, end] time of the run
     * @param run GPS run
     * @param schedulesOfRoute scheduled trips of the route
     * @param scheduleStartTimeEndTime [start, end] time of scheduled trips
     * @return the scheduled trips with [start, end] time covers [start, end] time of the run
     */
    public static Map<String, ArrayList<StopTime>> getCandidateSchedules(
            ArrayList<BusGpsRecord> run,
            Map<String, ArrayList<StopTime>> schedulesOfRoute,
            Map<String, ScheduleStartTimeEndTime> scheduleStartTimeEndTime) {

        Map<String, ArrayList<StopTime>> candidateSchedules = new HashMap<>();
        RunStartTimeEndTime runStartTimeEndTime = new RunStartTimeEndTime(run);

        for (String schedule : scheduleStartTimeEndTime.keySet()) {

            ScheduleStartTimeEndTime stopTimes = scheduleStartTimeEndTime.get(schedule);
            int runStartTime = runStartTimeEndTime.getRunStartTime();
            int runEndTime = runStartTimeEndTime.getRunEndTime();
            int scheduleStartTime = stopTimes.getScheduleStartTime();
            int scheduleEndTime = stopTimes.getScheduleEndTime();

            if (runStartTime > runEndTime) {
                System.out.println("Attention: runStartTime > runEndTime");
                runEndTime += 24 * 3600;
            }

            if (scheduleStartTime > scheduleEndTime) {
                System.out.println("Attention: scheduleStartTime > scheduleEndTime");
                scheduleEndTime += 24 * 3600;
            }

            if (scheduleStartTime <= (runStartTime + errorTime) && scheduleEndTime >= (runEndTime - errorTime)) {
                candidateSchedules.put(schedule, schedulesOfRoute.get(schedule));
            }
        }
        return candidateSchedules;
    }

    /**
     * Get schedule stop-times of each route
     * @param tripsOfRoute list of trip of the route
     * @param gtfsStore GTFS data store
     * @return map from TripId => StopTimes of that trip for all trips of the route
     */
    public static Map<String, ArrayList<StopTime>> getSchedulesOfRoute(ArrayList<Trip> tripsOfRoute, GtfsStore gtfsStore) {
        Map<String, ArrayList<StopTime>> schedulesOfRoute = new HashMap<>();
        Map<String, ArrayList<StopTime>> tripStopTimes = gtfsStore.getTripStopTimes();
        for (String tripId : tripStopTimes.keySet()) {
            if (tripsOfRoute.contains(tripStopTimes.get(tripId).get(0).getTrip())) {
                schedulesOfRoute.put(tripId, tripStopTimes.get(tripId));
            }
        }
        return schedulesOfRoute;
    }


    /**
     * Get all trips of a given route
     * @param route the route
     * @param gtfsStore GTFS data
     * @return all trips of a given route
     */
    public static ArrayList<Trip> getTripsOfRoute(Route route, GtfsStore gtfsStore) {

        ArrayList<Trip> trips = new ArrayList<>();
        for(Trip trip: gtfsStore.getGtfsDao().getAllTrips()) {
            if (trip.getRoute().getId().equals(route.getId())) {
                trips.add(trip);
            }
        }
        return trips;
    }

    /**
     * Get the start time and end time for each scheduled trip
     * @param stopTimes map from TripId => scheduled stop-times of the trip
     * @return map from TripId => scheduled start and end time of the trip
     */
    public static Map<String, ScheduleStartTimeEndTime> getScheduleStartTimeEndTime(
            Map<String, ArrayList<StopTime>> stopTimes) {

        Map<String, ScheduleStartTimeEndTime> ScheduleStartTimeEndTime = new HashMap<>();
        for (String tripId : stopTimes.keySet()) {
            ScheduleStartTimeEndTime StartTimeEndTime = new ScheduleStartTimeEndTime(stopTimes.get(tripId));
            ScheduleStartTimeEndTime.put(tripId, StartTimeEndTime);
        }
        return ScheduleStartTimeEndTime;
    }

}
