package main.Models.Entities;

import java.io.Serializable;
import java.util.List;

public class OrderTable implements Serializable {
    private int orderId;
    private int quantity;
    private double totalAmount;
    private Address address;
    private User user;
    private List<OrderProduct> orderProducts;
    private OrderDetails orderDetails;


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails; // Метод для установки деталей заказа
    }

    @Override
    public String toString() {
        return "OrderTable{" +
                "orderId=" + orderId +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                ", address=" + address +
                ", user=" + user +
                ", orderDetails=" + orderDetails +
                '}';
    }
}

