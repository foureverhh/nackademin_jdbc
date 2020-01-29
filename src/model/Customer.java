package model;

import java.util.Objects;

public class Customer {
    private int id;
    private String name;
    private int password;

    public Customer() {
    }

    public Customer(String name, int password) {
        this.name = name;
        this.password = password;
    }

    public Customer(int id, String name, int password) {
        this(name,password);
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " " +password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return password == customer.password &&
                Objects.equals(name, customer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password);
    }


}
