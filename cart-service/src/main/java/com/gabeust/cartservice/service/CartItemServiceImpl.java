package com.gabeust.cartservice.service;

import com.gabeust.cartservice.dto.InventoryDTO;
import com.gabeust.cartservice.dto.WineDTO;
import com.gabeust.cartservice.entity.Cart;
import com.gabeust.cartservice.entity.CartItem;
import com.gabeust.cartservice.repository.CartItemRepository;
import com.gabeust.cartservice.repository.CartRepository;
import com.gabeust.cartservice.service.client.InventoryClientService;
import com.gabeust.cartservice.service.client.WineClientService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/**
 * Implementación del servicio para gestionar los ítems del carrito de compras.
 * Permite buscar, agregar, actualizar cantidades y eliminar ítems del carrito,
 * validando stock disponible consultando servicios externos de vino e inventario.
 */
@Service
public class CartItemServiceImpl implements CartItemService{

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final WineClientService wineClientService;
    private final InventoryClientService inventoryClientService;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, CartRepository cartRepository, WineClientService wineClientService, InventoryClientService inventoryClientService) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.wineClientService = wineClientService;
        this.inventoryClientService = inventoryClientService;
    }
    /**
     * Busca un ítem en el carrito por su ID.
     *
     * @param itemId ID del ítem a buscar.
     * @return El CartItem correspondiente si existe.
     * @throws RuntimeException si no se encuentra el ítem.
     */
    @Override
    public CartItem findById(Long itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }
    /**
     * Obtiene la lista de ítems de un carrito específico.
     *
     * @param cartId ID del carrito.
     * @return Lista de CartItem pertenecientes al carrito.
     */
    @Override
    public List<CartItem> findByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }
    /**
     * Agrega un nuevo ítem al carrito especificado con la cantidad indicada.
     * Obtiene el vino mediante WineClientService para obtener detalles si es necesario.
     *
     * @param cartId  ID del carrito donde se agregará el ítem.
     * @param wineId  ID del vino a agregar.
     * @param quantity Cantidad del vino a agregar.
     * @return El CartItem recién creado y persistido.
     * @throws RuntimeException si no se encuentra el carrito con el ID dado.
     */
    public CartItem addItem(Long cartId, Long wineId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setWineId(wineId);
        item.setQuantity(quantity);
        // Obtiene el precio del vino directamente de WineService
        WineDTO wine = wineClientService.getWineById(wineId);

        return cartItemRepository.save(item);
    }

    /**
     * Actualiza la cantidad de un ítem existente en el carrito.
     * Válida que la nueva cantidad no supere el stock disponible.
     *
     * @param itemId   ID del ítem a actualizar.
     * @param quantity Nueva cantidad.
     * @return El CartItem actualizado.
     * @throws RuntimeException si no hay stock suficiente o no se encuentra el ítem.
     */
    @Override
    public CartItem updateQuantity(Long itemId, Integer quantity) {
        CartItem item = findById(itemId);
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }
    /**
     * Elimina un ítem del carrito por su ID.
     *
     * @param itemId ID del ítem a eliminar.
     */
    @Override
    public void removeItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    /**
     * Agrega un ítem al carrito o actualiza la cantidad si ya existe.
     * Válida que haya stock suficiente consultando el servicio de inventario.
     *
     * @param cartId  ID del carrito donde agregar o actualizar el ítem.
     * @param wineId  ID del vino a agregar.
     * @param quantity Cantidad a agregar.
     * @return El CartItem creado o actualizado.
     * @throws RuntimeException si no hay stock suficiente o no se encuentra el carrito.
     */
    @Transactional
    @Override
    public CartItem addOrUpdateItem(Long cartId, Long wineId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Verifica si existe el vino
        WineDTO wine;
        InventoryDTO inventory;

        try {
            wine = wineClientService.getWineById(wineId);
            inventory = inventoryClientService.getInventoryByWineId(wineId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch wine or inventory data", e);
        }

        // Válida que haya suficiente stock DISPONIBLE (sin descontar aún)
        if (inventory.quantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        // Busca si ya existe ese vino en el carrito
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndWineId(cartId, wineId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setWineId(wineId);
            item.setQuantity(quantity);
            return cartItemRepository.save(item);
        }
    }

}
