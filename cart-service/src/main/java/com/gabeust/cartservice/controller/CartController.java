package com.gabeust.cartservice.controller;

import com.gabeust.cartservice.entity.Cart;
import com.gabeust.cartservice.service.CartServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/cart")
public class CartController {

    private final CartServiceImpl cartService;

    public CartController(CartServiceImpl cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{userId}/add")
    public Cart addItem(@PathVariable Long userId,
                        @RequestParam Long wineId,
                        @RequestParam Integer quantity) {
        return cartService.addItemToCart(userId, wineId, quantity);
    }
    @PutMapping("/{userId}/update")
    public Cart updateItem(@PathVariable Long userId,
                           @RequestParam Long itemId,
                           @RequestParam Integer quantity) {
        return cartService.updateItemQuantity(userId, itemId, quantity);
    }

    @DeleteMapping("/{userId}/remove")
    public void removeItem(@PathVariable Long userId,
                           @RequestParam Long itemId) {
        cartService.removeItem(userId, itemId);
    }

    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getCart(userId);
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<?> checkout(@PathVariable Long userId) {
        try {
            Cart checkedOutCart = cartService.checkoutCart(userId);
            return ResponseEntity.ok(checkedOutCart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{userId}/history")
    public List<Cart> getOrderHistory(@PathVariable Long userId) {
        return cartService.getOrderHistory(userId);
    }

}
