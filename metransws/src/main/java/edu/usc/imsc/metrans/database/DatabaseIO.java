package edu.usc.imsc.metrans.database;


import edu.usc.imsc.metrans.ws.storage.DbItemInfo;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseIO {
    private static final String SELECT_AVG_DEVIATION_ALL_ROUTES =
            " SELECT SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation_all_routes " +
                    " FROM etd_avg_deviation_month_mv ";

    private static final String SELECT_AVG_DEVIATION_PER_ROUTE =
            "SELECT route_id, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_month_mv " +
                    " GROUP BY route_id ";

    private static final String SELECT_AVG_DEVIATION_OF_STOPS_OF_ROUTE =
            "SELECT stop_id, SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation " +
                    " FROM etd_avg_deviation_month_mv " +
                    " WHERE route_id = ?" +
                    " GROUP BY stop_id";

    private static final String SELECT_AVG_MIN_POS_DELAY_PER_ROUTE =
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
     * Get average arrival time deviation of all routes
     * @return average arrival time deviation of all routes or {@code null} if error occur
     */
    public static DbItemInfo getAvgDeviationAllRoutes() {
        DbItemInfo result = null;
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return result;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_AVG_DEVIATION_ALL_ROUTES);

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
            psql = connection.prepareStatement(SELECT_AVG_DEVIATION_PER_ROUTE);

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
     * @return list of average arrival time deviation of all routes or empty list if error occur
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
            psql = connection.prepareStatement(SELECT_AVG_MIN_POS_DELAY_PER_ROUTE);

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
     * Get reliability for route
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
            System.err.println("Error selecting overall reliability:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Get reliability for route
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
            System.err.println("Error selecting overall reliability:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

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
            System.err.println("Error selecting overall reliability:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
