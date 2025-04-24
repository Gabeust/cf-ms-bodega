package com.gabeust.cartservice.controller;

import com.gabeust.cartservice.entity.CartItem;
import com.gabeust.cartservice.service.CartItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/cart-item")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/{id}")
    public CartItem getItem(@PathVariable Long id) {
        return cartItemService.findById(id);
    }

    @GetMapping("/cart/{cartId}")
    public List<CartItem> getItemsByCart(@PathVariable Long cartId) {
        return cartItemService.findByCartId(cartId);
    }

    @PostMapping
    public CartItem addItem(@RequestParam Long cartId,
                            @RequestParam Long wineId,
                            @RequestParam Integer quantity) {
        return cartItemService.addItem(cartId, wineId, quantity);
    }

    @PutMapping("/{itemId}")
    public CartItem updateQuantity(@PathVariable Long itemId,
                                   @RequestParam Integer quantity) {
        return cartItemService.updateQuantity(itemId, quantity);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        cartItemService.removeItem(itemId);
    }
}
