package edu.usc.imsc.metrans.database;


import edu.usc.imsc.metrans.ws.storage.DbItemInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

public class DatabaseIO {
    /*
     * Average Deviation
     */

    private static final String SELECT_AVG_DEVIATION_OVERALL =
            " SELECT SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation_all_routes " +
                    " FROM etd_avg_deviation_month_mv ";

    private static final String SELECT_AVG_DEVIATION_OF_ROUTES =
            "SELECT route_id, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_month_mv " +
                    " GROUP BY route_id ";

    private static final String SELECT_AVG_DEVIATION_OF_STOPS_OF_ROUTE =
            "SELECT stop_id, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_month_mv " +
                    " WHERE route_id = ?" +
                    " GROUP BY stop_id";

    private static final String SELECT_AVG_DEVIATION_OF_TRIP_OF_STOP_OF_ROUTE =
            "SELECT SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_month_mv " +
                    " WHERE route_id = ? AND stop_id = ? AND trip_id = ?";



    private static final String SELECT_AVG_DEVIATION_OVERALL_BY_MONTH =
            "SELECT date_month AS d_part, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_month_mv " +
                    " GROUP BY date_month " +
                    " ORDER BY date_month ";

    private static final String SELECT_AVG_DEVIATION_OVERALL_BY_DOW =
            "SELECT date_dow AS d_part, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_dow_mv " +
                    " GROUP BY date_dow " +
                    " ORDER BY date_dow ";

    private static final String SELECT_AVG_DEVIATION_OVERALL_BY_HOUR =
            "SELECT date_hour AS d_part, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_hour_mv " +
                    " GROUP BY date_hour " +
                    " ORDER BY date_hour ";


    private static final String SELECT_AVG_DEVIATION_BY_MONTH_OF_ROUTE =
            "SELECT date_month AS d_part, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_month_mv " +
                    " WHERE route_id = ?" +
                    " GROUP BY date_month " +
                    " ORDER BY date_month ";

    private static final String SELECT_AVG_DEVIATION_BY_DOW_OF_ROUTE =
            "SELECT date_dow AS d_part, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_dow_mv " +
                    " WHERE route_id = ?" +
                    " GROUP BY date_dow " +
                    " ORDER BY date_dow ";

    private static final String SELECT_AVG_DEVIATION_BY_HOUR_OF_ROUTE =
            "SELECT date_hour AS d_part, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_hour_mv " +
                    " WHERE route_id = ?" +
                    " GROUP BY date_hour " +
                    " ORDER BY date_hour ";

    private static final String SELECT_AVG_DEVIATION_BY_MONTH_OF_STOP_OF_ROUTE =
            "SELECT date_month AS d_part, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_month_mv " +
                    " WHERE route_id = ? " +
                    " AND stop_id = ? " +
                    " GROUP BY date_month " +
                    " ORDER BY date_month ";

    private static final String SELECT_AVG_DEVIATION_BY_DOW_OF_STOP_OF_ROUTE =
            "SELECT date_dow AS d_part, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_dow_mv " +
                    " WHERE route_id = ?" +
                    " AND stop_id = ? " +
                    " GROUP BY date_dow " +
                    " ORDER BY date_dow ";

    private static final String SELECT_AVG_DEVIATION_BY_HOUR_OF_STOP_OF_ROUTE =
            "SELECT date_hour AS d_part, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_hour_mv " +
                    " WHERE route_id = ?" +
                    " AND stop_id = ? " +
                    " GROUP BY date_hour " +
                    " ORDER BY date_hour ";

    private static final String SELECT_AVG_DEVIATION_BY_MONTH_OF_TRIP_OF_STOP_OF_ROUTE =
            "SELECT date_month AS d_part, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_month_mv " +
                    " WHERE route_id = ? " +
                    " AND stop_id = ? " +
                    " AND trip_id = ? " +
                    " GROUP BY date_month " +
                    " ORDER BY date_month ";

    private static final String SELECT_AVG_DEVIATION_BY_DOW_OF_TRIP_OF_STOP_OF_ROUTE =
            "SELECT date_dow AS d_part, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_dow_mv " +
                    " WHERE route_id = ?" +
                    " AND stop_id = ? " +
                    " AND trip_id = ? " +
                    " GROUP BY date_dow " +
                    " ORDER BY date_dow ";

