package edu.usc.imsc.metrans.ws.stats;

import edu.usc.imsc.metrans.database.DatabaseIO;
import edu.usc.imsc.metrans.ws.storage.DataCache;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/main/stats")
public class StatsWs {
    public static final long INVALID_VALUE = -1;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatsInfo(
            @DefaultValue("") @QueryParam("type") String type,
            @DefaultValue("-1") @QueryParam("route") int routeId,
            @DefaultValue("-1") @QueryParam("stop") int stopId,
            @DefaultValue("-1") @QueryParam("trip") int tripId
    ) {
        String sampleRes = "{ \"year\": { \"2015\": 20, \"2016\": 20, \"2017\": 20, \"2018\": 20 }, \"month\": [1,1,1,1,5,1,1,1,1,10,1,12], \"week\": [1,2,3,4,5,6,7], \"day\": [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23] }";

        switch (type) {
            case "deviation":
                return Response.status(200).entity(getStatsDeviation(routeId, stopId, tripId)).build();
            case "busbunching":
                break;
            case "reliability":
                return Response.status(200).entity(getStatsReliability(routeId, stopId, tripId)).build();
            case "waitingtime":
                return Response.status(200).entity(getStatsMinPosDelay(routeId, stopId, tripId)).build();
            default:
                sampleRes = "";
                break;
        }

        return Response.status(200).entity(sampleRes).build();
    }

    /**
     * Get deviation
     * @param routeId route id
     * @param stopId stop id
     * @param tripId trip id
     * @return stat information for deviation
     */
    public static StatsInfo getStatsDeviation(long routeId, long stopId, long tripId) {
        StatsInfo info = new StatsInfo();
        if (routeId == StatsWs.INVALID_VALUE) {
            //overview
            info.setMonth(DataCache.getAvgDeviationsByDatePart(DataCache.AVG_DEVIATION_BY_MONTH_OVERALL));
            info.setDay(DataCache.getAvgDeviationsByDatePart(DataCache.AVG_DEVIATION_BY_HOUR_OVERALL));
            info.setWeek(DataCache.getAvgDeviationsByDatePart(DataCache.AVG_DEVIATION_BY_DOW_OVERALL));

        } else {
            if (stopId == StatsWs.INVALID_VALUE) {
                //route
                info.setMonth(DatabaseIO.getAvgDeviationByMonth(routeId));
                info.setDay(DatabaseIO.getAvgDeviationByHourOfDay(routeId));
                info.setWeek(DatabaseIO.getAvgDeviationByDayOfWeek(routeId));

            } else {
                if (tripId == StatsWs.INVALID_VALUE) {
                    info.setMonth(DatabaseIO.getAvgDeviationByMonth(routeId, stopId));
                    info.setDay(DatabaseIO.getAvgDeviationByHourOfDay(routeId, stopId));
                    info.setWeek(DatabaseIO.getAvgDeviationByDayOfWeek(routeId, stopId));
                } else {
                    info.setMonth(DatabaseIO.getAvgDeviationByMonth(routeId, stopId, tripId));
                    info.setDay(DatabaseIO.getAvgDeviationByHourOfDay(routeId, stopId, tripId));
                    info.setWeek(DatabaseIO.getAvgDeviationByDayOfWeek(routeId, stopId, tripId));
                }
            }
        }

        return info;
    }


    /**
     * Get MinPosDelay
     * @param routeId route id
     * @param stopId stop id
     * @param tripId trip id
     * @return stat information for MinPosDelay
     */
    public static StatsInfo getStatsMinPosDelay(long routeId, long stopId, long tripId) {
        StatsInfo info = new StatsInfo();
        if (routeId == StatsWs.INVALID_VALUE) {
            //overview
            info.setMonth(DataCache.getAvgMinPosDelayByDatePart(DataCache.AVG_MIN_POS_DELAY_BY_MONTH_OVERALL));
            info.setDay(DataCache.getAvgMinPosDelayByDatePart(DataCache.AVG_MIN_POS_DELAY_BY_HOUR_OVERALL));
            info.setWeek(DataCache.getAvgMinPosDelayByDatePart(DataCache.AVG_MIN_POS_DELAY_BY_DOW_OVERALL));

        } else {
            if (stopId == StatsWs.INVALID_VALUE) {
                //route
                info.setMonth(DatabaseIO.getAvgMinPosDelayByMonth(routeId));
                info.setDay(DatabaseIO.getAvgMinPosDelayByHourOfDay(routeId));
                info.setWeek(DatabaseIO.getAvgMinPosDelayByDayOfWeek(routeId));

            } else {
                if (tripId == StatsWs.INVALID_VALUE) {
                    info.setMonth(DatabaseIO.getAvgMinPosDelayByMonth(routeId, stopId));
                    info.setDay(DatabaseIO.getAvgMinPosDelayByHourOfDay(routeId, stopId));
                    info.setWeek(DatabaseIO.getAvgMinPosDelayByDayOfWeek(routeId, stopId));
                } else {
                    info.setMonth(DatabaseIO.getAvgMinPosDelayByMonth(routeId, stopId, tripId));
                    info.setDay(DatabaseIO.getAvgMinPosDelayByHourOfDay(routeId, stopId, tripId));
                    info.setWeek(DatabaseIO.getAvgMinPosDelayByDayOfWeek(routeId, stopId, tripId));
                }
            }
        }

        return info;
    }


    /**
     * Get reliability
     * @param routeId route id
     * @param stopId stop id
     * @param tripId trip id
     * @return stat information for reliability
     */
    public static StatsInfo getStatsReliability(long routeId, long stopId, long tripId) {
        StatsInfo info = new StatsInfo();
        if (routeId == StatsWs.INVALID_VALUE) {
            //overview
            info.setMonth(DataCache.getReliabilityByDatePart(DataCache.RELIABILITY_BY_MONTH_OVERALL));
            info.setDay(DataCache.getReliabilityByDatePart(DataCache.RELIABILITY_BY_HOUR_OVERALL));
            info.setWeek(DataCache.getReliabilityByDatePart(DataCache.RELIABILITY_BY_DOW_OVERALL));

        } else {
            if (stopId == StatsWs.INVALID_VALUE) {
                //route
                info.setMonth(DatabaseIO.getReliabilityByMonth(routeId));
                info.setDay(DatabaseIO.getReliabilityByHourOfDay(routeId));
                info.setWeek(DatabaseIO.getReliabilityByDayOfWeek(routeId));

            } else {
                if (tripId == StatsWs.INVALID_VALUE) {
                    info.setMonth(DatabaseIO.getReliabilityByMonth(routeId, stopId));
                    info.setDay(DatabaseIO.getReliabilityByHourOfDay(routeId, stopId));
                    info.setWeek(DatabaseIO.getReliabilityByDayOfWeek(routeId, stopId));
                } else {
                    info.setMonth(DatabaseIO.getReliabilityByMonth(routeId, stopId, tripId));
                    info.setDay(DatabaseIO.getReliabilityByHourOfDay(routeId, stopId, tripId));
                    info.setWeek(DatabaseIO.getReliabilityByDayOfWeek(routeId, stopId, tripId));
                }
            }
        }

        return info;
    }
}
