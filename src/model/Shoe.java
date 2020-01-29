package model;

import java.util.Objects;

public class Shoe {
    private String color;
    private String size;
    private String brand;
    private int price;
    private String category;
    private int storage;

    public Shoe(String color, String size, String brand, int price, String category) {
        this.color = color;
        this.size = size;
        this.brand = brand;
        this.price = price;
        this.category = category;
    }

    public Shoe(String color, String size, String brand, int price, String category, int storage) {
        this(color,size,brand,price,category);
        this.storage = storage;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public String getBrand() {
        return brand;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public int getStorage() {
        return storage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shoe shoe = (Shoe) o;
        return price == shoe.price &&
                Objects.equals(color, shoe.color) &&
                Objects.equals(size, shoe.size) &&
                Objects.equals(brand, shoe.brand) &&
                Objects.equals(category, shoe.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, size, brand, price, category);
    }
}
