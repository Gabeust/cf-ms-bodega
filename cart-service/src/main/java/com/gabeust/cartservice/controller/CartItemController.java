package com.gabeust.cartservice.controller;

import com.gabeust.cartservice.entity.CartItem;
import com.gabeust.cartservice.service.CartItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador REST para la gestión de ítems dentro del carrito.
 *
 * Permite obtener, agregar, actualizar y eliminar ítems del carrito.
 */
@RestController
@RequestMapping("api/v1/cart-item")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }
    /**
     * Obtiene un ítem del carrito por su ID.
     *
     * @param id ID del ítem
     * @return ítem del carrito
     */
    @GetMapping("/{id}")
    public CartItem getItem(@PathVariable Long id) {
        return cartItemService.findById(id);
    }
    /**
     * Obtiene todos los ítems pertenecientes a un carrito específico.
     *
     * @param cartId ID del carrito
     * @return lista de ítems del carrito
     */
    @GetMapping("/cart/{cartId}")
    public List<CartItem> getItemsByCart(@PathVariable Long cartId) {
        return cartItemService.findByCartId(cartId);
    }
    /**
     * Agrega un nuevo ítem a un carrito.
     *
     * @param cartId ID del carrito
     * @param wineId ID del vino a agregar
     * @param quantity cantidad a agregar
     * @return ítem agregado
     */
    @PostMapping
    public CartItem addItem(@RequestParam Long cartId,
                            @RequestParam Long wineId,
                            @RequestParam Integer quantity) {
        return cartItemService.addItem(cartId, wineId, quantity);
    }
    /**
     * Actualiza la cantidad de un ítem específico en el carrito.
     *
     * @param itemId ID del ítem a actualizar
     * @param quantity nueva cantidad
     * @return ítem actualizado
     */
    @PutMapping("/{itemId}")
    public CartItem updateQuantity(@PathVariable Long itemId,
                                   @RequestParam Integer quantity) {
        return cartItemService.updateQuantity(itemId, quantity);
    }
    /**
     * Elimina un ítem del carrito por su ID.
     *
     * @param itemId ID del ítem a eliminar
     */
    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        cartItemService.removeItem(itemId);
    }
}
