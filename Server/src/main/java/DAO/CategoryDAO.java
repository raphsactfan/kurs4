package DAO;

import Entities.Category;
import Interfaces.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO implements DAO<Category> {
    private final Connection connection;

    public CategoryDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Category findById(int id) {
        try {
            String query = "SELECT * FROM Category WHERE CategoryID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getInt("CategoryID"));
                category.setName(resultSet.getString("Name"));
                return category;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        try {
            String query = "SELECT * FROM Category";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getInt("CategoryID"));
                category.setName(resultSet.getString("Name"));
                categories.add(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public void save(Category category) {
        try {
            String query = "INSERT INTO Category (Name) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, category.getName());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Category category) {
        try {
            String query = "UPDATE Category SET Name = ? WHERE CategoryID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, category.getName());
            statement.setInt(2, category.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Category category) {
        try {
            String query = "DELETE FROM Category WHERE CategoryID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, category.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int countProductsInCategory(int categoryId) {
        String query = "SELECT COUNT(*) FROM Product WHERE CategoryID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, categoryId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Приведение к типу int для устранения ошибки
                    return resultSet.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
