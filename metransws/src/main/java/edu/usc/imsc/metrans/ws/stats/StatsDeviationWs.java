package edu.usc.imsc.metrans.ws.stats;

import edu.usc.imsc.metrans.database.DatabaseIO;
import edu.usc.imsc.metrans.ws.storage.DataCache;

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

        info.setMonth(DataCache.getAvgDeviationsByDatePart(DataCache.AVG_DEVIATION_BY_MONTH_OVERALL));
        info.setDay(DataCache.getAvgDeviationsByDatePart(DataCache.AVG_DEVIATION_BY_HOUR_OVERALL));
        info.setWeek(DataCache.getAvgDeviationsByDatePart(DataCache.AVG_DEVIATION_BY_DOW_OVERALL));

        return Response.status(200).entity(info).build();
    }

    @GET
    @Path("route/{routeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteBasicInfo(@PathParam("routeId") int routeId) {
        StatsDeviationInfo info = new StatsDeviationInfo();

        info.setMonth(DatabaseIO.getAvgDeviationByMonth(routeId));
        info.setDay(DatabaseIO.getAvgDeviationByHourOfDay(routeId));
        info.setWeek(DatabaseIO.getAvgDeviationByDayOfWeek(routeId));

        return Response.status(200).entity(info).build();
    }

    @GET
    @Path("route/{routeId}/stop/{stopId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteStopBasicInfo(@PathParam("routeId") int routeId,
                                          @PathParam("stopId") int stopId) {
        StatsDeviationInfo info = new StatsDeviationInfo();

        info.setMonth(DatabaseIO.getAvgDeviationByMonth(routeId, stopId));
        info.setDay(DatabaseIO.getAvgDeviationByHourOfDay(routeId, stopId));
        info.setWeek(DatabaseIO.getAvgDeviationByDayOfWeek(routeId, stopId));

        return Response.status(200).entity(info).build();
    }

    @GET
    @Path("route/{routeId}/stop/{stopId}/trip/{tripId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRouteStopTripBasicInfo(@PathParam("routeId") int routeId,
                                              @PathParam("stopId") int stopId,
                                              @PathParam("tripId") int tripId) {
        StatsDeviationInfo info = new StatsDeviationInfo();

        info.setMonth(DatabaseIO.getAvgDeviationByMonth(routeId, stopId, tripId));
        info.setDay(DatabaseIO.getAvgDeviationByHourOfDay(routeId, stopId, tripId));
        info.setWeek(DatabaseIO.getAvgDeviationByDayOfWeek(routeId, stopId, tripId));

        return Response.status(200).entity(info).build();
    }
}
