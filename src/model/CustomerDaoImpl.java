package model;

import Util.ConnectToMysqlDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CustomerDaoImpl implements Dao {

    @Override
    public Map<Integer,Customer> getAll() {
        Object[] objects = {};
        Map<Integer,Customer> customers = new HashMap<>();
        ResultSet resultset = ConnectToMysqlDatabase
                .query("SELECT Customer_Id,First_name,Last_name,Pincode From Customers",
                        objects);
        try{
           while(resultset.next()) {
               int id = resultset.getInt(1);
               String firstname = resultset.getString(2);
               String lastename = resultset.getString(3);
               String name = firstname + " " + lastename;
               int password = resultset.getInt(4);

               Customer customer = new Customer(name,password);
               customers.put(id,customer);
           }
        } catch (SQLException e) {
            e.printStackTrace();
        }

       /* for(Map.Entry<Integer,Customer> entry : customers.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue());
        }*/
        return customers;
    }
}
