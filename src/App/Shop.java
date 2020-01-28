package App;

import Util.ConnectToMysqlDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Shop {
    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConnectToMysqlDatabase.setUpConnection();
            ps = conn.prepareStatement("SELECT * FROM Customers where customer_id = ?");
            ps.setObject(1,1);
            rs = ps.executeQuery();
            while(rs.next()){
                System.out.println(rs.getInt(1)+"--"+rs.getString(2)+"--"+rs.getString(3)
                        +"--"+rs.getObject(4)+"--"+rs.getInt(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectToMysqlDatabase.close(rs,ps,conn);
        }
    }
}
