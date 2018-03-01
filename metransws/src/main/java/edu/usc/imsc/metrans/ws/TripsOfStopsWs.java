package edu.usc.imsc.metrans.ws;

import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsStoreProvider;

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
    public Response getRouteStopBasicInfo(@PathParam("routeId") String routeId,
                                          @PathParam("stopId") String stopId) {
        //GtfsStore gtfsStore = GtfsStoreProvider.getGtfsStore();
        //int numRoutes = gtfsStore.getGtfsDao().getAllRoutes().size();

        String sampleRes = "[ { \"id\": \"2\", \"service\": \"weekday\", \"arrivalTime\": \"9:00\" }, { \"id\": \"3\", \"service\": \"saturday\", \"arrivalTime\": \"9:00\" }, { \"id\": \"5\", \"service\": \"saturday\", \"arrivalTime\": \"9:00\" }, { \"id\": \"7\", \"service\": \"sunday\", \"arrivalTime\": \"9:00\" } ]";

        return Response.status(200).entity(sampleRes).build();
    }
}
