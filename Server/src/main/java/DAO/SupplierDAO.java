package DAO;

import Entities.Address;
import Entities.Supplier;
import Interfaces.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierDAO implements DAO<Supplier> {
    private final Connection connection;

    public SupplierDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Supplier supplier) {
        String addressQuery = "INSERT INTO Address (Country, City, Street) VALUES (?, ?, ?)";
        String supplierQuery = "INSERT INTO Supplier (Name, Representative, Phone, AddressID) VALUES (?, ?, ?, ?)";

        try (PreparedStatement addressStmt = connection.prepareStatement(addressQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement supplierStmt = connection.prepareStatement(supplierQuery)) {

            // Вставка адреса
            addressStmt.setString(1, supplier.getAddress().getCountry());
            addressStmt.setString(2, supplier.getAddress().getCity());
            addressStmt.setString(3, supplier.getAddress().getStreet());
            addressStmt.executeUpdate();

            ResultSet addressKeys = addressStmt.getGeneratedKeys();
            if (addressKeys.next()) {
                int addressId = addressKeys.getInt(1);
                supplier.getAddress().setId(addressId);

                // Вставка поставщика с привязкой к адресу
                supplierStmt.setString(1, supplier.getName());
                supplierStmt.setString(2, supplier.getRepresentative());
                supplierStmt.setString(3, supplier.getPhone());
                supplierStmt.setInt(4, addressId);
                supplierStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Supplier supplier) {
        String addressQuery = "UPDATE Address SET Country = ?, City = ?, Street = ? WHERE AddressID = ?";
        String supplierQuery = "UPDATE Supplier SET Name = ?, Representative = ?, Phone = ? WHERE SupplierID = ?";

        try (PreparedStatement addressStmt = connection.prepareStatement(addressQuery);
             PreparedStatement supplierStmt = connection.prepareStatement(supplierQuery)) {

            // Обновление адреса
            addressStmt.setString(1, supplier.getAddress().getCountry());
            addressStmt.setString(2, supplier.getAddress().getCity());
            addressStmt.setString(3, supplier.getAddress().getStreet());
            addressStmt.setInt(4, supplier.getAddress().getId());
            addressStmt.executeUpdate();

            // Обновление данных поставщика
            supplierStmt.setString(1, supplier.getName());
            supplierStmt.setString(2, supplier.getRepresentative());
            supplierStmt.setString(3, supplier.getPhone());
            supplierStmt.setInt(4, supplier.getId());
            supplierStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Supplier supplier) {
        String supplierQuery = "DELETE FROM Supplier WHERE SupplierID = ?";
        String addressQuery = "DELETE FROM Address WHERE AddressID = ?";

        try (PreparedStatement supplierStmt = connection.prepareStatement(supplierQuery);
             PreparedStatement addressStmt = connection.prepareStatement(addressQuery)) {

            // Удаление поставщика
            supplierStmt.setInt(1, supplier.getId());
            supplierStmt.executeUpdate();

            // Удаление адреса
            addressStmt.setInt(1, supplier.getAddress().getId());
            addressStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Supplier findById(int id) {
        String query = """
                SELECT s.SupplierID, s.Name, s.Representative, s.Phone,
                       a.AddressID, a.Country, a.City, a.Street
                FROM Supplier s
                INNER JOIN Address a ON s.AddressID = a.AddressID
                WHERE s.SupplierID = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Supplier supplier = new Supplier();
                    supplier.setId(resultSet.getInt("SupplierID"));
                    supplier.setName(resultSet.getString("Name"));
                    supplier.setRepresentative(resultSet.getString("Representative"));
                    supplier.setPhone(resultSet.getString("Phone"));

                    Address address = new Address();
                    address.setId(resultSet.getInt("AddressID"));
                    address.setCountry(resultSet.getString("Country"));
                    address.setCity(resultSet.getString("City"));
                    address.setStreet(resultSet.getString("Street"));

                    supplier.setAddress(address);
                    return supplier;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Supplier> findAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String query = """
                SELECT s.SupplierID, s.Name, s.Representative, s.Phone,
                       a.AddressID, a.Country, a.City, a.Street
                FROM Supplier s
                INNER JOIN Address a ON s.AddressID = a.AddressID
                """;

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Supplier supplier = new Supplier();
                supplier.setId(resultSet.getInt("SupplierID"));
                supplier.setName(resultSet.getString("Name"));
                supplier.setRepresentative(resultSet.getString("Representative"));
                supplier.setPhone(resultSet.getString("Phone"));

                Address address = new Address();
                address.setId(resultSet.getInt("AddressID"));
                address.setCountry(resultSet.getString("Country"));
                address.setCity(resultSet.getString("City"));
                address.setStreet(resultSet.getString("Street"));

                supplier.setAddress(address);
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public Map<String, Integer> fetchSupplierProductCounts() {
        Map<String, Integer> supplierProductCounts = new HashMap<>();
        String query = "SELECT s.Name AS SupplierName, COUNT(p.ProductID) AS ProductCount " +
                "FROM Supplier s " +
                "LEFT JOIN Product p ON s.SupplierID = p.SupplierID " +
                "GROUP BY s.Name";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                supplierProductCounts.put(rs.getString("SupplierName"), rs.getInt("ProductCount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return supplierProductCounts;
    }

}
