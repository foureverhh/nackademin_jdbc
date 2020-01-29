package model;

import Util.ConnectToMysqlDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ShoeDaoImpl implements Dao{
    @Override
    public Map<Integer,Shoe> getAll() {
        Map<Integer,Shoe> shoes = new HashMap<>();
        ResultSet resultset = ConnectToMysqlDatabase
                .query("SELECT Shoe_Id,Color,Size,Brand,price, Storage From Shoes",
                        new Object[]{});
        try{
            while(resultset.next()) {
                int id = resultset.getInt(1);
                String color = resultset.getString(2);
                String size = resultset.getString(3);
                String brand = resultset.getString(4);
                int price = resultset.getInt(5);
                int storage = resultset.getInt(6);


                Shoe shoe = new Shoe(color,size,brand,price,storage);
                shoes.put(id,shoe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shoes;
    }
}
