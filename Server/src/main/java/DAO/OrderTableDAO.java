package DAO;

import Entities.Address;
import Entities.OrderTable;
import Entities.User;
import Interfaces.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderTableDAO implements DAO<OrderTable> {
    private final Connection connection;

    public OrderTableDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(OrderTable orderTable) {
        String query = "INSERT INTO OrderTable (Quantity, TotalAmount, AddressID, UserID) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, orderTable.getQuantity());
            stmt.setDouble(2, orderTable.getTotalAmount());
            stmt.setInt(3, orderTable.getAddress().getId());
            stmt.setInt(4, orderTable.getUser().getId());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                orderTable.setOrderId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(OrderTable orderTable) {
        String query = "UPDATE OrderTable SET Quantity = ?, TotalAmount = ?, AddressID = ?, UserID = ? WHERE OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderTable.getQuantity());
            stmt.setDouble(2, orderTable.getTotalAmount());
            stmt.setInt(3, orderTable.getAddress().getId());
            stmt.setInt(4, orderTable.getUser().getId());
            stmt.setInt(5, orderTable.getOrderId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(OrderTable orderTable) {
        String query = "DELETE FROM OrderTable WHERE OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderTable.getOrderId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public OrderTable findById(int id) {
        String query = """
                SELECT ot.*, a.Country, a.City, a.Street, u.UserID, u.Login, u.Role
                FROM OrderTable ot
                JOIN Address a ON ot.AddressID = a.AddressID
                JOIN User u ON ot.UserID = u.UserID
                WHERE ot.OrderID = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrderTable(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<OrderTable> findAll() {
        List<OrderTable> orders = new ArrayList<>();
        String query = """
                SELECT ot.*, a.Country, a.City, a.Street, u.UserID, u.Login, u.Role
                FROM OrderTable ot
                JOIN Address a ON ot.AddressID = a.AddressID
                JOIN User u ON ot.UserID = u.UserID
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(mapResultSetToOrderTable(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    private OrderTable mapResultSetToOrderTable(ResultSet rs) throws SQLException {
        OrderTable order = new OrderTable();

        // Устанавливаем данные из OrderTable
        order.setOrderId(rs.getInt("OrderID"));
        order.setQuantity(rs.getInt("Quantity"));
        order.setTotalAmount(rs.getDouble("TotalAmount"));

        // Создаём и устанавливаем Address
        Address address = new Address();
        address.setId(rs.getInt("AddressID"));
        address.setCountry(rs.getString("Country"));
        address.setCity(rs.getString("City"));
        address.setStreet(rs.getString("Street"));
        order.setAddress(address);

        // Создаём и устанавливаем User
        User user = new User();
        user.setId(rs.getInt("UserID"));
        user.setLogin(rs.getString("Login"));
        user.setRole(rs.getString("Role"));
        order.setUser(user);

        return order;
    }

    public double getTotalOrderAmountByUserId(int userId) {
        String query = "SELECT COALESCE(SUM(TotalAmount), 0) AS TotalAmount FROM OrderTable WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TotalAmount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

}
