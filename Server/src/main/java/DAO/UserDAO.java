package DAO;

import Entities.User;
import Interfaces.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements DAO<User> {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(User user) {
        String query = "INSERT INTO User (Login, Password, Role) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(User user) {
        String query = "UPDATE User SET Login = ?, Password = ?, Role = ? WHERE UserID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.setInt(4, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(User user) {
        String query = "DELETE FROM User WHERE UserID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User findById(int id) {
        String query = "SELECT UserID, Login, Password, Role FROM User WHERE UserID = ?";
        User user = null;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getInt("UserID"));
                    user.setLogin(resultSet.getString("Login"));
                    user.setPassword(resultSet.getString("Password"));
                    user.setRole(resultSet.getString("Role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT UserID, Login, Password, Role FROM User";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("UserID"));
                user.setLogin(resultSet.getString("Login"));
                user.setPassword(resultSet.getString("Password"));
                user.setRole(resultSet.getString("Role"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