    private static final String SELECT_AVG_DEVIATION_BY_HOUR_OF_TRIP_OF_STOP_OF_ROUTE =
            "SELECT date_hour AS d_part, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_hour_mv " +
                    " WHERE route_id = ?" +
                    " AND stop_id = ? " +
                    " AND trip_id = ? " +
                    " GROUP BY date_hour " +
                    " ORDER BY date_hour ";


    /*
     * Min Positive Delay
     */
    private static final String SELECT_AVG_MIN_POS_DELAY_OF_ROUTES =
            "SELECT route_id, SUM((avg_min_pos_delay * num_estimations)) / SUM(num_estimations) as avg_min_pos_delay " +
                    " FROM empd_avg_mpd_month_mv " +
                    " GROUP BY route_id ";

    private static final String SELECT_AVG_MIN_POS_DELAY_OF_STOPS_OF_ROUTE =
            "SELECT stop_id, SUM((avg_min_pos_delay * num_estimations)) / SUM(num_estimations) as avg_min_pos_delay " +
                    " FROM empd_avg_mpd_month_mv " +
                    " WHERE route_id = ?" +
                    " GROUP BY stop_id";

    private static final String SELECT_MIN_MAX_DATE =
            "SELECT MIN(date_estimated_time) AS min_date, MAX(date_estimated_time) AS max_date " +
                    " FROM estimated_arrival_time";

    private static final String SELECT_ESTIMATED_DATA_POINTS =
            "SELECT reltuples::BIGINT AS estimated_data_points " +
                    " FROM pg_class " +
                    " WHERE relname='estimated_arrival_time'";

    private static final String SELECT_ONTIME_COUNT_OVERALL =
            "SELECT SUM(num_ontime_estimations)::BIGINT AS sum_ontimes, SUM(num_estimations)::BIGINT AS sum_estimations " +
                    " FROM etd_ontime_count_mv";

    private static final String SELECT_ONTIME_COUNT_FOR_ROUTE =
            "SELECT SUM(num_ontime_estimations)::BIGINT AS sum_ontimes, SUM(num_estimations)::BIGINT AS sum_estimations " +
                    " FROM etd_ontime_count_mv " +
                    " WHERE route_id=?";

    private static final String SELECT_ONTIME_COUNT_FOR_ROUTE_AND_STOP =
            "SELECT SUM(num_ontime_estimations)::BIGINT AS sum_ontimes, SUM(num_estimations)::BIGINT AS sum_estimations " +
                    " FROM etd_ontime_count_mv " +
                    " WHERE route_id=? AND stop_id=?";

    private static final String SELECT_ONTIME_COUNT_FOR_ROUTE_AND_STOP_AND_TRIP =
            "SELECT SUM(num_ontime_estimations)::BIGINT AS sum_ontimes, SUM(num_estimations)::BIGINT AS sum_estimations " +
                    " FROM etd_ontime_count_mv " +
                    " WHERE route_id=? AND stop_id=? AND trip_id=?";


    /*
     * BUS BUNCHING
     */
    private static final String SELECT_BUS_BUNCHING_BY_DOW_OF_STOP_OF_ROUTE =
            "SELECT date_part('dow', date(timezone('America/Los_Angeles'::text, date_estimated_time)))::BIGINT AS d_part, COUNT(CASE WHEN num_buses > 1 THEN 1 END) AS num_bunching, COUNT(*) AS num_estimations  " +
                    " FROM bus_bunching " +
                    " WHERE route_id = ?" +
                    " AND stop_id = ? " +
                    " GROUP BY date_part('dow', date(timezone('America/Los_Angeles'::text, date_estimated_time))) " +
                    " ORDER BY date_part('dow', date(timezone('America/Los_Angeles'::text, date_estimated_time))) ";


    private static Connection getConnection() {
        Connection con = null;
        String url = "jdbc:postgresql://dsicloud2.usc.edu:5432/metrans";
        String user = "metrans";
        String password = "Bg86526Us";
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.err.println("Error creating connection: " + e.getMessage());
            e.printStackTrace();
        }

