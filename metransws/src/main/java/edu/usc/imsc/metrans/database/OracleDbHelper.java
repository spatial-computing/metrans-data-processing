package edu.usc.imsc.metrans.database;


import oracle.jdbc.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class OracleDbHelper {
    private static String USERNAME = "gtfs";
    private static final String PASSWORD = "gtfs1127";
    private static final String URL = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=gd.usc.edu)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=adms)))";

    /**
     * Utility method: creates a new JDBC connection to the database.
     */
    static OracleConnection connect()
    {
        OracleConnection conn = null;
        try{
            OracleDriver dr = new OracleDriver();
            Properties prop = new Properties();
            prop.setProperty("user",USERNAME);
            prop.setProperty("password",PASSWORD);
            conn = (OracleConnection)dr.connect(URL,prop);
            conn.setAutoCommit(false);
        } catch( SQLException ex ){ex.printStackTrace(); }
        return conn;
    }

    public static int getEstimateCount() {
        OracleConnection conn = connect();
        int count = -1;
        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                String query = "SELECT COUNT(*) FROM ESTIMATED_NEW";
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    count = rs.getInt(1);
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return count;
    }
}
