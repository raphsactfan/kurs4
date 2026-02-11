package main.Models.Entities;

import java.io.Serializable;

public class Supplier implements Serializable {
    private int id;
    private String name;
    private String representative;
    private String phone;
    private Address address;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", representative='" + representative + '\'' +
                ", phone='" + phone + '\'' +
                ", address=" + address +
                '}';
    }
}
