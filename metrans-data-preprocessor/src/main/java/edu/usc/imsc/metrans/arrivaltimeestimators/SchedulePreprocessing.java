package edu.usc.imsc.metrans.arrivaltimeestimators;

import edu.usc.imsc.metrans.busdata.BusGpsRecord;
import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.timedata.RunStartTimeEndTime;
import edu.usc.imsc.metrans.timedata.TripStartTimeEndTime;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SchedulePreprocessing {

    private static final int ERROR_TIME = 10 * 60;

    /**
     * Get candidate trips for  a GPS run.
     *
     * A trip can be a candidate for a GPS run
     * if its time covers the run's time
     * which means: its start time <= run's start time and its end time >= run's end time
     *
     * @param run the GPS run
     * @param tripToStopTimesOfRoute Trip => StopTimes mapping
     * @param tripStartTimeEndTimes Trip => StartTimeEndTime mapping
     * @return all the candidate trips in Trip => StopTimes mapping
     */
    public static Map<String, ArrayList<StopTime>> getCandidateTrips(
            ArrayList<BusGpsRecord> run,
            Map<String, ArrayList<StopTime>> tripToStopTimesOfRoute,
            Map<String, TripStartTimeEndTime> tripStartTimeEndTimes) {

        Map<String, ArrayList<StopTime>> candidateTrips = new HashMap<>();
        RunStartTimeEndTime runStartTimeEndTime = new RunStartTimeEndTime(run);

        for (String trip : tripStartTimeEndTimes.keySet()) {

            TripStartTimeEndTime tripStartTimeEndTime = tripStartTimeEndTimes.get(trip);
            long runStartTime = runStartTimeEndTime.getStartTime();
            long runEndTime = runStartTimeEndTime.getEndTime();
            long tripStartTime = tripStartTimeEndTime.getStartTime();
            long tripEndTime = tripStartTimeEndTime.getEndTime();

            if (runStartTime > runEndTime) {
                System.out.println("Attention: runStartTime > runEndTime");
                runEndTime += 24 * 3600;
            }

            if (tripStartTime > tripEndTime) {
                System.out.println("Attention: scheduleStartTime > scheduleEndTime");
                tripEndTime += 24 * 3600;
            }

            if (tripStartTime <= (runStartTime + ERROR_TIME) && tripEndTime >= (runEndTime - ERROR_TIME)) {
                candidateTrips.put(trip, tripToStopTimesOfRoute.get(trip));
            }
        }
        return candidateTrips;
    }

    /**
     * Get Trip => StopTimes mapping of trips of a route
     * @param tripsOfRoute trips of a route
     * @param gtfsStore GTFS store
     * @return Trip => StopTimes mapping of trips of a route
     */
    public static Map<String, ArrayList<StopTime>> getSchedulesOfRoute(ArrayList<Trip> tripsOfRoute, GtfsStore gtfsStore) {
        Map<String, ArrayList<StopTime>> schedulesOfRoute = new HashMap<>();
        Map<String, ArrayList<StopTime>> stopTimes = gtfsStore.getTripStopTimes();
        for (String trip : stopTimes.keySet()) {
            if (tripsOfRoute.contains(stopTimes.get(trip).get(0).getTrip())) {
                schedulesOfRoute.put(trip, stopTimes.get(trip));
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

    /**
     * Get the start time and end time for each trip
     * @param tripToStopTimes Trip => StopTimes mapping
     * @return Trip => TripStartTimeEndTime mapping
     */
    public static Map<String, TripStartTimeEndTime> getTripStartTimeEndTimes(
            Map<String, ArrayList<StopTime>> tripToStopTimes) {

        Map<String, TripStartTimeEndTime> tripToStartTimeEndTime = new HashMap<>();
        for (String trip : tripToStopTimes.keySet()) {
            TripStartTimeEndTime startTimeEndTime = new TripStartTimeEndTime(tripToStopTimes.get(trip));
            tripToStartTimeEndTime.put(trip, startTimeEndTime);
        }
        return tripToStartTimeEndTime;
    }

}
