package main.Models.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OrderDetails implements Serializable {
    private int orderId;
    private LocalDateTime dateTime;


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "orderId=" + orderId +
                ", dateTime=" + dateTime +
                '}';
    }
}
