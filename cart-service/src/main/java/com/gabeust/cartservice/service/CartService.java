package com.gabeust.cartservice.service;

import com.gabeust.cartservice.entity.Cart;

public interface CartService {

    Cart getOrCreateCart(Long userId);
    Cart addItemToCart(Long userId, Long wineId, Integer quantity);
    Cart updateItemQuantity(Long userId, Long itemId, Integer quantity);
    void removeItem(Long userId, Long itemId);
    Cart getCart(Long userId);
    Cart checkoutCart(Long userId);

}
