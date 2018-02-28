package edu.usc.imsc.metrans.arrivaltimeestimators;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.timedata.RunStartTimeEndTime;
import edu.usc.imsc.metrans.timedata.ScheduleStartTimeEndTime;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SchedulePreprocessing {

    private static int errorTime = 10 * 60;

    public static Map<String, ArrayList<StopTime>> getCandidateSchedules(
            ArrayList<BusGpsRecord> run,
            Map<String, ArrayList<StopTime>> schedulesOfRoute,
            Map<String, ScheduleStartTimeEndTime> scheduleStartTimeEndTime) {

        Map<String, ArrayList<StopTime>> candidateSchedules = new HashMap<>();
        RunStartTimeEndTime runStartTimeEndTime = new RunStartTimeEndTime(run);

        for (String schedule : scheduleStartTimeEndTime.keySet()) {

            ScheduleStartTimeEndTime stopTimes = scheduleStartTimeEndTime.get(schedule);
            long runStartTime = runStartTimeEndTime.getRunStartTime();
            long runEndTime = runStartTimeEndTime.getRunEndTime();
            long scheduleStartTime = stopTimes.getScheduleStartTime();
            long scheduleEndTime = stopTimes.getScheduleEndTime();

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

    public static Map<String, ArrayList<StopTime>> getSchedulesOfRoute(ArrayList<Trip> tripsOfRoute, GtfsStore gtfsStore) {
        Map<String, ArrayList<StopTime>> schedulesOfRoute = new HashMap<>();
        Map<String, ArrayList<StopTime>> stopTimes = gtfsStore.getTripStopTimes();
        for (String stopTime : stopTimes.keySet()) {
            if (tripsOfRoute.contains(stopTimes.get(stopTime).get(0).getTrip())) {
                schedulesOfRoute.put(stopTime, stopTimes.get(stopTime));
            }
        }
        return schedulesOfRoute;
    }

    public static ArrayList<Trip> getTripsOfRoute(Route route, GtfsStore gtfsStore) {

        ArrayList<Trip> trips = new ArrayList<>();
        for(Trip trip: gtfsStore.getGtfsDao().getAllTrips()) {
            if (trip.getRoute().equals(route)) {
                trips.add(trip);
            }
        }
        return trips;
    }

    // Get the start time and end time for each schedule
    public static Map<String, ScheduleStartTimeEndTime> getScheduleStartTimeEndTime(
            Map<String, ArrayList<StopTime>> stopTimes) {

        Map<String, ScheduleStartTimeEndTime> ScheduleStartTimeEndTime = new HashMap<>();
        for (String eachStopTime : stopTimes.keySet()) {
            ScheduleStartTimeEndTime StartTimeEndTime = new ScheduleStartTimeEndTime(stopTimes.get(eachStopTime));
            ScheduleStartTimeEndTime.put(eachStopTime, StartTimeEndTime);
        }
        return ScheduleStartTimeEndTime;
    }

}
