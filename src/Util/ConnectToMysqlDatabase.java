package Util;

import model.Customer;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Properties;

public class ConnectToMysqlDatabase {
    private static Properties pros = new Properties();
    private static Connection conn;
    private static PreparedStatement preparedStatement;

    static {
        try {
            pros.load(new FileReader("/Users/foureverhh/nackademin_jdbc/res/login.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection()  {
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

    public static ResultSet query(String sql, Object[] objects){
        try {
            conn = getConnection();
            preparedStatement = conn.prepareStatement(sql);
            for (int i = 0; i < objects.length ; i++) {
                preparedStatement.setObject(i+1,objects[i]);
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void close(ResultSet rs, CallableStatement cs,Connection conn){
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(cs != null){
            try {
                cs.close();
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
