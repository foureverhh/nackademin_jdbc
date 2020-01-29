package App;

import Util.ConnectToMysqlDatabase;
import model.Customer;
import model.CustomerDaoImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Shop {
    private static int uid;
    static Map<Integer,Customer> customerList;
    static Connection conn = null;
    static PreparedStatement ps = null;
    static CallableStatement cs = null;
    static ResultSet rs = null;
    static Customer customer;

    static {
        conn = ConnectToMysqlDatabase.getConnection();
        customerList = new CustomerDaoImpl().getAll();
    }
    public static void main(String[] args) {
        /*login*/
        login();
        /*Show storage*/
        showProductsInStore();
        /*show customers order*/
        showAllOrders();





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
             customer = new Customer(name, password);
            if (customerList.containsValue(customer)) {
                System.out.println("Welcome " + name);
                for (Map.Entry<Integer,Customer> entry : customerList.entrySet()) {
                    if(entry.getValue().equals(customer))
                        uid = entry.getKey();
                }
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

    /*static void customerPlaceOrder(){
        try {
            cs = conn.prepareCall("{call AddTOCart(?,?,?)}");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }*/

    static void showAllOrders(){
        String sql = "SELECT DATE_FORMAT (Orders.Order_date,'%Y%m%d'), "+
                "shoes.color,shoes.size,shoes.brand,shoes.price,categories.category,OrderDetails.Shoe_quantity "+
                "FROM Customers " +
                "JOIN Orders ON Customers.Customer_Id = Orders.Customer " +
                "JOIN OrderDetails USING (Order_id) " +
                "JOIN Shoes USING (Shoe_id) " +
                "JOIN ShoeCategoryDetails USING (Shoe_id) " +
                "JOIN Categories USING (Category_id) " +
                "Where Customers.Customer_Id = " + uid + ";";
        try {
            rs = ConnectToMysqlDatabase.query(sql,new Object[]{});
            /*String name = null;*/
            List<String> orders = new ArrayList<>();
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
           /*     String firstName = rs.getString(1);
                String lastName = rs.getString(2);*/
                /*name = firstName + " " + lastName;*/
                String date = rs.getString(1);
                sb.append(date).append(" ");
                String color = rs.getString(2);
                sb.append(color).append(" ");
                String size = rs.getString(3);
                sb.append(size).append(" ");
                String brand = rs.getString(4);
                sb.append(brand).append(" ");
                int price = rs.getInt(5);
                sb.append(price).append(" ");
                String category = rs.getString(6);
                sb.append(category).append(" ");
                int quantity = rs.getInt(7);
                sb.append(quantity);
                orders.add(sb.toString());
            }
            System.out.println("Dear " + customer.getName() + ", you have ordered: " );
            for(String order : orders){
                System.out.println(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
