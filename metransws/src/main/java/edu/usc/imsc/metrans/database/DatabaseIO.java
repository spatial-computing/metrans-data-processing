package edu.usc.imsc.metrans.database;


import java.sql.*;

public class DatabaseIO {
    private static final String SELECT_AVG_DEVIATION_ALL_ROUTES =
            " SELECT SUM((avg_deviation * num_estimations)) / SUM(num_estimations) as avg_deviation_all_routes " +
            " FROM etd_avg_deviation_month_mv ";

    private static final String SELECT_MIN_MAX_DATE =
            "SELECT MIN(date_estimated_time) AS min_date, MAX(date_estimated_time) AS max_date " +
            " FROM estimated_arrival_time";

    private static final String SELECT_ESTIMATED_DATA_POINTS = "SELECT reltuples::BIGINT AS estimated_data_points " +
            " FROM pg_class " +
            " WHERE relname='estimated_arrival_time'";

    private static final String SELECT_ONTIME_COUNT_OVERALL =
            "SELECT SUM(num_ontime_estimations)::BIGINT AS sum_ontimes, SUM(num_estimations)::BIGINT AS sum_estimations " +
            " FROM etd_ontime_count_mv";

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
     * @return average arrival time deviation of all routes or {@code NEGATIVE_INFINITY} if error occur
     */
    public static Double getAvgDeviationAllRoutes() {
        Double result = Double.NEGATIVE_INFINITY;
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
                result = rs.getDouble("avg_deviation_all_routes");
            }

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting average arrival time deviation of all routes:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
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
    public static double getReliabilityOverall() {
        double result = -1;
        Connection connection = getConnection();
        if (connection == null) {
            System.err.println("No database connection");
            return result;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_ONTIME_COUNT_OVERALL);

            ResultSet rs = psql.executeQuery();

            while(rs.next()){
                double ontimes = rs.getLong("sum_ontimes");
                double estimations = rs.getLong("sum_estimations");

                result = ontimes / estimations;
            }

            connection.close();

        } catch (SQLException e) {
            System.err.println("Error selecting overall reliability:"  + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
