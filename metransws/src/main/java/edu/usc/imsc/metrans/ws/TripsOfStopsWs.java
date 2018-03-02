package edu.usc.imsc.metrans.ws;

import edu.usc.imsc.metrans.gtfsutil.GtfsStore;
import edu.usc.imsc.metrans.gtfsutil.GtfsStoreProvider;
import edu.usc.imsc.metrans.ws.list.ListingWs;

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
        return (new ListingWs().getTripsOfStopOfRoute(routeId, stopId));
    }
}
