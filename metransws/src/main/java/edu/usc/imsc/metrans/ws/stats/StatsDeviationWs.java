package edu.usc.imsc.metrans.ws.stats;

import edu.usc.imsc.metrans.database.DatabaseIO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/main/stats/deviation")
public class StatsDeviationWs {
    @GET
    @Path("overview")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOverallBasicInfo() {
        StatsDeviationInfo info = new StatsDeviationInfo();

        info.setMonth(DatabaseIO.getAvgDeviationByMonthOverall());
        info.setDay(DatabaseIO.getAvgDeviationByHourOfDayOverall());
        info.setWeek(DatabaseIO.getAvgDeviationByDayOfWeekOverall());

        return Response.status(200).entity(info).build();
    }

    @GET
    @Path("route/{routeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteBasicInfo(@PathParam("routeId") int routeId) {
        String sampleRes = "{ \"year\": { \"2015\": 20, \"2016\": 20, \"2017\": 20, \"2018\": 20 }, \"month\": [1,1,1,1,5,1,1,1,1,10,1,12], \"week\": [1,2,3,4,5,6,7], \"day\": [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23] }";

        return Response.status(200).entity(sampleRes).build();
    }

    @GET
    @Path("route/{routeId}/stop/{stopId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteStopBasicInfo(@PathParam("routeId") int routeId,
                                          @PathParam("stopId") int stopId) {
        String sampleRes = "{ \"year\": { \"2015\": 20, \"2016\": 20, \"2017\": 20, \"2018\": 20 }, \"month\": [1,1,1,1,5,1,1,1,1,10,1,12], \"week\": [1,2,3,4,5,6,7], \"day\": [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23] }";

        return Response.status(200).entity(sampleRes).build();
    }

    @GET
    @Path("route/{routeId}/stop/{stopId}/trip/{tripId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteStopTripBasicInfo(@PathParam("routeId") int routeId,
                                              @PathParam("stopId") int stopId,
                                              @PathParam("tripId") int tripId) {
        String sampleRes = "{ \"year\": { \"2015\": 20, \"2016\": 20, \"2017\": 20, \"2018\": 20 }, \"month\": [1,1,1,1,5,1,1,1,1,10,1,12], \"week\": [1,2,3,4,5,6,7], \"day\": [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23] }";

        return Response.status(200).entity(sampleRes).build();
    }
}
