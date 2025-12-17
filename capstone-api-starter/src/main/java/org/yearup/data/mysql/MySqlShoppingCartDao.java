package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapRow(results);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting shopping cart by User ID", e);
        }
        return null;
    }

//    private ShoppingCart mapRow(ResultSet row) throws SQLException {
//        int userId = row.getInt("user_id");
//        int productId = row.getInt("product_id");
//        int quantity = row.getInt("quantity");
//
//        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
//        shoppingCartItem.setProduct(productId);
//
//        Map<Integer, ShoppingCartItem> shoppingCartItem = new HashMap<>();
//        shoppingCartItem.put(productId, quantity);
//
//        ShoppingCart shoppingCart = new ShoppingCart() {
//            {
//                getByUserId(userId);
//                setItems(shoppingCartItem);
//            }
//        };
//        return shoppingCart;
//    }
}
