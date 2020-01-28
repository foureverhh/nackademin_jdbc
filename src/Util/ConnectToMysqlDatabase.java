package Util;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class ConnectToMysqlDatabase {
    static Properties pros = new Properties();
    static {
        try {
            pros.load(new FileReader("/Users/foureverhh/nackademin_jdbc/res/login.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection setUpConnection()  {
        try {
            Class.forName(pros.getProperty("mysqlDriver"));
            return DriverManager.getConnection (
                    pros.getProperty("mysqlURL"),
                    pros.getProperty("name"),
                    pros.getProperty("password"));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void close(ResultSet rs, PreparedStatement ps, Connection conn){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(ps != null){
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn != null){
            try{
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
