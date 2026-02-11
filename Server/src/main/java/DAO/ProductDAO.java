package DAO;

import Entities.Product;
import Entities.Supplier;
import Entities.Category;
import Interfaces.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO implements DAO<Product> {
    private final Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Product product) {
        String query = "INSERT INTO Product (Name, Quantity, Price, SupplierID, CategoryID) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getName());
            statement.setInt(2, product.getQuantity());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getSupplier().getId());
            statement.setInt(5, product.getCategory().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Product product) {
        String query = "UPDATE Product SET Name = ?, Quantity = ?, Price = ?, SupplierID = ?, CategoryID = ? WHERE ProductID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getName());
            statement.setInt(2, product.getQuantity());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getSupplier().getId());
            statement.setInt(5, product.getCategory().getId());
            statement.setInt(6, product.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Product product) {
        String query = "DELETE FROM Product WHERE ProductID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, product.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Product findById(int id) {
        String query = """
                SELECT p.ProductID, p.Name, p.Quantity, p.Price, 
                       s.SupplierID, s.Name AS SupplierName, 
                       c.CategoryID, c.Name AS CategoryName
                FROM Product p
                JOIN Supplier s ON p.SupplierID = s.SupplierID
                JOIN Category c ON p.CategoryID = c.CategoryID
                WHERE p.ProductID = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Product product = new Product();
                    product.setId(resultSet.getInt("ProductID"));
                    product.setName(resultSet.getString("Name"));
                    product.setQuantity(resultSet.getInt("Quantity"));
                    product.setPrice(resultSet.getDouble("Price"));

                    Supplier supplier = new Supplier();
                    supplier.setId(resultSet.getInt("SupplierID"));
                    supplier.setName(resultSet.getString("SupplierName"));
                    product.setSupplier(supplier);

                    Category category = new Category();
                    category.setId(resultSet.getInt("CategoryID"));
                    category.setName(resultSet.getString("CategoryName"));
                    product.setCategory(category);

                    return product;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String query = """
                SELECT p.ProductID, p.Name, p.Quantity, p.Price, 
                       s.SupplierID, s.Name AS SupplierName, 
                       c.CategoryID, c.Name AS CategoryName
                FROM Product p
                JOIN Supplier s ON p.SupplierID = s.SupplierID
                JOIN Category c ON p.CategoryID = c.CategoryID
                ORDER BY p.ProductID ASC
                """;

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("ProductID"));
                product.setName(resultSet.getString("Name"));
                product.setQuantity(resultSet.getInt("Quantity"));
                product.setPrice(resultSet.getDouble("Price"));

                Supplier supplier = new Supplier();
                supplier.setId(resultSet.getInt("SupplierID"));
                supplier.setName(resultSet.getString("SupplierName"));
                product.setSupplier(supplier);

                Category category = new Category();
                category.setId(resultSet.getInt("CategoryID"));
                category.setName(resultSet.getString("CategoryName"));
                product.setCategory(category);

                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public void updateQuantity(int productId, int quantity) throws SQLException {
        String query = """
            UPDATE Product 
            SET Quantity = Quantity - ? 
            WHERE ProductID = ? AND Quantity >= ?;
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Недостаточный запас для ProductID: " + productId);
            }
        }
    }
}
