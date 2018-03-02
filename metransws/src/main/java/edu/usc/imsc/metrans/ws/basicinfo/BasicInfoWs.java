package edu.usc.imsc.metrans.ws.basicinfo;

import edu.usc.imsc.metrans.database.DatabaseIO;
import edu.usc.imsc.metrans.gtfsutil.GtfsStoreProvider;
import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import edu.usc.imsc.metrans.ws.storage.AvgDeviation;
import edu.usc.imsc.metrans.ws.storage.DataCache;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Trip;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("/main/basicinfo")
public class BasicInfoWs {
    @GET
    @Path("overview")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOverallBasicInfo() {
        OverallBasicInfo info = new OverallBasicInfo();

        AvgDeviation avgDeviation = DatabaseIO.getAvgDeviationAllRoutes();
        if (avgDeviation == null)
            avgDeviation.setAvgDeviation(-99999999999.0);

        info.setAvgDeviation(avgDeviation.getAvgDeviation());
        info.setReliability(DatabaseIO.getReliabilityOverall());

        info.setNumBusRoutes(GtfsStoreProvider.getGtfsStore().getGtfsDao().getAllRoutes().size());
        info.setNumBusStops(GtfsStoreProvider.getGtfsStore().getGtfsDao().getAllStops().size());
        info.setNumDataPoints(DatabaseIO.getEstimatedDataPoints());
        info.setStartTime(DatabaseIO.getMinMaxTime(true));
        info.setEndTime(DatabaseIO.getMinMaxTime(false));


        return Response.status(200).entity(info).build();
    }

    @GET
    @Path("route/{routeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteBasicInfo(@PathParam("routeId") int routeId) {
        RouteBasicInfo info = new RouteBasicInfo();

        ArrayList<AvgDeviation> avgDeviations = DataCache.getAvgDeviationsOfAllRoutes();
        AvgDeviation avgDeviation = null;
        for (AvgDeviation obj: avgDeviations) {
            if (obj.getRouteId() == routeId) {
                avgDeviation = obj;
                break;
            }
        }
        if (avgDeviation != null) {
            info.setAvgDeviation(avgDeviation.getAvgDeviation());
            info.setReliability(DatabaseIO.getReliabilityForRoute(routeId));

            info.setAvgDeviationRank(avgDeviation.getRank());

            //get num trips of this route
            try {
                Route route = GtfsUtil.getRouteFromShortId(GtfsStoreProvider.getGtfsStore(), String.valueOf(routeId));
                if (route != null) {
                    ArrayList<Trip> tripsOfRoute = GtfsStoreProvider.getGtfsStore().getRouteTrips().get(route.getId().getId());
                    info.setNumTripsPerDay(tripsOfRoute.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            info.setNumDataPointsPerDay(-1);
        } else {
            System.err.println("Unable to find route average deviation for route " + routeId);
        }

        return Response.status(200).entity(info).build();
    }

    @GET
    @Path("route/{routeId}/stop/{stopId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteStopBasicInfo(@PathParam("routeId") int routeId,
                                          @PathParam("stopId") int stopId) {
        RouteStopBasicInfo info = new RouteStopBasicInfo();

        info.setAvgDeviation(66.5);
        info.setReliability(0.86);

        info.setAvgDeviationRank(76);
        info.setStopName("Figueroa/Exposition");
        info.setWaitingTimeEstimation(3.2);

        return Response.status(200).entity(info).build();
    }

    @GET
    @Path("route/{routeId}/stop/{stopId}/trip/{tripId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteStopTripBasicInfo(@PathParam("routeId") int routeId,
                                              @PathParam("stopId") int stopId,
                                              @PathParam("tripId") int tripId) {
        RouteStopTripBasicInfo info = new RouteStopTripBasicInfo();

        info.setAvgDeviation(23.5);
        info.setReliability(0.9);

        return Response.status(200).entity(info).build();
    }
}
