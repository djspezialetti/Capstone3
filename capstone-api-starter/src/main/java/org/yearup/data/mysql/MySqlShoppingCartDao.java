package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();

        String  query = "SELECT sc.user_id, sc.product_id, sc.quantity, " +
                "p.name, p.price, p.description, p.category_id, p.image_url " +
                "FROM shopping_cart AS sc " +
                "JOIN products AS p ON (p.product_id = sc.product_id) " +
                "WHERE user_id = ?;";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    ShoppingCartItem cartItem = mapRow(results);
                    shoppingCart.add(cartItem);
                    System.out.println("shopping cart items: " + shoppingCart);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting shopping cart by User ID", e);
        }
        return shoppingCart;
    }

    @Override
    public void addProduct(int userId, int productId) {
        String  query = "INSERT INTO shopping_cart (user_id, product_id, quantity) " +
                "VALUES ((SELECT user_id FROM users WHERE user_id = ?), ?, ?);";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, 1);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Product added to cart");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating category...", e);
        }
    }

//
//    @Override
//    public ShoppingCart removeProduct(int productId) {
//        return null;
//    }

    private ShoppingCartItem mapRow(ResultSet row) throws SQLException {
        int productId = row.getInt("sc.product_id");
        int quantity = row.getInt("sc.quantity");
        String name = row.getString("p.name");
        BigDecimal price = row.getBigDecimal("p.price");
        String description = row.getString("p.description");
        int categoryId = row.getInt("p.category_id");
        String imageUrl = row.getString("p.image_url");

        Product product = new Product();
        product.setProductId(productId);
        product.setPrice(price);
        product.setName(name);
        product.setDescription(description);
        product.setCategoryId(categoryId);
        product.setImageUrl(imageUrl);

        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setProduct(product);
        shoppingCartItem.setQuantity(quantity);

        return shoppingCartItem;
    }
}
