package edu.usc.imsc.metrans.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/main/tripinfo")
public class TripsOfStopsWs {

    @GET
    @Path("route/{routeId}/stop/{stopId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteStopBasicInfo(@PathParam("routeId") int routeId,
                                          @PathParam("stopId") int stopId) {
        String sampleRes = "[ { \"id\": \"2\", \"service\": \"weekday\", \"arrivalTime\": \"9:00\" }, { \"id\": \"3\", \"service\": \"saturday\", \"arrivalTime\": \"9:00\" }, { \"id\": \"5\", \"service\": \"saturday\", \"arrivalTime\": \"9:00\" }, { \"id\": \"7\", \"service\": \"sunday\", \"arrivalTime\": \"9:00\" } ]";

        return Response.status(200).entity(sampleRes).build();
    }
}
