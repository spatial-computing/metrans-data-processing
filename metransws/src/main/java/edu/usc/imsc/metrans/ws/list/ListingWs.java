package edu.usc.imsc.metrans.ws.list;

import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsStoreProvider;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

@Path("/main/shape")
public class ListingWs {
    @GET
    @Path("routes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoutesInfo() {
//        ArrayList<RouteMetadataInfo> infos = new ArrayList<>();
//
//        for (Route route : GtfsStoreProvider.getGtfsStore().getGtfsDao().getAllRoutes()) {
//            RouteMetadataInfo info = new RouteMetadataInfo();
//            info.setDisplay_name(route.getLongName());
//            info.setId(GtfsUtil.toShortRouteId(route.getId().getId()));
//
//            infos.add(info);
//        }
//        infos.sort(new Comparator<RouteMetadataInfo>() {
//            @Override
//            public int compare(RouteMetadataInfo o1, RouteMetadataInfo o2) {
//                return o1.getId().compareTo(o2.getId());
//            }
//        });
        TreeMap<String, String> infos = new TreeMap<>();
        for (Route route : GtfsStoreProvider.getGtfsStore().getGtfsDao().getAllRoutes()) {
            infos.put(GtfsUtil.toShortRouteId(route.getId().getId()), route.getLongName());
        }

        return Response.status(200).entity(infos).build();
    }

    @GET
    @Path("route/{routeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStopsOfRoutes(@PathParam("routeId") int routeId) {
        ArrayList<String> infos = new ArrayList<>();

        return Response.status(200).entity(infos).build();
    }

    @GET
    @Path("route/{routeId}/stop/{stopId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTripsOfStopOfRoute(@PathParam("routeId") int routeId,
                                          @PathParam("stopId") int stopId) {
        ArrayList<TripMetadataInfo> infos = new ArrayList<>();

        try {
            GtfsStore gtfsStore = GtfsStoreProvider.getGtfsStore();
            Route route = GtfsUtil.getRouteFromShortId(gtfsStore, String.valueOf(routeId));
            if (route != null) {
                for (Trip trip : gtfsStore.getRouteTrips().get(route.getId().getId())) {
                    TripMetadataInfo info = new TripMetadataInfo();
                    info.setId(trip.getId().getId());
                    info.setService(GtfsUtil.getServiceDay(trip.getServiceId().getId()));

                    //get stop time
                    boolean stopTimeFound = false;
                    ArrayList<StopTime> stopTimes = gtfsStore.getTripStopTimes().get(trip.getId().getId());
                    for (StopTime stopTime : stopTimes) {
                        if (Integer.valueOf(stopTime.getStop().getId().getId()) == stopId) {
                            //found the stop time
                            int arrivalTime = stopTime.getArrivalTime();
                            info.setArrivalTime(GtfsUtil.getHourMinSec(arrivalTime, "HH:mm"));
                            stopTimeFound = true;
                        }
                    }

                    if (stopTimeFound)
                        infos.add(info);
                }
            } else {
                System.err.println("Can NOT find route for routeId=" + routeId);
            }
        } catch (Exception e) {
            System.err.println("Error finding route for routeId=" + routeId + ":" + e.getMessage());
            e.printStackTrace();
        }

        return Response.status(200).entity(infos).build();
    }
}
