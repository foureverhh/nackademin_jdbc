package App;

import Util.ConnectToMysqlDatabase;
import model.Customer;
import model.CustomerDaoImpl;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Shop {
    static Map<Integer,Customer> customerList;
    static Connection conn = null;
    static PreparedStatement ps = null;
    static CallableStatement cs = null;
    static ResultSet rs = null;
    static {
        conn = ConnectToMysqlDatabase.getConnection();
        customerList = new CustomerDaoImpl().getAll();
    }
    public static void main(String[] args) {
        /*login*/
        //login();
        /*Show storage*/
        showProductsInStore();





        /*try {
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
        }*/
    }

    static void login(){
      /*  for(Map.Entry<Integer,Customer> entry : customerList.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue());
        }*/
        while(true) {
            String name = null;
            int password = 0;
            System.out.println("Input your name please");
            Scanner scanner = new Scanner(System.in);
            String temp = null;
            if ((temp = scanner.nextLine()) != null) {
                name = temp.trim();
            }
            System.out.println("Input your password please");
            while (true) {
                try {
                    if ((temp = scanner.nextLine()) != null) {
                        password = Integer.parseInt(temp.trim());
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("please input a number");
                }
            }
            Customer customer = new Customer(name, password);
            if (customerList.containsValue(customer)) {
                System.out.println("Welcome " + name);
                break;
            } else {
                System.out.println("name or password is wrong try again please");
            }
        }
    }

    static void showProductsInStore(){
        try {
            String sql = "SELECT shoes.color,shoes.size,shoes.brand,shoes.price,shoes.storage,categories.category From shoes " +
                         "JOIN  ShoeCategoryDetails USING (shoe_id)"+
                         "JOIN  Categories USING (category_id)";
            rs = ConnectToMysqlDatabase.query(sql, new Object[]{});
            System.out.println("\nShoes in store:");
            System.out.printf("%-10s%-10s%-10s%-10s%-10s%-10s\n","Color","Size","Brand","Price","Storage","Category");
            while (rs.next()){
                System.out.printf("%-10s%-10s%-10s%-10d%-10d%-10s\n",
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt("Price"),
                        rs.getInt("Storage"),
                        rs.getString("category"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void customerPlaceOrder(){

    }

}
