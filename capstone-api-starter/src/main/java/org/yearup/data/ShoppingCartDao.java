package org.yearup.data;

import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao {
    ShoppingCart getByUserId(int userId);
//    Product addProductToCart(int productId);
//    ShoppingCart removeProduct(int productId);

    // need to get the cart
    // need to add stuff to the cart
    // need to delete stuff from the cart
    // optional: update stuff in the cart with @PutMapping
}
