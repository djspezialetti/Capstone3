package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        String  query = "SELECT user_id, product_id, quantity " +
                "FROM shopping_cart " +
                "WHERE user_id = ?;";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            System.out.println("userid in query: " + userId);
//            System.out.println("productid in query: ");
//            System.out.println("userid in query: " + userId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    System.out.println("Results in query " + results);
                    return mapRow(results);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting shopping cart by User ID", e);
        }
        return null;
    }

//    @Override
//    public Product addProductToCart(Product product) {
//        String  query = "INSERT INTO shopping_cart (user_id, product_id, quantity) " +
//                "VALUES (?, ?, ?);";
//        try (Connection connection = getConnection();
//             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
//            statement.setInt(1, product.getProductId());
//            statement.setInt(2, product.getProductId());
//            statement.setInt(3, product.);
//
//            statement.executeUpdate();
//
//            try (ResultSet keys = statement.getGeneratedKeys()) {
//                if (keys.next()) {
//                    int user_id = keys.getInt(1);
//                    return getByUserId(user_id);
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Error creating category...", e);
//        }
//        return null;
//    }
//
//    @Override
//    public ShoppingCart removeProduct(int productId) {
//        return null;
//    }

    private ShoppingCart mapRow(ResultSet row) throws SQLException {
        int userId = row.getInt("user_id");
        int productId = row.getInt("product_id");
        int quantity = row.getInt("quantity");
        System.out.println("userId in mapRow: " + userId);
        System.out.println("productId in mapRow: " + productId);
        System.out.println("quantity in mapRow: " + quantity);

        Product product;
        ProductDao productDao = null;

        product = productDao.getById(productId);
        System.out.println("product by ID after the dao: " + product);

        ShoppingCartItem cartItem = new ShoppingCartItem();
        cartItem.setProduct(product);
        System.out.println("shopping cart after product is added: " + cartItem);

        Map<Integer, ShoppingCartItem> shoppingCartItem = new HashMap<>();
        shoppingCartItem.put(userId, cartItem);

        ShoppingCart shoppingCart = new ShoppingCart() {
            {
                setItems(shoppingCartItem);
            }
        };
        return shoppingCart;
    }
}
