package edu.usc.imsc.metrans.connection;

import edu.usc.imsc.metrans.timedata.DelayTimeRecord;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.function.DoubleBinaryOperator;

public class DatabaseIO {

    public static void writeDatabase(Route route, ArrayList<DelayTimeRecord> estimatedArrivalTimeResult) {

        Connection con;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/METRANS";
        String user = "root";
        String password = "123456";
        try {

            Class.forName(driver);
            con = DriverManager.getConnection(url,user,password);
            if(!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");

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

            con.close();
        } catch(ClassNotFoundException e) {
            System.out.println("Sorry,can't find the Driver!");
            e.printStackTrace();
            } catch(SQLException e) {
                e.printStackTrace();
                }catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
        }finally{
            System.out.println("Successful Output");
        }
    }
}
