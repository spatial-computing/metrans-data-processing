package edu.usc.imsc.metrans.connection;

import edu.usc.imsc.metrans.timedata.DelayTimeRecord;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class OracleIO {

    public static void writeDatabase(Route route, ArrayList<DelayTimeRecord> estimatedArrivalTimeResult) {

        Connection con = null;
        PreparedStatement pre = null;
        ResultSet result = null;

        String driver = "oracle.jdbc.OracleDriver";
        String url = "jdbc:oracle:thin:@gd.usc.edu:1521:ADMS";
        String username = "gtfs";
        String password = "gtfs1127";

        try {

            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            if (con != null) {
                System.out.println("Connected with ADMS: " + con);
            }

//            // Test for selection
//            String sql = "select * from gtfs.agency";
//            pre = con.prepareStatement(sql);
//            result = pre.executeQuery(sql);
//            while (result.next()) {
//                System.out.println(result.getString(1));
//            }
            for (int i = 0; i < estimatedArrivalTimeResult.size(); i++){

                AgencyAndId stopId = estimatedArrivalTimeResult.get(i).getStopTime().getStop().getId();
                AgencyAndId tripId = estimatedArrivalTimeResult.get(i).getStopTime().getTrip().getId();
                Integer busId = estimatedArrivalTimeResult.get(i).getBusId();
                Integer scheduleTime = estimatedArrivalTimeResult.get(i).getStopTime().getArrivalTime();
                ZonedDateTime time = estimatedArrivalTimeResult.get(i).getEstimatedTime();
                Double delay = estimatedArrivalTimeResult.get(i).getDelayTime();

                PreparedStatement psql;

                psql = con.prepareStatement("insert into estimatedArrivalTime "
                        + "values(?, ?, ?, ?, ?, ?, ?)");

//                psql.setInt(1, i);
                psql.setString(1, route.getId().toString());
                psql.setString(2, stopId.toString());
                psql.setString(3, tripId.toString());
                psql.setInt(4, busId);
                psql.setInt(5, scheduleTime);
                psql.setTimestamp(6, Timestamp.valueOf(time.toLocalDateTime()));
                psql.setDouble(7, delay);

                System.out.println(time);
                psql.executeUpdate();

            }


        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (result != null)
                    result.close();
                if (pre != null)
                    pre.close();
                if (con != null && !con.isClosed()) {
                    con.close();
                }
                System.out.println("Database connection closed.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
