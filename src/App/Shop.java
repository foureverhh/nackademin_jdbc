package App;

import Util.ConnectToMysqlDatabase;
import model.Customer;
import model.CustomerDaoImpl;
import model.Shoe;
import model.ShoeDaoImpl;

import java.sql.*;
import java.util.*;

public class Shop {
    private static int uid;
    private static int shoeId;
    private static Map<Integer,Customer> customerMap;
    private static Connection conn;
    private static CallableStatement cs;
    private static ResultSet rs = null;
    private static Customer customer;
    private static Scanner scanner;
    private static Map<Integer,Shoe> shoesInStore;

    static {
        conn = ConnectToMysqlDatabase.getConnection();
        customerMap = new CustomerDaoImpl().getAll();
        shoesInStore = new ShoeDaoImpl().getAll();
        scanner = new Scanner(System.in);
    }
    public static void main(String[] args) {
        /*User authentication*/
        login();
        /*Show current storage*/
        showProductsInStore();

        /*AddToCart*/
        addToCart();

        /*Check whether continue*/
        continueShopping();
    }

    /*authentication*/
    static void login(){
        while(true) {
            String name = null;
            int password = 0;
            System.out.println("Input your name please (Macus Smith " +
                    "/ Jim King " +
                    "/ Kim Ericsson " +
                    "/ John Ken" +
                    "/ Mike Fisherman)");
            String temp = null;
            if ((temp = scanner.nextLine()) != null) {
                name = temp.trim();
            }
            System.out.println("Input your password please( 123 / 345 / 456 / 678 / 890)");
            while (true) {
                try {
                    if ((temp = scanner.nextLine()) != null) {
                        password = Integer.parseInt(temp.trim());
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("please input a number for password");
                }
            }
             customer = new Customer(name, password);
            if (customerMap.containsValue(customer)) {
                System.out.println("Welcome " + name);
                for (Map.Entry<Integer,Customer> entry : customerMap.entrySet()) {
                    if(entry.getValue().equals(customer))
                        uid = entry.getKey();
                }
                break;
            } else {
                System.out.println("name or password is wrong try again please");
            }
        }
    }

    /*show storage*/
    static void showProductsInStore(){
        try {
            String sql = "SELECT shoes.color,shoes.size,shoes.brand,shoes.price,shoes.storage,Categories.category From shoes " +
                         "JOIN  ShoeCategoryDetails USING (shoe_id)"+
                         "JOIN  Categories USING (category_id);";
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

    static String verifyInput(){
        while(true) {
            try {
                String input = scanner.nextLine();
                if (input == null || input.isEmpty()) {
                    System.out.println("You should input something according to prompt!");
                }else {
                    return input;
                }
            }catch (Exception e){
                System.out.println("Input type is wrong. You should input something according to prompt!");
            }
        }
    }
    /*Get requires from customer*/
    static Shoe getRequireShoeFromCustomer(){
        System.out.println("\nChoose the color");
        String color = verifyInput();
        System.out.println("Choose the size");
        String size = verifyInput();
        System.out.println("Choose the brand");
        String brand = verifyInput();
        System.out.println("Choose the price");
        int price = 0;
        while (true) {
            try {
                price = Integer.parseInt(verifyInput());
                break;
            } catch (Exception e) {
                System.out.println("Only integer is accepted!");
            }
        }
        return new Shoe(color,size,brand,price);
    }

    /*Add new order*/
    static void addToCart(){
        Shoe customerChoice = getRequireShoeFromCustomer();
        for(Map.Entry<Integer,Shoe> entry : shoesInStore.entrySet()){
            if(entry.getValue().equals(customerChoice)){
                shoeId = entry.getKey();
                customerChoice = entry.getValue();
                break;
            }
        }
        if(customerChoice.getStorage() < 1 || shoeId == 0){
            System.out.println("Your choice is unavailable");
        }else {
            try {
                cs = conn.prepareCall("{call AddTOCart(?,?,?)}");
                cs.setInt(1, uid);
                cs.setObject(2, null);
                cs.setInt(3, shoeId);
                cs.executeQuery();
                System.out.println("Thanks for ordering a pair of" + customerChoice);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /*Show all */
    static void showAllOrders(){
        String sql = "SELECT DATE_FORMAT (Orders.Order_date,'%Y%m%d'), "+
                "shoes.color,shoes.size,shoes.brand,shoes.price,OrderDetails.Shoe_quantity "+
                "FROM Customers " +
                "JOIN Orders ON Customers.Customer_Id = Orders.Customer " +
                "JOIN OrderDetails USING (Order_id) " +
                "JOIN Shoes USING (Shoe_id) " +
                "Where Customers.Customer_Id = " + uid + ";";
        try {
            rs = ConnectToMysqlDatabase.query(sql,new Object[]{});
            System.out.println("\nDear " + customer.getName() + ", you have ordered: " );
            while (rs.next()) {
                String date = rs.getString(1);
                String color = rs.getString(2);
                String size = rs.getString(3);
                String brand = rs.getString(4);
                int price = rs.getInt(5);
                int quantity = rs.getInt(6);
                System.out.println(date + " " + new Shoe(color,size,brand,price) + " " + quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void shutdown(){
        ConnectToMysqlDatabase.close(rs,cs,conn);
        System.exit(0);
    }

    static void continueShopping(){
        System.out.println("\nDo you want to buy more? (Y/N)");
        boolean buyMore = true;
        while(buyMore) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("y")) {
                addToCart();
            }
            else if (input.equalsIgnoreCase("n")) {
                /*show customers order*/
                showAllOrders();
                buyMore = false;
                shutdown();
            }
            else{
                System.out.println("Please input Y or N");
            }
        }
    }

}
