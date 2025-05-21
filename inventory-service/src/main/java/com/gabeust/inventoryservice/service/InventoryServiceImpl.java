package com.gabeust.inventoryservice.service;

import com.gabeust.inventoryservice.dto.InventoryWithWineDTO;
import com.gabeust.inventoryservice.dto.WineDTO;
import com.gabeust.inventoryservice.entity.Inventory;
import com.gabeust.inventoryservice.entity.enums.MovementType;
import com.gabeust.inventoryservice.repository.InventoryRepository;
import com.gabeust.inventoryservice.service.client.WineClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WineClientService wineClientService;
    private final InventoryMovementService movementService;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, WineClientService wineClientService, InventoryMovementService movementService) {
        this.inventoryRepository = inventoryRepository;
        this.wineClientService = wineClientService;
        this.movementService = movementService;
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Optional<Inventory> findByWineId(Long wineId) {
        return inventoryRepository.findByWineId(wineId);
    }

    @Override
    public Inventory save(Inventory inventory) {
        try {
            wineClientService.getWineById(inventory.getWineId());
        } catch (WebClientResponseException.NotFound e) {
            throw new IllegalArgumentException("El vino con ID " + inventory.getWineId() + " no existe.");
        } catch (Exception e) {
            throw new IllegalStateException("Error al verificar el vino: " + e.getMessage(), e);
        }

        // Validar que no exista inventario para ese vino
        if (inventoryRepository.existsByWineId(inventory.getWineId())) {
            throw new IllegalStateException("Ya existe un inventario para el vino con ID: " + inventory.getWineId());
        }

        // Guardar inventario
        Inventory savedInventory = inventoryRepository.save(inventory);

        // Registrar movimiento solo si la cantidad es mayor a 0
        if (savedInventory.getQuantity() > 0) {
            movementService.registerMovement(
                    savedInventory.getWineId(),
                    MovementType.INCREASE,
                    savedInventory.getQuantity()
            );
        }

        return savedInventory;
    }

    @Override
    public void increaseStock(Long wineId, int amount) {
        Inventory inventory = inventoryRepository.findByWineId(wineId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for wine with ID: " + wineId));

        inventory.setQuantity(inventory.getQuantity() + amount);
        inventoryRepository.save(inventory);
        movementService.registerMovement(wineId, MovementType.INCREASE, amount);
    }

    @Override
    public void decreaseStock(Long wineId, int amount) {
        Inventory inventory = inventoryRepository.findByWineId(wineId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for wine with ID: " + wineId));

        int newQuantity = inventory.getQuantity() - amount;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock for wine with ID: " + wineId);
        }
        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);
        movementService.registerMovement(wineId, MovementType.DECREASE, amount);
    }

    @Override
    public boolean deleteByWineId(Long wineId) {
        Inventory inventory = inventoryRepository.findByWineId(wineId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for wine with ID: " + wineId));

        inventoryRepository.delete(inventory);
        return true;
    }
    public InventoryWithWineDTO findInventoryWithWineInfo(Long wineId) {
        Inventory inventory = inventoryRepository.findByWineId(wineId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for wine ID: " + wineId));

        WineDTO wine = wineClientService.getWineById(wineId);

        return new InventoryWithWineDTO(
                wineId,
                inventory.getQuantity(),
                inventory.getMinimumQuantity(),
                wine
        );
    }
}
