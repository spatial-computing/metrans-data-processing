package edu.usc.imsc.metrans.ws.stats;

import edu.usc.imsc.metrans.database.DatabaseIO;
import edu.usc.imsc.metrans.ws.storage.DataCache;

public class StatsDeviationCalculator {

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
}
