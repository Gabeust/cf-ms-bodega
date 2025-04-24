package com.gabeust.cartservice.service;

import com.gabeust.cartservice.entity.Cart;
import com.gabeust.cartservice.entity.CartItem;
import com.gabeust.cartservice.repository.CartRepository;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final CartItemService cartItemService;

    public CartServiceImpl(CartRepository cartRepository, CartItemService cartItemService) {
        this.cartRepository = cartRepository;
        this.cartItemService = cartItemService;
    }

    @Override
    public Cart getOrCreateCart(Long userId) {
        return  cartRepository.findByUserIdAndCheckedOutFalse(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public Cart addItemToCart(Long userId, Long wineId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        cartItemService.addOrUpdateItem(cart.getId(), wineId, quantity);

        return cartRepository.save(cart);
    }

    private Integer getWinePrice(Long wineId) {

        return 1000; // Simulado por ahora
    }

    @Override
    public Cart updateItemQuantity(Long userId, Long itemId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    @Override
    public void removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);

        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cartRepository.save(cart);
    }

    @Override
    public Cart getCart(Long userId) {
        return getOrCreateCart(userId);
    }

    @Override
    public Cart checkoutCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.setCheckedOut(true);
        return cartRepository.save(cart);
    }
}
