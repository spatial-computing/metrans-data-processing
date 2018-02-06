package edu.usc.imsc.metrans.connection;

import edu.usc.imsc.metrans.timedata.DelayTimeRawRecord;
import edu.usc.imsc.metrans.timedata.DelayTimeRecord;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.function.DoubleBinaryOperator;

public class DatabaseIO {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseIO.class);

    private static final String INSERT_STMT = "INSERT INTO metrans.estimated_arrival_time" +
            " (route_id, stop_id, trip_id, bus_id, estimated_time, delay_time, schedule_time) " +
            " VALUES(?, ?, ?, ?, ?, ?, ?)" +
            " ON CONFLICT DO NOTHING";

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

    public static boolean insertBatch(ArrayList<DelayTimeRawRecord> records) {
        Connection connection = getConnection();
        if (connection == null) {
            logger.error("No database connection");
            return false;
        }
        PreparedStatement psql;

        int batchSize = 1000;

        try {
            psql = connection.prepareStatement(INSERT_STMT);

            for (int i = 0; i < records.size(); i++) {
                DelayTimeRawRecord record = records.get(i);
                psql.setString(1, record.getRouteId());
                psql.setString(2, record.getStopId());
                psql.setString(3, record.getTripId());
                psql.setInt(4, record.getBusId());
                psql.setTimestamp(5, Timestamp.from(Instant.ofEpochSecond(record.getEstimatedTime())));
                psql.setDouble(6, record.getDelayTime());
                psql.setInt(7, record.getScheduleTime());

                psql.addBatch();

                if ( (i + 1) % batchSize == 0) {
                    psql.executeBatch();
                    logger.info("Inserted " + (i + 1) + " records");
                }
            }

            psql.executeBatch();
            logger.info("Inserted " + records.size() + " records");

            if (connection != null) {
                connection.close();
            }

            return true;
        } catch (SQLException e) {
            logger.error("Error inserting into database", e);
        }

        return false;
    }

    public static void writeDatabase(Route route, ArrayList<DelayTimeRecord> estimatedArrivalTimeResult) {

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

                psql = con.prepareStatement(INSERT_STMT);

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
