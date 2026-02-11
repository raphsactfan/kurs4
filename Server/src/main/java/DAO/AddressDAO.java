package DAO;

import Entities.Address;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {
    private final Connection connection;

    public AddressDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Address> getAllAddresses() throws Exception {
        List<Address> addresses = new ArrayList<>();
        String query = "SELECT * FROM Address";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Address address = new Address();
                address.setId(resultSet.getInt("AddressID"));
                address.setCountry(resultSet.getString("Country"));
                address.setCity(resultSet.getString("City"));
                address.setStreet(resultSet.getString("Street"));
                addresses.add(address);
            }
        }

        return addresses;
    }

    public void save(Address address) throws Exception {
        String query = "INSERT INTO Address (Country, City, Street) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, address.getCountry());
            stmt.setString(2, address.getCity());
            stmt.setString(3, address.getStreet());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                address.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Failed to generate AddressID");
            }
        }
    }
}
