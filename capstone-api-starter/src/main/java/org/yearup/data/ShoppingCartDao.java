package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao {
    ShoppingCart getByUserId(int userId);
    //ShoppingCart

    // need to get the cart
    // need to add stuff to the cart
    // need to delete stuff from the cart
    // optional: update stuff in the cart with @PutMapping
}
