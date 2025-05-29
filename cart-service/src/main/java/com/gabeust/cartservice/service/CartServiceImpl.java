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
/**
 * Implementación del servicio para gestionar carritos de compra,
 * incluyendo creación, actualización, eliminación de ítems y checkout.
 */
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
    /**
     * Obtiene el carrito activo (no finalizado) para un usuario o crea uno nuevo si no existe.
     *
     * @param userId ID del usuario.
     * @return Carrito activo del usuario.
     */
    @Override
    public Cart getOrCreateCart(Long userId) {
        return  cartRepository.findByUserIdAndCheckedOutFalse(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
    }
    /**
     * Agrega o actualiza un ítem en el carrito activo del usuario.
     *
     * @param userId   ID del usuario.
     * @param wineId   ID del vino a agregar.
     * @param quantity Cantidad a agregar.
     * @return Carrito actualizado.
     */
    @Override
    public Cart addItemToCart(Long userId, Long wineId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        cartItemService.addOrUpdateItem(cart.getId(), wineId, quantity);

        return cartRepository.save(cart);
    }
    /**
     * Actualiza la cantidad de un ítem existente en el carrito del usuario.
     *
     * @param userId   ID del usuario.
     * @param itemId   ID del ítem a actualizar.
     * @param quantity Nueva cantidad.
     * @return Carrito actualizado.
     * @throws RuntimeException si el ítem no se encuentra en el carrito.
     */
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
    /**
     * Elimina un ítem del carrito del usuario.
     *
     * @param userId ID del usuario.
     * @param itemId ID del ítem a eliminar.
     */
    @Override
    public void removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);

        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cartRepository.save(cart);
    }
    /**
     * Obtiene el carrito activo del usuario.
     *
     * @param userId ID del usuario.
     * @return Carrito activo.
     */
    @Override
    public Cart getCart(Long userId) {
        return getOrCreateCart(userId);
    }
    /**
     * Realiza el checkout del carrito activo del usuario,
     * verifica el stock para cada ítem y descuenta el inventario.
     * Marca el carrito como finalizado y registra la fecha de checkout.
     *
     * @param userId ID del usuario.
     * @return Carrito finalizado.
     * @throws RuntimeException si el carrito está vacío o no hay stock suficiente.
     */
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
    /**
     * Obtiene el historial de órdenes finalizadas de un usuario.
     *
     * @param userId ID del usuario.
     * @return Lista de carritos finalizados (historial de órdenes).
     */
    public List<Cart> getOrderHistory(Long userId) {
        return cartRepository.findAllByUserIdAndCheckedOutTrue(userId);
    }
}
