package com.gabeust.cartservice.service;

import com.gabeust.cartservice.entity.CartItem;

import java.util.List;

public interface CartItemService {
    CartItem findById(Long itemId);
    List<CartItem> findByCartId(Long cartId);
    CartItem addOrUpdateItem(Long cartId, Long wineId, Integer quantity);
    CartItem addItem(Long cartId, Long wineId, Integer quantity);
    CartItem updateQuantity(Long itemId, Integer quantity);
    void removeItem(Long itemId);
}