        return con;
    }

    /**
     * Get min/max date time
     * @param toGetMin get min (true) or max (false)
     * @return timestamp of min/ax date time or -1 if error occurred
     */
    public static long getMinMaxTime(boolean toGetMin) {
        long result = -1;
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return result;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_MIN_MAX_DATE);

            ResultSet rs = psql.executeQuery();

            while(rs.next()){
                if (toGetMin)
                    result = rs.getTimestamp("min_date").getTime() / 1000;
                else
                    result = rs.getTimestamp("max_date").getTime() / 1000;
            }

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting min/max date time:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Get estimated data points
     * @return estimated data points or -1 if error occur
     */
    public static long getEstimatedDataPoints() {
        long result = -1;
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return result;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_ESTIMATED_DATA_POINTS);

            ResultSet rs = psql.executeQuery();

            while(rs.next()){
                result = rs.getLong("estimated_data_points");
            }

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting estimated data points:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Get average arrival time deviation of all routes
     * @return average arrival time deviation of all routes or {@code null} if error occur
     */
    public static DbItemInfo getAvgDeviationOverall() {
        DbItemInfo result = null;
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return result;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_AVG_DEVIATION_OVERALL);

            ResultSet rs = psql.executeQuery();

            while(rs.next()){
                result = new DbItemInfo();
                result.setTimeDiff(rs.getDouble("avg_deviation_all_routes"));
            }

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting average arrival time deviation of all routes:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Get list of average arrival time deviation of all routes
     * @return list of average arrival time deviation of all routes or empty list if error occur
     */
    public static ArrayList<DbItemInfo> getAvgDeviationPerRoutes() {
        ArrayList<DbItemInfo> results = new ArrayList<>();
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return results;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_AVG_DEVIATION_OF_ROUTES);

            ResultSet rs = psql.executeQuery();

            while(rs.next()){
                DbItemInfo avgDeviation = new DbItemInfo();
                avgDeviation.setRouteId(rs.getLong("route_id"));
                avgDeviation.setTimeDiff(rs.getDouble("avg_deviation"));

                results.add(avgDeviation);
            }

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting average arrival time deviation of all routes:"  + e.getMessage());
            e.printStackTrace();
            results = new ArrayList<>();
        }

        return results;
    }


    /**
     * Get list of average arrival time deviation of stops of a route
     * @return list of average arrival time deviation of stops of a route or empty list if error occur
     */
    public static ArrayList<DbItemInfo> getAvgDeviationOfStopsOfRoute(Long routeId) {
        ArrayList<DbItemInfo> results = new ArrayList<>();
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return results;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_AVG_DEVIATION_OF_STOPS_OF_ROUTE);
            psql.setLong(1, routeId);

            ResultSet rs = psql.executeQuery();

            while(rs.next()){
                DbItemInfo avgDeviation = new DbItemInfo();
                avgDeviation.setStopId(rs.getLong("stop_id"));
                avgDeviation.setTimeDiff(rs.getDouble("avg_deviation"));

                results.add(avgDeviation);
            }

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting list of average arrival time deviation of stops of a route " + routeId
                    + ":"  + e.getMessage());
            e.printStackTrace();
            results = new ArrayList<>();
        }

        return results;
    }


    /**
     * Get average arrival time deviation of a trip of a stop of a route
     * @return list of average arrival time deviation of a trip of a stop of a route or {@code null} if error occurred
     */
    public static DbItemInfo getAvgDeviationOfTripOfStopOfRoute(long routeId, long stopId, long tripId) {
        DbItemInfo result = null;
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return result;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_AVG_DEVIATION_OF_TRIP_OF_STOP_OF_ROUTE);
            psql.setLong(1, routeId);
            psql.setLong(2, stopId);
            psql.setLong(3, tripId);

            ResultSet rs = psql.executeQuery();

            while(rs.next()){
                result = new DbItemInfo();
                result.setTimeDiff(rs.getDouble("avg_deviation"));
            }

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting list of average arrival time deviation of a trip of a stop of a route " + routeId
                    + ":"  + e.getMessage());
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    /**
     * Get average arrival time deviation of all routes by month
     * @return list of average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getAvgDeviationByMonth() {
        ArrayList<Long> params = new ArrayList<>();
        return getAvgDeviationByDatePart(SELECT_AVG_DEVIATION_OVERALL_BY_MONTH, params, Calendar.MONTH);
    }

    /**
     * Get average arrival time deviation of all routes by day of week
     * @return list of average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getAvgDeviationByDayOfWeek() {
        ArrayList<Long> params = new ArrayList<>();
        return getAvgDeviationByDatePart(SELECT_AVG_DEVIATION_OVERALL_BY_DOW, params, Calendar.DAY_OF_WEEK);
    }

    /**
     * Get average arrival time deviation of all routes by hour of day
     * @return list of  average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getAvgDeviationByHourOfDay() {
        ArrayList<Long> params = new ArrayList<>();
        return getAvgDeviationByDatePart(SELECT_AVG_DEVIATION_OVERALL_BY_HOUR, params, Calendar.HOUR_OF_DAY);
    }


    /**
     * Get average arrival time deviation of a route by month
     * @param routeId route id
     * @return list of average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getAvgDeviationByMonth(long routeId) {
        ArrayList<Long> params = new ArrayList<>();
        params.add(routeId);
        return getAvgDeviationByDatePart(SELECT_AVG_DEVIATION_BY_MONTH_OF_ROUTE, params, Calendar.MONTH);
    }

    /**
     * Get average arrival time deviation of a route by day of week
     * @param routeId route id
     * @return list of average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getAvgDeviationByDayOfWeek(long routeId) {
        ArrayList<Long> params = new ArrayList<>();
        params.add(routeId);
        return getAvgDeviationByDatePart(SELECT_AVG_DEVIATION_BY_DOW_OF_ROUTE, params, Calendar.DAY_OF_WEEK);
    }

    /**
     * Get average arrival time deviation of a route by hour of day
     * @param routeId route id
     * @return list of  average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getAvgDeviationByHourOfDay(long routeId) {
        ArrayList<Long> params = new ArrayList<>();
        params.add(routeId);
        return getAvgDeviationByDatePart(SELECT_AVG_DEVIATION_BY_HOUR_OF_ROUTE, params, Calendar.HOUR_OF_DAY);
    }


    /**
     * Get average arrival time deviation of a stop of a route by month
     * @param routeId route id
     * @param stopId stop id
     * @return list of average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getAvgDeviationByMonth(long routeId, long stopId) {
        ArrayList<Long> params = new ArrayList<>();
        params.add(routeId);
        params.add(stopId);
        return getAvgDeviationByDatePart(SELECT_AVG_DEVIATION_BY_MONTH_OF_STOP_OF_ROUTE, params, Calendar.MONTH);
    }

    /**
     * Get average arrival time deviation of a stop of a route by day of week
     * @param routeId route id
     * @param stopId stop id
     * @return list of average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getAvgDeviationByDayOfWeek(long routeId, long stopId) {
        ArrayList<Long> params = new ArrayList<>();
        params.add(routeId);
        params.add(stopId);
        return getAvgDeviationByDatePart(SELECT_AVG_DEVIATION_BY_DOW_OF_STOP_OF_ROUTE, params, Calendar.DAY_OF_WEEK);
    }

    /**
     * Get average arrival time deviation of a stop of a route by hour of day
     * @param routeId route id
     * @param stopId stop id
     * @return list of  average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getAvgDeviationByHourOfDay(long routeId, long stopId) {
        ArrayList<Long> params = new ArrayList<>();
        params.add(routeId);
        params.add(stopId);
        return getAvgDeviationByDatePart(SELECT_AVG_DEVIATION_BY_HOUR_OF_STOP_OF_ROUTE, params, Calendar.HOUR_OF_DAY);
    }

    /**
     * Get average arrival time deviation of a trip of a stop of a route by month
     * @param routeId route id
     * @param stopId stop id
     * @param tripId trip id
     * @return list of average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getAvgDeviationByMonth(long routeId, long stopId, long tripId) {
        ArrayList<Long> params = new ArrayList<>();
        params.add(routeId);
        params.add(stopId);
        params.add(tripId);
        return getAvgDeviationByDatePart(SELECT_AVG_DEVIATION_BY_MONTH_OF_TRIP_OF_STOP_OF_ROUTE, params, Calendar.MONTH);
    }

    /**
     * Get average arrival time deviation of a trip of a stop of a route by day of week
     * @param routeId route id
     * @param stopId stop id
     * @param tripId trip id
     * @return list of average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getAvgDeviationByDayOfWeek(long routeId, long stopId, long tripId) {
        ArrayList<Long> params = new ArrayList<>();
        params.add(routeId);
        params.add(stopId);
        params.add(tripId);
        return getAvgDeviationByDatePart(SELECT_AVG_DEVIATION_BY_DOW_OF_TRIP_OF_STOP_OF_ROUTE, params, Calendar.DAY_OF_WEEK);
    }

    /**
     * Get average arrival time deviation of a trip of a stop of a route by hour of day
     * @param routeId route id
     * @param stopId stop id
     * @param tripId trip id
     * @return list of  average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getAvgDeviationByHourOfDay(long routeId, long stopId, long tripId) {
        ArrayList<Long> params = new ArrayList<>();
        params.add(routeId);
        params.add(stopId);
        params.add(tripId);
        return getAvgDeviationByDatePart(SELECT_AVG_DEVIATION_BY_HOUR_OF_TRIP_OF_STOP_OF_ROUTE, params, Calendar.HOUR_OF_DAY);
    }


    /**
     * Get average arrival time deviation of all routes by date part
     * @return average arrival time deviation of all routes or empty list if error occur
     */
    private static ArrayList<Double> getAvgDeviationByDatePart(String sqlStatement, ArrayList<Long> params, int datePartType) {
        ArrayList<Double> results = new ArrayList<>();
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return results;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(sqlStatement);
            if (params != null)
                for (int i = 0; i < params.size(); i++)
                    psql.setLong(i + 1, params.get(i));

            results = getAvgDeviationByDatePart(psql, datePartType);

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting average arrival time deviation of all routes by date part:"  + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Get average arrival time deviation by date part
     * @return average arrival time deviation by date part or empty list if error occurred
     */
    private static ArrayList<Double> getAvgDeviationByDatePart(PreparedStatement psql, int datePartType) {
        ArrayList<Double> results = new ArrayList<>();
        try {
            ResultSet rs = psql.executeQuery();

            TreeMap<Long, Double> rows = new TreeMap<>();
            while(rs.next()){
                rows.put(rs.getLong("d_part"), rs.getDouble("avg_deviation"));
            }

            results = parseDataForDatePart(rows, datePartType);

        } catch (SQLException e) {
            System.err.println("Error selecting average arrival time deviation by date part:"  + e.getMessage());
            e.printStackTrace();
            results = new ArrayList<>();
        }

        return results;
    }



    /**
     * Get list of average min positive delay of all routes
     * @return list of average min positive delay of all routes or empty list if error occur
     */
    public static ArrayList<DbItemInfo> getAvgMinPosDelayPerRoutes() {
        ArrayList<DbItemInfo> results = new ArrayList<>();
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return results;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_AVG_MIN_POS_DELAY_OF_ROUTES);

            ResultSet rs = psql.executeQuery();

            while(rs.next()){
                DbItemInfo avgMinPosDelay = new DbItemInfo();
                avgMinPosDelay.setRouteId(rs.getLong("route_id"));
                avgMinPosDelay.setTimeDiff(rs.getDouble("avg_min_pos_delay"));

                results.add(avgMinPosDelay);
            }

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting average min positive delay of all routes:"  + e.getMessage());
            e.printStackTrace();
            results = new ArrayList<>();
        }

        return results;
    }


    /**
     * Get list of average min positive delay of stops of a route
     * @return list of average min positive delay of all routes or empty list if error occur
     */
    public static ArrayList<DbItemInfo> getAvgMinPosDelayOfStopsOfRoute(Long routeId) {
        ArrayList<DbItemInfo> results = new ArrayList<>();
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return results;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_AVG_MIN_POS_DELAY_OF_STOPS_OF_ROUTE);
            psql.setLong(1, routeId);

            ResultSet rs = psql.executeQuery();

            while(rs.next()){
                DbItemInfo avgMinPosDelay = new DbItemInfo();
                avgMinPosDelay.setStopId(rs.getLong("stop_id"));
                avgMinPosDelay.setTimeDiff(rs.getDouble("avg_min_pos_delay"));

                results.add(avgMinPosDelay);
            }

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting list of average min positive delay of stops of a route " + routeId
                    + ":"  + e.getMessage());
            e.printStackTrace();
            results = new ArrayList<>();
        }

        return results;
    }


    /**
     * Get overall reliability
     * @return overall reliability or -1 if error occur
     */
    public static double getReliability() {
        double result = -1;
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return result;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_ONTIME_COUNT_OVERALL);

            result = getReliabilityResult(psql);

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting overall reliability:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Get reliability for a route
     * @param routeId route id
     * @return overall reliability or -1 if error occur
     */
    public static double getReliability(long routeId) {
        double result = -1;
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return result;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_ONTIME_COUNT_FOR_ROUTE);
            psql.setLong(1, routeId);

            result = getReliabilityResult(psql);

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting reliability for a route:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Get reliability for a stop of a route
     * @param routeId route id
     * @param stopId stop id
     * @return overall reliability or -1 if error occur
     */
    public static double getReliability(long routeId, long stopId) {
        double result = -1;
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return result;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_ONTIME_COUNT_FOR_ROUTE_AND_STOP);
            psql.setLong(1, routeId);
            psql.setLong(2, stopId);

            result = getReliabilityResult(psql);

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting reliability for a stop of a route:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Get reliability for a trip of a stop of a route
     * @param routeId route id
     * @param stopId stop id
     * @param tripId trip id
     * @return overall reliability or -1 if error occur
     */
    public static double getReliability(long routeId, long stopId, long tripId) {
        double result = -1;
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return result;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_ONTIME_COUNT_FOR_ROUTE_AND_STOP_AND_TRIP);
            psql.setLong(1, routeId);
            psql.setLong(2, stopId);
            psql.setLong(3, tripId);

            result = getReliabilityResult(psql);

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting reliability for a trip of a stop of a route:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Retrieve a parse DB item to get reliability
     * @param psql statement
     * @return reliability or -1 if error occurred
     */
    private static double getReliabilityResult(PreparedStatement psql) {
        double result = -1;
        try {
            ResultSet rs = psql.executeQuery();

            while(rs.next()){
                double ontimes = rs.getLong("sum_ontimes");
                double estimations = rs.getLong("sum_estimations");

                result = ontimes / estimations;
            }
        } catch (Exception e) {
            System.err.println("Error a parse DB item to get reliability:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }




    /**
     * Get bus bunching probability of a stop of a route by day of week
     * @param routeId route id
     * @param stopId stop id
     * @return list of  average arrival time deviation or empty list if error occur
     */
    public static ArrayList<Double> getBusBunchingByDayOfWeek(long routeId, long stopId) {
        ArrayList<Long> params = new ArrayList<>();
        params.add(routeId);
        params.add(stopId);
        return getBusBunchingByDatePart(SELECT_BUS_BUNCHING_BY_DOW_OF_STOP_OF_ROUTE, params, Calendar.DAY_OF_WEEK);
    }


    /**
     * Get bus bunching probability of all routes by date part
     * @return bus bunching probability of all routes or empty list if error occur
     */
    private static ArrayList<Double> getBusBunchingByDatePart(String sqlStatement, ArrayList<Long> params, int datePartType) {
        ArrayList<Double> results = new ArrayList<>();
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return results;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(sqlStatement);
            if (params != null)
                for (int i = 0; i < params.size(); i++)
                    psql.setLong(i + 1, params.get(i));

            results = getBusBunchingByDatePart(psql, datePartType);

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting bus bunching probability of all routes by date part:"  + e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Get bus bunching probability by date part
     * @return bus bunching probability by date part or empty list if error occurred
     */
    private static ArrayList<Double> getBusBunchingByDatePart(PreparedStatement psql, int datePartType) {
        ArrayList<Double> results = new ArrayList<>();
        try {
            ResultSet rs = psql.executeQuery();

            TreeMap<Long, Double> rows = new TreeMap<>();
            while(rs.next()){
                long num_bunching = rs.getLong("num_bunching");
                long num_estimations = rs.getLong("num_estimations");
                rows.put(rs.getLong("d_part"), ((double) num_bunching / (double) num_estimations) * 100.0);
            }

            results = parseDataForDatePart(rows, datePartType);

        } catch (SQLException e) {
            System.err.println("Error selecting bus bunching probability by date part:"  + e.getMessage());
            e.printStackTrace();
            results = new ArrayList<>();
        }

        return results;
    }

    /**
     * Parse data in Map to ArrayList for different type of date part
     * @param records data
     * @param datePartType type of date part
     * @return values of Map as ArrayList
     */
    private static ArrayList<Double> parseDataForDatePart(TreeMap<Long, Double> records, int datePartType) {
        ArrayList<Double> result = new ArrayList<>();
        int maxValue = 0;
        switch (datePartType) {
            case Calendar.MONTH:
                maxValue = 12;
                break;
            case Calendar.DAY_OF_WEEK:
                maxValue = 6;
                break;
            case Calendar.HOUR_OF_DAY:
                maxValue = 23;
                break;
            default:
                System.err.println("Invalid date part type : " + datePartType);
        }

        for (int i = 0; i < maxValue + 1; i++) {
            result.add(0.0);
        }

        for (Long key : records.keySet()) {
            Double value = records.get(key);

            result.set(key.intValue() % (maxValue + 1), value);
        }

        if (datePartType == Calendar.MONTH)
            result.remove(0);

//        System.out.println("parseDataForDatePart: records = " + records.toString());
//        System.out.println("parseDataForDatePart: result = " + result.toString());

        return result;
    }
}
