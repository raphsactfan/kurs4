package main.Models.Entities;

import java.io.Serializable;

public class OrderProduct implements Serializable {
    private int orderId;
    private Product product;


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "OrderProduct{" +
                "orderId=" + orderId +
                ", product=" + product +
                '}';
    }
}
