package com.gabeust.cartservice.service;

import com.gabeust.cartservice.entity.Cart;
import com.gabeust.cartservice.entity.CartItem;
import com.gabeust.cartservice.repository.CartItemRepository;
import com.gabeust.cartservice.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService{

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, CartRepository cartRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public CartItem findById(Long itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    public List<CartItem> findByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    @Override
    public CartItem addItem(Long cartId, Long wineId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setWineId(wineId);
        item.setQuantity(quantity);
        item.setPriceAtTime(1000); // Simular consulta a WineService

        return cartItemRepository.save(item);
    }

    @Override
    public CartItem updateQuantity(Long itemId, Integer quantity) {
        CartItem item = findById(itemId);
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    @Override
    public void removeItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    @Override
    public CartItem addOrUpdateItem(Long cartId, Long wineId, Integer quantity) {
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndWineId(cartId, wineId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartItemRepository.save(item);
        } else {
            return addItem(cartId, wineId, quantity);
        }
    }
}
