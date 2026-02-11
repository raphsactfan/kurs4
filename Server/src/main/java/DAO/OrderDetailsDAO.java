package DAO;

import Entities.OrderDetails;
import Interfaces.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsDAO implements DAO<OrderDetails> {
    private final Connection connection;

    public OrderDetailsDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(OrderDetails orderDetails) {
        String query = "INSERT INTO OrderDetails (OrderID, DateTime) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderDetails.getOrderId());
            stmt.setTimestamp(2, Timestamp.valueOf(orderDetails.getDateTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(OrderDetails orderDetails) {
        String query = "UPDATE OrderDetails SET DateTime = ? WHERE OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(orderDetails.getDateTime()));
            stmt.setInt(2, orderDetails.getOrderId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(OrderDetails orderDetails) {
        String query = "DELETE FROM OrderDetails WHERE OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderDetails.getOrderId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public OrderDetails findById(int id) {
        String query = "SELECT OrderID, DateTime FROM OrderDetails WHERE OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    OrderDetails orderDetails = new OrderDetails();
                    orderDetails.setOrderId(rs.getInt("OrderID"));
                    orderDetails.setDateTime(rs.getTimestamp("DateTime").toLocalDateTime());
                    return orderDetails;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<OrderDetails> findAll() {
        List<OrderDetails> orderDetailsList = new ArrayList<>();
        String query = "SELECT OrderID, DateTime FROM OrderDetails";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setOrderId(rs.getInt("OrderID"));
                orderDetails.setDateTime(rs.getTimestamp("DateTime").toLocalDateTime());
                orderDetailsList.add(orderDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetailsList;
    }

}
