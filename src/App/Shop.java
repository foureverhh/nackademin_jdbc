package App;

import Util.ConnectToMysqlDatabase;
import model.Customer;
import model.CustomerDaoImpl;
import model.Shoe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Shop {
    private static int uid;
    private static int shoeId;
    private static Map<Integer,Customer> customerList;
    private static Connection conn;
    private static PreparedStatement ps;
    private static CallableStatement cs;
    private static ResultSet rs = null;
    private static Customer customer;
    private static Scanner scanner;
    private static List<Shoe> shoesInStore = new ArrayList<>();


    static {
        conn = ConnectToMysqlDatabase.getConnection();
        customerList = new CustomerDaoImpl().getAll();
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
            System.out.println("Input your name please");
            /*scanner = new Scanner(System.in);*/
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
                    System.out.println("please input a number for password");
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

    /*show storage*/
    static void showProductsInStore(){
        try {
            String sql = "SELECT shoes.color,shoes.size,shoes.brand,shoes.price,shoes.storage From shoes " +
                         "JOIN  ShoeCategoryDetails USING (shoe_id)"+
                         "JOIN  Categories USING (category_id);";
            rs = ConnectToMysqlDatabase.query(sql, new Object[]{});
            System.out.println("\nShoes in store:");
            System.out.printf("%-10s%-10s%-10s%-10s%-10s%-10s\n","Color","Size","Brand","Price","Storage","Category");
            while (rs.next()){
                String color = rs.getString(1);
                String size = rs.getString(2);
                String brand = rs.getString(3);
                int price = rs.getInt("Price");
                int storage  = rs.getInt("Storage");
                String category = rs.getString("category");
                System.out.printf("%-10s%-10s%-10s%-10d%-10d%-10s\n",
                        color,
                        size,
                        brand,
                        price,
                        storage,
                        category);
                shoesInStore.add(new Shoe(color,size,brand,price,category,storage));
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
        System.out.println("Choose the color");
        String color = verifyInput();
        System.out.println("Choose the size");
        String size = verifyInput();;
        System.out.println("Choose the brand");
        String brand = verifyInput();
        System.out.println("Choose the price");
        int price = Integer.parseInt(verifyInput());
        System.out.println("Choose the category");
        String category = verifyInput();
        return new Shoe(color,size,brand,price,category);
    }

    /*Add new order*/
    static void addToCart(){
        Shoe customerChoice = getRequireShoeFromCustomer();
        for (int i = 0; i < shoesInStore.size(); i++) {
            if(customerChoice.equals(shoesInStore.get(i))){
                shoeId = i+1;
                System.out.println(shoeId);
                customerChoice = shoesInStore.get(i);
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /*Show all */
    static void showAllOrders(){
        String sql = "SELECT DATE_FORMAT (Orders.Order_date,'%Y%m%d'), "+
                "categories.category,shoes.color,shoes.size,shoes.brand,shoes.price,OrderDetails.Shoe_quantity "+
                "FROM Customers " +
                "JOIN Orders ON Customers.Customer_Id = Orders.Customer " +
                "JOIN OrderDetails USING (Order_id) " +
                "JOIN Shoes USING (Shoe_id) " +
                "JOIN ShoeCategoryDetails USING (Shoe_id) " +
                "JOIN Categories USING (Category_id) " +
                "Where Customers.Customer_Id = " + uid + ";";
        try {
            rs = ConnectToMysqlDatabase.query(sql,new Object[]{});
            List<String> orders = new ArrayList<>();
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                String category = rs.getString(1);
                sb.append(category).append(" ");
                String date = rs.getString(2);
                sb.append(date).append(" ");
                String color = rs.getString(3);
                sb.append(color).append(" ");
                String size = rs.getString(4);
                sb.append(size).append(" ");
                String brand = rs.getString(5);
                sb.append(brand).append(" ");
                int price = rs.getInt(6);
                sb.append(price).append(" ");
                int quantity = rs.getInt(7);
                sb.append(quantity);
                orders.add(sb.toString());
            }
            System.out.println("\nDear " + customer.getName() + ", you have ordered: " );
            for(String order : orders){
                System.out.println(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void shutdown(){
        ConnectToMysqlDatabase.close(rs,ps,cs,conn);
        System.exit(0);
    }

    static void continueShopping(){
        System.out.println("\nDo you want to buy more? (Y/N)");
        boolean buyMore = true;
        while(buyMore) {
            /*scanner = new Scanner(System.in);*/
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
