package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao {
    ShoppingCart getByUserId(int userId);
    void addProduct(int userId, int productId);
//    ShoppingCart removeProduct(int productId);

    // need to delete stuff from the cart
    // optional: update stuff in the cart with @PutMapping
}
