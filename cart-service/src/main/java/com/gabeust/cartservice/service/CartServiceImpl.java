package com.gabeust.cartservice.service;

import com.gabeust.cartservice.dto.InventoryDTO;
import com.gabeust.cartservice.entity.Cart;
import com.gabeust.cartservice.entity.CartItem;
import com.gabeust.cartservice.repository.CartRepository;
import com.gabeust.cartservice.service.client.InventoryClientService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final CartItemService cartItemService;
    private final InventoryClientService inventoryClientService;

    public CartServiceImpl(CartRepository cartRepository, CartItemService cartItemService, InventoryClientService inventoryClientService) {
        this.cartRepository = cartRepository;
        this.cartItemService = cartItemService;
        this.inventoryClientService = inventoryClientService;
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
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("The cart is empty");
        }

        // Agrupa por wineId y suma cantidades
        Map<Long, Integer> wineIdToQuantity = new HashMap<>();
        for (CartItem item : cart.getItems()) {
            wineIdToQuantity.merge(item.getWineId(), item.getQuantity(), Integer::sum);
        }

        // Verifica stock y descuenta
        for (Map.Entry<Long, Integer> entry : wineIdToQuantity.entrySet()) {
            Long wineId = entry.getKey();
            int totalQuantity = entry.getValue();

            // Verifica stock actual
            InventoryDTO inventory = inventoryClientService.getInventoryByWineId(wineId);
            if (inventory.quantity() < totalQuantity) {
                throw new RuntimeException("Insufficient stock for wine with ID: " + wineId);
            }

            // Descuenta stock
            inventoryClientService.decreaseStock(wineId, totalQuantity);
        }

        cart.setCheckedOut(true);
        cart.setCheckoutDate(LocalDate.now());
        return cartRepository.save(cart);
    }

    public List<Cart> getOrderHistory(Long userId) {
        return cartRepository.findAllByUserIdAndCheckedOutTrue(userId);
    }
}
