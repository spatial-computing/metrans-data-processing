package edu.usc.imsc.metrans.ws.basicinfo;

import edu.usc.imsc.metrans.ws.Constants;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/main/basicinfo")
public class BasicInfoWs {

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBasicInfoWithRequestParams(
            @DefaultValue("-1") @QueryParam("route") int routeId,
            @DefaultValue("-1") @QueryParam("stop") int stopId,
            @DefaultValue("-1") @QueryParam("trip") int tripId) {
        if (routeId == Constants.INVALID_VALUE) {
            //overall
            return Response.status(200).entity(BasicInfoProcessor.getOverallBasicInfo()).build();
        } else {
            if (stopId == Constants.INVALID_VALUE) {
                //route
                return Response.status(200).entity(BasicInfoProcessor.getRouteBasicInfo(routeId)).build();
            } else {
                if (tripId == Constants.INVALID_VALUE) {
                    //route-stop
                    return Response.status(200).entity(BasicInfoProcessor.getRouteStopBasicInfo(routeId, stopId)).build();
                } else {
                    //route-stop-trip
                    return Response.status(200).entity(BasicInfoProcessor.getRouteStopTripBasicInfo(routeId, stopId, tripId)).build();
                }
            }
        }
    }

    @GET
    @Path("overview")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOverallBasicInfo() {
        return Response.status(200).entity(BasicInfoProcessor.getOverallBasicInfo()).build();
    }

    @GET
    @Path("route/{routeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteBasicInfo(@PathParam("routeId") int routeId) {
        return Response.status(200).entity(BasicInfoProcessor.getRouteBasicInfo(routeId)).build();
    }

    @GET
    @Path("route/{routeId}/stop/{stopId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteStopBasicInfo(@PathParam("routeId") int routeId,
                                          @PathParam("stopId") int stopId) {
        return Response.status(200).entity(BasicInfoProcessor.getRouteStopBasicInfo(routeId, stopId)).build();
    }

    @GET
    @Path("route/{routeId}/stop/{stopId}/trip/{tripId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteStopTripBasicInfo(@PathParam("routeId") int routeId,
                                              @PathParam("stopId") int stopId,
                                              @PathParam("tripId") int tripId) {
        return Response.status(200).entity(BasicInfoProcessor.getRouteStopTripBasicInfo(routeId, stopId, tripId)).build();
    }
}
