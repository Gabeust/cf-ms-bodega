package com.gabeust.cartservice.controller;

import com.gabeust.cartservice.entity.Cart;
import com.gabeust.cartservice.service.CartServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador REST para la gestión del carrito de compras.
 *
 * Proporciona endpoints para agregar, actualizar, eliminar ítems,
 * obtener el carrito actual, realizar el checkout y consultar el historial de pedidos.
 */
@RestController
@RequestMapping("api/v1/cart")
public class CartController {

    private final CartServiceImpl cartService;

    public CartController(CartServiceImpl cartService) {
        this.cartService = cartService;
    }
    /**
     * Agrega un ítem al carrito de un usuario.
     *
     * @param userId ID del usuario
     * @param wineId ID del vino a agregar
     * @param quantity cantidad a agregar
     * @return carrito actualizado
     */
    @PostMapping("/{userId}/add")
    public Cart addItem(@PathVariable Long userId,
                        @RequestParam Long wineId,
                        @RequestParam Integer quantity) {
        return cartService.addItemToCart(userId, wineId, quantity);
    }

    /**
     * Actualiza la cantidad de un ítem específico en el carrito de un usuario.
     *
     * @param userId ID del usuario
     * @param itemId ID del ítem a actualizar
     * @param quantity nueva cantidad
     * @return carrito actualizado
     */
    @PutMapping("/{userId}/update")
    public Cart updateItem(@PathVariable Long userId,
                           @RequestParam Long itemId,
                           @RequestParam Integer quantity) {
        return cartService.updateItemQuantity(userId, itemId, quantity);
    }
    /**
     * Elimina un ítem del carrito de un usuario.
     *
     * @param userId ID del usuario
     * @param itemId ID del ítem a eliminar
     */
    @DeleteMapping("/{userId}/remove")
    public void removeItem(@PathVariable Long userId,
                           @RequestParam Long itemId) {
        cartService.removeItem(userId, itemId);
    }
    /**
     * Obtiene el carrito actual de un usuario.
     *
     * @param userId ID del usuario
     * @return carrito actual
     */
    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getCart(userId);
    }
    /**
     * Realiza el proceso de checkout del carrito de un usuario.
     *
     * @param userId ID del usuario
     * @return respuesta HTTP con el carrito finalizado o mensaje de error
     */
    @PostMapping("/{userId}/checkout")
    public ResponseEntity<?> checkout(@PathVariable Long userId) {
        try {
            Cart checkedOutCart = cartService.checkoutCart(userId);
            return ResponseEntity.ok(checkedOutCart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    /**
     * Obtiene el historial de pedidos (carritos finalizados) de un usuario.
     *
     * @param userId ID del usuario
     * @return lista de carritos finalizados
     */
    @GetMapping("/{userId}/history")
    public List<Cart> getOrderHistory(@PathVariable Long userId) {
        return cartService.getOrderHistory(userId);
    }

}
