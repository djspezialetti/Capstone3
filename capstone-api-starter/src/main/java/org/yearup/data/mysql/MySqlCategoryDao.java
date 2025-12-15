package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {

    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String  query = "SELECT category_id, name, description " +
                "FROM categories;";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                Category category = mapRow(results);
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all categories", e);
        }
        return categories;
    }

    @Override
    public Category getById(int categoryId) {
        String  query = "SELECT category_id, name, description " +
                "FROM categories " +
                "WHERE category_id = ?;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, categoryId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapRow(results);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting category by ID", e);
        }
        return null;
    }

    @Override
    public Category create(Category category) {
        String  query = "INSERT INTO categories (category_id, name, description) " +
                "VALUES (?, ?, ?);";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, category.getCategoryId());
            statement.setString(2, category.getName());
            statement.setString(3, category.getDescription());

            statement.executeUpdate();

            try (ResultSet keys = statement.executeQuery()) {
                if (keys.next()) {
                    int category_id = keys.getInt(1);
                    return getById(category_id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating category...", e);
        }
        return null;
    }

    @Override
    public Category update(int categoryId, Category category) {
        String query = "UPDATE categories " +
                "SET category_id = ?, name = ?, description = ? " +
                "WHERE category_id = ?;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, category.getCategoryId());
            statement.setString(2, category.getName());
            statement.setString(3, category.getDescription());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                return null;
            }
            return getById(categoryId);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating category", e);
        }
    }

    @Override
    public boolean delete(int categoryId) {
        String query = "DELETE FROM categories " +
                "WHERE category_id = ?;";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, categoryId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category...", e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {
            {
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
            }
        };
        return category;
    }
}
