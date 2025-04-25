package com.gabeust.cartservice.service;

import com.gabeust.cartservice.dto.InventoryDTO;
import com.gabeust.cartservice.dto.WineDTO;
import com.gabeust.cartservice.entity.Cart;
import com.gabeust.cartservice.entity.CartItem;
import com.gabeust.cartservice.repository.CartItemRepository;
import com.gabeust.cartservice.repository.CartRepository;
import com.gabeust.cartservice.service.client.InventoryClientService;
import com.gabeust.cartservice.service.client.WineClientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public CartItem findById(Long itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    public List<CartItem> findByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    public CartItem addItem(Long cartId, Long wineId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setWineId(wineId);
        item.setQuantity(quantity);
        // Obtener el precio del vino directamente de WineService
        WineDTO wine = wineClientService.getWineById(wineId);

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

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        WineDTO wine;
        InventoryDTO inventory;

        try {
            wine = wineClientService.getWineById(wineId);
            inventory = inventoryClientService.getInventoryByWineId(wineId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener información del vino o inventario", e);
        }

        if (inventory.quantity() < quantity) {
            throw new RuntimeException("Stock insuficiente");
        }

        try {
            inventoryClientService.decreaseStock(wineId, quantity);
        } catch (Exception e) {
            throw new RuntimeException("Error al disminuir el stock", e);
        }

        // Buscar si ya existe ese vino en el carrito
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndWineId(cartId, wineId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart); // Seteás el objeto Cart, no el ID
            item.setWineId(wineId);
            item.setQuantity(quantity);
            return cartItemRepository.save(item);
        }
    }
}
