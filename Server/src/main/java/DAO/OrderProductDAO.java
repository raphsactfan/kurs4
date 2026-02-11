package DAO;

import Entities.OrderProduct;
import Entities.Product;
import Entities.Supplier;
import Entities.Category;
import Interfaces.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderProductDAO implements DAO<OrderProduct> {
    private final Connection connection;

    public OrderProductDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(OrderProduct orderProduct) {
        String query = "INSERT INTO OrderProduct (OrderID, ProductID) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderProduct.getOrderId());
            stmt.setInt(2, orderProduct.getProduct().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(OrderProduct obj) {
        throw new UnsupportedOperationException("Update not supported");
    }

    @Override
    public void delete(OrderProduct orderProduct) {
        String query = "DELETE FROM OrderProduct WHERE OrderID = ? AND ProductID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderProduct.getOrderId());
            stmt.setInt(2, orderProduct.getProduct().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public OrderProduct findById(int id) {
        throw new UnsupportedOperationException("FindById not supported");
    }

    @Override
    public List<OrderProduct> findAll() {
        List<OrderProduct> orderProducts = new ArrayList<>();
        String query = """
                SELECT op.OrderID, p.ProductID, p.Name, p.Quantity, p.Price, 
                       s.SupplierID, s.Name AS SupplierName, 
                       c.CategoryID, c.Name AS CategoryName
                FROM OrderProduct op
                JOIN Product p ON op.ProductID = p.ProductID
                JOIN Supplier s ON p.SupplierID = s.SupplierID
                JOIN Category c ON p.CategoryID = c.CategoryID
                """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setOrderId(rs.getInt("OrderID"));

                Product product = new Product();
                product.setId(rs.getInt("ProductID"));
                product.setName(rs.getString("Name"));
                product.setQuantity(rs.getInt("Quantity"));
                product.setPrice(rs.getDouble("Price"));

                Supplier supplier = new Supplier();
                supplier.setId(rs.getInt("SupplierID"));
                supplier.setName(rs.getString("SupplierName"));
                product.setSupplier(supplier);

                Category category = new Category();
                category.setId(rs.getInt("CategoryID"));
                category.setName(rs.getString("CategoryName"));
                product.setCategory(category);

                orderProduct.setProduct(product);
                orderProducts.add(orderProduct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderProducts;
    }
}
