package edu.usc.imsc.metrans.connection;

import edu.usc.imsc.metrans.gtfsutil.GtfsUtil;
import edu.usc.imsc.metrans.timedata.ArrivalTimeEstRawRecord;
import edu.usc.imsc.metrans.timedata.ArrivalTimeEstRecord;
import edu.usc.imsc.metrans.timedata.BusBunchingRecord;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;

public class DatabaseIO {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseIO.class);

    private static final int DEFAULT_BATCH_SIZE = 1024;

    private static final String INSERT_ESTIMATED_ARRIVAL_TIME_STMT = "INSERT INTO estimated_arrival_time" +
            " (route_id, stop_id, trip_id, estimated_time, delay_time, schedule_time, date_estimated_time) " +
            " VALUES(?, ?, ?, ?, ?, ?, DATE(?))" +
            " ON CONFLICT DO NOTHING";

    private static final String INSERT_ESTIMATED_DELAY_TIME_STMT = "INSERT INTO estimated_delay_time" +
            " (route_id, stop_id, trip_id, estimated_time, delay_time, schedule_time, date_estimated_time) " +
            " VALUES(?, ?, ?, ?, ?, ?, DATE(?))" +
            " ON CONFLICT DO NOTHING";

    private static final String INSERT_ESTIMATED_ARRIVAL_TIME_MIN_POS_STMT = "INSERT INTO estimated_arrival_time_min_pos" +
            " (route_id, stop_id, trip_id, estimated_time, delay_time, schedule_time, date_estimated_time) " +
            " VALUES(?, ?, ?, ?, ?, ?, DATE(?))" +
            " ON CONFLICT DO NOTHING";

    private static final String INSERT_BUS_BUNCHING_STMT = "INSERT INTO bus_bunching" +
            " (route_id, stop_id, trip_id, date_estimated_time, schedule_time, num_buses) " +
            " VALUES(?, ?, ?, DATE(?), ?, ?)" +
            " ON CONFLICT DO NOTHING";

    private static final String SELECT_BY_DATE_STMT = "SELECT * " +
            " FROM estimated_arrival_time " +
            " WHERE date_estimated_time = DATE(?)";

    private static Connection getConnection() {
        Connection con = null;
        String url = "jdbc:postgresql://dsicloud2.usc.edu:5432/metrans";
        String user = "metrans";
        String password = "Bg86526Us";
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            logger.error("Error creating connection", e);
        }

        return con;
    }

    public static boolean insertBatchEstimatedArrivalTime(ArrayList<ArrivalTimeEstRawRecord> records) {
        return insertBatchEstimatedTime(records, INSERT_ESTIMATED_ARRIVAL_TIME_STMT);
    }

    public static boolean insertBatchEstimatedDelayTime(ArrayList<ArrivalTimeEstRawRecord> records) {
        return insertBatchEstimatedTime(records, INSERT_ESTIMATED_DELAY_TIME_STMT);
    }

    public static boolean insertBatchTravelTime(ArrayList<ArrivalTimeEstRawRecord> records) {
        return insertBatchEstimatedTime(records, INSERT_ESTIMATED_ARRIVAL_TIME_MIN_POS_STMT);
    }

    private static boolean insertBatchEstimatedTime(ArrayList<ArrivalTimeEstRawRecord> records, String stmt) {
        Connection connection = getConnection();
        if (connection == null) {
            logger.error("No database connection");
            return false;
        }
        PreparedStatement psql = null;

        int batchSize = DEFAULT_BATCH_SIZE;

        boolean ok = false;

        try {
            connection.setAutoCommit(false);
            psql = connection.prepareStatement(stmt);

            for (int i = 0; i < records.size(); i++) {
                ArrivalTimeEstRawRecord record = records.get(i);
                psql.setLong(1, Long.valueOf(GtfsUtil.toShortRouteId(record.getRouteId())));
                psql.setLong(2, Long.valueOf(record.getStopId()));
                psql.setLong(3, Long.valueOf(record.getTripId()));
                psql.setTimestamp(4, Timestamp.from(Instant.ofEpochSecond(record.getEstimatedTime())));
                psql.setDouble(5, record.getDelayTime());
                psql.setInt(6, record.getScheduleTime());
                psql.setTimestamp(7, Timestamp.from(Instant.ofEpochSecond(record.getEstimatedTime())));

                psql.addBatch();

                if ( (i + 1) % batchSize == 0) {
                    psql.executeBatch();
//                    logger.info("Inserted " + (i + 1) + " records");
                }
            }

            psql.executeBatch();
            logger.info("Inserted " + records.size() + " records");

            connection.commit();
        } catch (SQLException e) {
            logger.error("Error inserting into database", e);
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            try {
                if (psql != null)
                    psql.close();
                if (connection != null)
                    connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


    public static boolean insertBatchBusBunching(ArrayList<BusBunchingRecord> records) {
        Connection connection = getConnection();
        if (connection == null) {
            logger.error("No database connection");
            return false;
        }
        PreparedStatement psql;

        int batchSize = DEFAULT_BATCH_SIZE;

        try {
            psql = connection.prepareStatement(INSERT_BUS_BUNCHING_STMT);

            for (int i = 0; i < records.size(); i++) {
                BusBunchingRecord record = records.get(i);
                psql.setLong(1, Long.valueOf(GtfsUtil.toShortRouteId(record.getRouteId())));
                psql.setLong(2, Long.valueOf(record.getStopId()));
                psql.setLong(3, Long.valueOf(record.getTripId()));
                psql.setTimestamp(4, Timestamp.from(Instant.ofEpochSecond(record.getEstimatedTime())));
                psql.setInt(5, record.getScheduleTime());
                psql.setInt(6, record.getNumBusBunching());

                psql.addBatch();

                if ( (i + 1) % batchSize == 0) {
                    psql.executeBatch();
//                    logger.info("Inserted " + (i + 1) + " records");
                }
            }

            psql.executeBatch();
            logger.info("Inserted " + records.size() + " records");

            connection.close();

            return true;
        } catch (SQLException e) {
            logger.error("Error inserting into database", e);
        }

        return false;
    }

    /**
     * Get estimated delay records for a specific day
     * @param timestamp timestamp of the day
     * @return list of estimated delay records for a specific day
     */
    public static ArrayList<ArrivalTimeEstRawRecord> getEstimatedDelayByDay(long timestamp) {
        ArrayList<ArrivalTimeEstRawRecord> results = new ArrayList<>();
        Connection connection = getConnection();
        if (connection == null) {
            logger.error("No database connection");
            return results;
        }
        PreparedStatement psql;

        try {
            psql = connection.prepareStatement(SELECT_BY_DATE_STMT);
            psql.setTimestamp(1, Timestamp.from(Instant.ofEpochSecond(timestamp)));

            ResultSet rs = psql.executeQuery();

            while(rs.next()){
                //Retrieve by column name
                ArrivalTimeEstRawRecord record = new ArrivalTimeEstRawRecord();

                record.setRouteId(String.valueOf(rs.getLong("route_id")));
                record.setStopId(String.valueOf(rs.getLong("stop_id")));
                record.setTripId(String.valueOf(rs.getLong("trip_id")));
                record.setEstimatedTime(rs.getTimestamp("estimated_time").getTime() / 1000);
                record.setDelayTime(rs.getDouble("delay_time"));
                record.setScheduleTime(rs.getInt("schedule_time"));
                record.setBusId(0);

                results.add(record);
            }
            rs.close();

            logger.info("Retrieved " + results.size() + " records");

            connection.close();

        } catch (SQLException e) {
            logger.error("Error inserting into database", e);
        }

        return results;
    }



    public static void writeDatabase(Route route, ArrayList<ArrivalTimeEstRecord> estimatedArrivalTimeResult) {

        Connection con = getConnection();
        try {

            if(!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            for (int i = 0; i < estimatedArrivalTimeResult.size(); i++){

                AgencyAndId stopId = estimatedArrivalTimeResult.get(i).getStopTime().getStop().getId();
                AgencyAndId tripId = estimatedArrivalTimeResult.get(i).getStopTime().getTrip().getId();
                Integer busId = estimatedArrivalTimeResult.get(i).getBusId();
                Integer scheduleTime = estimatedArrivalTimeResult.get(i).getStopTime().getArrivalTime();
                long estimatedArrivalTime = estimatedArrivalTimeResult.get(i).getEstimatedTime();
                Double delay = estimatedArrivalTimeResult.get(i).getDelayTime();

                PreparedStatement psql;

                psql = con.prepareStatement(INSERT_ESTIMATED_ARRIVAL_TIME_STMT);

//                psql.setInt(1, i);
                psql.setString(1, route.getId().toString());
                psql.setString(2, stopId.toString());
                psql.setString(3, tripId.toString());
                psql.setInt(4, busId);
                psql.setTimestamp(5, Timestamp.from(Instant.ofEpochSecond(estimatedArrivalTime)));
                psql.setDouble(6, delay);
                psql.setInt(7, scheduleTime);

//                System.out.println(time);
                psql.executeUpdate();

            }

            con.close();
        } catch(Exception e) {
            e.printStackTrace();
        }finally{
            System.out.println("Done Output");
        }
    }
}
