package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart theCart = new ShoppingCart();
        ShoppingCartItem cartItem = new ShoppingCartItem();
        List<ShoppingCartItem> shoppingCart = new ArrayList<>();

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
                    ShoppingCartItem cartItems = mapRow(results);
                    theCart.add(cartItems);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting shopping cart by User ID", e);
        }
        return theCart;
    }

//    @Override
//    public ShoppingCartItem addProductToCart(int productId) {
//        ShoppingCart shoppingCart = new ShoppingCart();
//        List<ShoppingCartItem> cartItem = new ArrayList<>();
//
//        String  query = "INSERT INTO shopping_cart (user_id, product_id, quantity) " +
//                "VALUES (?, ?, ?);";
//        try (Connection connection = getConnection();
//             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
//            //statement.setInt(1, product.getProductId());
//            statement.setInt(2, productId);
//            statement.setInt(3, 1);
//
//            int rowsAffected = statement.executeUpdate();
//
//            ResultSet rs = statement.getGeneratedKeys();
//            ShoppingCartItem item = mapRow(rs);
//            System.out.println("Shopping Cart Item after product is added: " + item);
//
//            if (rs.next()) cartItem.add(item);
//            shoppingCart.add(item);
//            System.out.println("Shopping cart after product has been added: " + shoppingCart);
//
//            if (rowsAffected > 0) {
//                System.out.println("Product added to cart");
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Error creating category...", e);
//        }
//        return shoppingCart;
//    }

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

        System.out.println(imageUrl);

        Product product = new Product();
        product.setProductId(productId);
        product.setPrice(price);
        product.setName(name);
        product.setDescription(description);
        product.setCategoryId(categoryId);
        product.setImageUrl(imageUrl);

        System.out.println(product);

        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setProduct(product);
        shoppingCartItem.setQuantity(quantity);

        return shoppingCartItem;
    }
}
