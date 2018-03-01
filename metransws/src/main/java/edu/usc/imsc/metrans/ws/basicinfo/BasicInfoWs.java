package edu.usc.imsc.metrans.ws.basicinfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/main/basicinfo")
public class BasicInfoWs {
    @GET
    @Path("overview")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOverallBasicInfo() {
        OverallBasicInfo info = new OverallBasicInfo();

        info.setAvgDeviation(34.5);
        info.setReliability(0.81);

        info.setNumBusRoutes(144);
        info.setNumBusStops(10000);
        info.setNumDataPoints(1534545);
        info.setStartTime(1451635200);
        info.setEndTime(1514793600);


        return Response.status(200).entity(info).build();
    }

    @GET
    @Path("route/{routeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteBasicInfo(@PathParam("routeId") int routeId) {
        RouteBasicInfo info = new RouteBasicInfo();

        info.setAvgDeviation(55.5);
        info.setReliability(0.91);

        info.setAvgDeviationRank(7);
        info.setNumTripsPerDay(45);
        info.setNumDataPointsPerDay(1500);

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
