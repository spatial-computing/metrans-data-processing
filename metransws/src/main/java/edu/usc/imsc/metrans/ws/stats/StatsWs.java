package edu.usc.imsc.metrans.ws.stats;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/main/stats")
public class StatsWs {
    public static final long INVALID_VALUE = -1;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatsInfo(
            @DefaultValue("deviation") @QueryParam("type") String type,
            @DefaultValue("-1") @QueryParam("route") int routeId,
            @DefaultValue("-1") @QueryParam("stop") int stopId,
            @DefaultValue("-1") @QueryParam("trip") int tripId
    ) {
        String sampleRes = "{ \"year\": { \"2015\": 20, \"2016\": 20, \"2017\": 20, \"2018\": 20 }, \"month\": [1,1,1,1,5,1,1,1,1,10,1,12], \"week\": [1,2,3,4,5,6,7], \"day\": [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23] }";

        switch (type) {
            case "deviation":
                return Response.status(200).entity(StatsDeviationCalculator.getStatsDeviation(routeId, stopId, tripId)).build();
            case "busbunching":
                break;
            case "reliability":
                break;
            case "waitingtime":
                break;
            default:
                sampleRes = "";
                break;
        }

        return Response.status(200).entity(sampleRes).build();
    }
}
